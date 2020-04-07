package com.cradle.neptune.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HealthCareFacilityEntity {

    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String location;
    @ColumnInfo
    private String phoneNumber;

    // if user wants this in the dropdown menu
    @ColumnInfo
    private boolean isUserSelected;

    public HealthCareFacilityEntity(@NonNull String id, String name, String location, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.phoneNumber = phoneNumber;
    }

    public void setUserSelected(boolean userSelected) {
        isUserSelected = userSelected;
    }

    public boolean isUserSelected() {
        return isUserSelected;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
