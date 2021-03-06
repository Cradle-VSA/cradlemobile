package com.cradle.neptune.manager

import androidx.lifecycle.LiveData
import com.cradle.neptune.database.CradleDatabase
import com.cradle.neptune.model.HealthFacility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *  * manager to interact with the [HealthFacility] table
 *Added [suspend] function so that there is compile time error when inserting on DB through
 * main thread rather than run time error
 */
@Suppress("RedundantSuspendModifier")
class HealthCentreManager(private val database: CradleDatabase) {

    private val dao get() = database.healthFacilityDaoAccess()

    /**
     * get a [HealthFacility] by id
     */
    suspend fun getById(id: String) = dao.getHealthFacilityById(id)

    /**
     * get all the [HealthFacility] selected by the current user.
     */
    suspend fun getAllSelectedByUser() = dao.allUserSelectedHealthFacilities

    /**
     * TODO: once all the java classes calling this method are turned into Kotlin,
     * remove this function and call the corressponding method.
     * This is only for legacy java code still calling this function.
     */
    @Deprecated("Please avoid using this function in Kotlin files.")
    fun getAllSelectedByUserBlocking() = runBlocking { getAllSelectedByUser() }

    /**
     * add a single health facility
     */
    fun add(facility: HealthFacility) = GlobalScope.launch(Dispatchers.IO) { dao.insert(facility) }

    /**
     * Add all the health facilities
     */
    fun addAll(facilities: List<HealthFacility>) =
        GlobalScope.launch(Dispatchers.IO) { dao.insertAll(facilities) }

    /**
     * update a single Health Facility
     */
    fun update(facility: HealthFacility) = GlobalScope.launch { dao.update(facility) }

    /**
     * returns a live list of the facilities
     */
    val getLiveList: LiveData<List<HealthFacility>> = dao.allFacilitiesLiveData

    /**
     * delete all [HealthFacility] from the DB
     */
    suspend fun deleteAll() = dao.deleteAll()
}
