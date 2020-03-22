package com.cradle.neptune.database;

import android.content.Context;

import com.cradle.neptune.model.Reading;
import com.cradle.neptune.model.ReadingManager;
import com.cradle.neptune.utilitiles.GsonUtil;
import com.cradle.neptune.utilitiles.Util;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoomReadingManager implements ReadingManager {
    private ReadingEntitiesDatabase readingEntitiesDatabase;

    public RoomReadingManager(ReadingEntitiesDatabase readingEntitiesDatabase) {
        this.readingEntitiesDatabase = readingEntitiesDatabase;
    }

    @Override
    public void addNewReading(Context context, Reading reading) {
        // add the new reading into the DB.
        reading.dateLastSaved = ZonedDateTime.now();
        if (reading.readingId == null || reading.readingId.equals("") ||
                reading.readingId.toLowerCase().equals("null")) {
            reading.readingId = UUID.randomUUID().toString();
        }

        ReadingEntity readingEntity = new ReadingEntity();
        readingEntity.setReadingId(reading.readingId);
        readingEntity.setPatientId(reading.patient.patientId);
        readingEntity.setReadDataJsonString(GsonUtil.getJson(reading));
        readingEntitiesDatabase.daoAccess().insertReading(readingEntity);

        // update all the other patient's records .. because thats the way it is...
        List<Reading> readings = getReadings(context);
        for (Reading r : readings) {
            if (r.patient.patientId.equals(reading.patient.patientId) && r.readingId != reading.readingId) {
                if (r.isNeedRecheckVitals()) {
                    r.dateRecheckVitalsNeeded = null;
                    updateReading(context, r);
                }
            }
        }

    }

    @Override
    public void updateReading(Context context, Reading reading) {
        reading.dateLastSaved = ZonedDateTime.now();
        ReadingEntity readingEntity = new ReadingEntity();
        readingEntity.setReadingId(reading.readingId);
        readingEntity.setPatientId(reading.patient.patientId);
        readingEntity.setReadDataJsonString(GsonUtil.getJson(reading));

    }

    @Override
    public List<Reading> getReadings(Context context) {
        List<Reading> readings = new ArrayList<>();
        List<ReadingEntity> readingEntities = readingEntitiesDatabase.daoAccess().getAllReadingEntities();
        for (ReadingEntity readingEntity:readingEntities){
            Reading r = GsonUtil.makeObjectFromJson(readingEntity.getReadDataJsonString(),Reading.class);
            readings.add(r);
            String patientId  = r.patient.patientId;
            Util.ensure(readingEntity.getPatientId() == patientId ||
                    patientId.equals(r.patient.patientId));
        }
        return readings;
    }

    @Override
    public Reading getReadingById(Context context, String id) {
        return null;
    }

    @Override
    public void deleteReadingById(Context context, String readingID) {

    }

    @Override
    public void deleteAllData(Context context) {

    }
}
