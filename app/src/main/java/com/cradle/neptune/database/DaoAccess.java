package com.cradle.neptune.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cradle.neptune.model.Reading;

import java.util.List;

@Dao
public interface DaoAccess  {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReading(ReadingEntity readingEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ReadingEntity> readingEntities);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(ReadingEntity readingEntity);

    @Delete
    void delete(ReadingEntity readingEntity);

    @Query("SELECT * FROM ReadingEntity")
    List<ReadingEntity> getAllReadingEntities();

    @Query("SELECT * FROM readingentity WHERE readingId LIKE :readinId LIMIT 1")
    ReadingEntity getReadingById(String readinId);

    @Query("SELECT * FROM readingentity WHERE patientId LIKE :patientID")
    List<ReadingEntity> getAllReadingByPatientId(String patientID);

    @Query("DELETE FROM ReadingEntity")
    void deleteAllReading();



}
