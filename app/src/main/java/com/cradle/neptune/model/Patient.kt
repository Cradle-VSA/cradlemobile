package com.cradle.neptune.model

import java.util.Locale

// TODO: Figure out which of these fields must be optional and which are never
//  used at all.

// TODO: Remove default constructor, we should never be able to instantiate
//  partial objects.

/**
 * Holds information about a patient.
 *
 * @property id The unique identifier for this patient.
 * @property name This patient's name or initials.
 * @property dob This patient's date of birth (if known).
 * @property age This patient's age; used if [dob] is not known.
 * @property gestationalAge The gestational age of this patient if applicable.
 * @property sex This patient's sex.
 * @property isPregnant Whether this patient is pregnant or not.
 * // @property needsAssessment Whether this patient needs assessment or not.
 * @property zone The zone in which this patient lives.
 * @property villageNumber The number of the village in which this patient lives.
 * @property drugHistoryList A list of drug history for the patient.
 * @property medicalHistoryList A list of medical history for the patient.
 */
data class Patient(
    var id: String = "",
    var name: String = "",
    var dob: String? = null,
    var age: Int? = null,
    var gestationalAge: GestationalAge? = null,
    var sex: Sex = Sex.OTHER,
    var isPregnant: Boolean = false,
    // var needsAssessment: Boolean = false,
    var zone: String? = null,
    var villageNumber: String? = null,
    var drugHistoryList: List<String> = emptyList(),
    var medicalHistoryList: List<String> = emptyList()
) : Marshal<JsonObject> {

    /**
     * Constructs a [JsonObject] from this object.
     */
    override fun marshal(): JsonObject = with(JsonObject()) {
        if (gestationalAge != null) {
            union(gestationalAge!!)
        }

        put(PatientField.ID, id)
        put(PatientField.NAME, name)
        put(PatientField.DOB, dob)
        put(PatientField.SEX, sex.name)
        put(PatientField.IS_PREGNANT, isPregnant)
        // put(PatientField.NEEDS_ASSESSMENT, needsAssessment)
        put(PatientField.ZONE, zone)
        put(PatientField.VILLAGE_NUMBER, villageNumber)

        // FIXME: Original implementation does not handle drug or medical history?
    }

    companion object : Unmarshal<Patient, JsonObject> {
        /**
         * Constructs a [Patient] object from a [JsonObject].
         *
         * @param data The JSON data to unmarshal.
         * @return A new patient.
         *
         * @throws JsonException If any of the required patient fields are
         * missing from [data].
         *
         * @throws IllegalArgumentException If the value for an enum field
         * cannot be converted into said enum.
         */
        override fun unmarshal(data: JsonObject): Patient = Patient().apply {
            id = data.stringField(PatientField.ID)
            name = data.stringField(PatientField.NAME)
            dob = data.optStringField(PatientField.DOB)
            age = try {
                data.intField(PatientField.AGE)
            } catch (e: JsonException) {
                null
            }
            gestationalAge = maybeUnmarshal(GestationalAge, data)
            sex = data.mapField(PatientField.SEX, Sex::valueOf)
            isPregnant = data.booleanField(PatientField.IS_PREGNANT)
            // needsAssessment = data.booleanField(PatientField.NEEDS_ASSESSMENT)
            zone = data.stringField(PatientField.ZONE)
            villageNumber = data.stringField(PatientField.VILLAGE_NUMBER)

            val stringToList: (String) -> List<String> = {
                if (it.toLowerCase(Locale.getDefault()) == "null") {
                    emptyList()
                } else {
                    listOf(it)
                }
            }

            drugHistoryList = data.mapField(PatientField.DRUG_HISTORY, stringToList)
            medicalHistoryList = data.mapField(PatientField.MEDICAL_HISTORY, stringToList)
        }
    }
}

/**
 * The sex of a patient.
 */
enum class Sex {
    MALE, FEMALE, OTHER
}

sealed class PatientAge

