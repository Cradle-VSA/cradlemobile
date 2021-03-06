package com.cradle.neptune.dagger

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.cradle.neptune.database.CradleDatabase
import com.cradle.neptune.database.HealthFacilityDaoAccess
import com.cradle.neptune.database.PatientDaoAccess
import com.cradle.neptune.database.ReadingDaoAccess
import com.cradle.neptune.manager.HealthCentreManager
import com.cradle.neptune.net.Http
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Provide the singleton objects for data access
 * Source: https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2#instantiating-the-component
 */
@Module
class DataModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application?): CradleDatabase {
        return Room.databaseBuilder(
            application!!,
            CradleDatabase::class.java,
            "room-readingDB"
        ).build()
    }

    @Provides
    @Singleton
    fun providePatientDao(database: CradleDatabase): PatientDaoAccess =
        database.patientDaoAccess()

    @Provides
    @Singleton
    fun provideReadingDao(database: CradleDatabase): ReadingDaoAccess =
        database.readingDaoAccess()

    @Provides
    @Singleton
    fun provideHealthFacilityDao(database: CradleDatabase): HealthFacilityDaoAccess =
        database.healthFacilityDaoAccess()

    @Provides
    @Singleton
    fun provideHealthCentreService(database: CradleDatabase?): HealthCentreManager {
        return HealthCentreManager(database!!)
    }

    @Provides
    @Singleton
    fun providesSharedPreferences(application: Application?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    @Singleton
    fun providesHttp(): Http = Http()
}
