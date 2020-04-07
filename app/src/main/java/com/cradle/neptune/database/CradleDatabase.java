package com.cradle.neptune.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ReadingEntity.class, HealthCareFacilityEntity.class}, version = 1, exportSchema = false)
public abstract class CradleDatabase extends RoomDatabase {
    public abstract ReadingDaoAccess daoAccess();
}