/**
 * Holds data about the gestational age of a patient.
 *
 * Instead of storing both the unit type and value in the patient, we abstract
 * the concept of a gestational age into an abstract data type. Implementors
 * are responsible for marshalling to JSON and the base class is responsible
 * for unmarshalling.
 *
 * With regards to marshalling, care is taken to ensure that the new data
 * format conforms to the legacy API. This means that, when marshalling a
 * [Patient] object, one should union the gestational age JSON object with
 * the patient's one.
 *
 * @see GestationalAgeWeeks
 * @see GestationalAgeMonths
 */
sealed class GestationalAge(val value: Int) : Marshal<JsonObject> {
    /**
     * The age in weeks and days.
     *
     * Implementors must be able to convert whatever internal value they store
     * into this format.
     */
    abstract val age: WeeksAndDays

    companion object : Unmarshal<GestationalAge, JsonObject> {

        // These need to be marked static so we can share them with implementors.
        @JvmStatic protected val UNIT_VALUE_WEEKS =  "GESTATIONAL_AGE_UNITS_WEEKS"
        @JvmStatic protected val UNIT_VALUE_MONTHS =  "GESTATIONAL_AGE_UNITS_MONTHS"

        /**
         * Constructs a [GestationalAge] variant from a [JsonObject].
         *
         * @throws JsonException if any of the required fields are missing or
         * if the value for the gestational age unit field is invalid.
         */
        override fun unmarshal(data: JsonObject): GestationalAge {
            val units = data.stringField(PatientField.GESTATIONAL_AGE_UNIT)
            val value = data.stringField(PatientField.GESTATIONAL_AGE_VALUE).toInt()
            return when (units) {
                UNIT_VALUE_WEEKS -> GestationalAgeWeeks(value)
                UNIT_VALUE_MONTHS -> GestationalAgeMonths(value)
                else -> throw JsonException("invalid value for ${PatientField.GESTATIONAL_AGE_UNIT.text}")
            }
        }
    }
}

/**
 * Variant of [GestationalAge] which stores age in number of weeks.
 */
class GestationalAgeWeeks(weeks: Int) : GestationalAge(weeks) {
    override val age = WeeksAndDays.weeks(value)

    override fun marshal(): JsonObject = with(JsonObject()) {
        put(PatientField.GESTATIONAL_AGE_VALUE, value)
        put(PatientField.GESTATIONAL_AGE_UNIT, UNIT_VALUE_WEEKS)
    }
}

/**
 * Variant of [GestationalAge] which stores age in number of months.
 */
class GestationalAgeMonths(months: Int) : GestationalAge(months) {
    override val age = WeeksAndDays.months(value)

    override fun marshal(): JsonObject = with(JsonObject()) {
        put(PatientField.GESTATIONAL_AGE_VALUE, value)
        put(PatientField.GESTATIONAL_AGE_UNIT, UNIT_VALUE_MONTHS)
    }
}

/**
 * A temporal duration expressed in weeks and days.
 */
data class WeeksAndDays(val weeks: Int, val days: Int) {
    companion object {
        private const val DAYS_PER_MONTH = 30
        private const val DAYS_PER_WEEK = 7

        fun weeks(weeks: Int) = WeeksAndDays(weeks, 0)

        fun months(months: Int): WeeksAndDays {
            val days = DAYS_PER_MONTH * months
            return WeeksAndDays(days / DAYS_PER_WEEK, days % DAYS_PER_WEEK)
        }
    }
}

/**
 * The collection of JSON fields which make up a [Patient] object.
 *
 * These fields are defined here to ensure that the marshal and unmarshal
 * methods use the same field names.
 */
private enum class PatientField(override val text: String) : Field {
    ID("patientId"),
    NAME("patientName"),
    DOB("dob"),
    AGE("AGE"),
    GESTATIONAL_AGE_UNIT("gestationalAgeUnit"),
    GESTATIONAL_AGE_VALUE("gestationalAgeValue"),
    SEX("patientSex"),
    IS_PREGNANT("isPregnant"),
    NEEDS_ASSESSMENT("needsAssessment"),
    ZONE("zone"),
    VILLAGE_NUMBER("villageNumber"),
    DRUG_HISTORY("drugHistory"),
    MEDICAL_HISTORY("medicalHistory"),
}
