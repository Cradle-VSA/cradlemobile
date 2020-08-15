package com.cradle.neptune.manager

import android.util.Log
import com.cradle.neptune.model.Patient
import com.cradle.neptune.model.PatientAndReadings
import com.cradle.neptune.model.Reading
import com.cradle.neptune.network.Api
import com.cradle.neptune.network.Failure
import com.cradle.neptune.network.NetworkResult
import com.cradle.neptune.network.map
import javax.inject.Inject

private const val HTTP_NOT_FOUND = 404

/**
 * Manages uploading referrals via HTTP.
 */
class ReferralUploadManger @Inject constructor(private val api: Api) {

    /**
     * Attempts to upload a referral to the server.
     *
     * The referral it self is actually nested within [reading], but we can't
     * just upload that to the server because the reading (and maybe the
     * patient) don't yet exist up there. This method takes care of figuring
     * out what additional data needs to be uploaded along with the referral.
     *
     * @param patient the patient being referred
     * @param reading the reading containing the referral
     * @throws IllegalArgumentException if [reading] does not contain a referral
     * @return result of the network request
     */
    suspend fun uploadReferralViaWeb(
        patient: Patient,
        reading: Reading
    ): NetworkResult<PatientAndReadings> {
        if (reading.referral == null) {
            throw IllegalArgumentException("reading must contain a nested referral")
        }

        // First check to see if the patient exists. We don't have an explicit
        // API for this so we use the response code of the get patient info
        // API to determine whether the patient exists or not.
        val patientCheckResult = api.getPatientInfo(patient.id)
        val patientExists = if (patientCheckResult is Failure) {
            val err = patientCheckResult.value
            if (err.networkResponse == null) {
                Log.e(this::class.simpleName, "Patient check failed with no response")
                return Failure(err)
            } else if (err.networkResponse.statusCode != HTTP_NOT_FOUND) {
                Log.e(
                    this::class.simpleName,
                    "Patient check failed with non 404 error, aborting upload"
                )
                return Failure(err)
            }
            false
        } else {
            true
        }

        // If the patient exists we only need to upload the reading, if not
        // then we need to upload the whole patient as well.
        return if (patientExists) {
            api.postReading(reading).map { PatientAndReadings(patient, listOf(it)) }
        } else {
            api.postPatient(PatientAndReadings(patient, listOf(reading)))
        }
    }
}
