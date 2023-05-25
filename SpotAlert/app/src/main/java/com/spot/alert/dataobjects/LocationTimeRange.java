package com.spot.alert.dataobjects;

import androidx.room.ColumnInfo;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(foreignKeys = @ForeignKey(entity = Location.class,
        parentColumns = "id",
        childColumns = "locationId",
        onDelete = ForeignKey.CASCADE))
public class LocationTimeRange {

    @PrimaryKey(autoGenerate = true)
    public int id;


    @ColumnInfo(name = "fromTime")
    public String fromTime;

    @ColumnInfo(name = "toTime")
    public String toTime;

    @ColumnInfo(name = "dayWeek")
    public int dayWeek;

    @ColumnInfo(name = "locationId")
    public Long locationId;

    public LocationTimeRange(int id, String fromTime, String toTime, int dayWeek, Long locationId) {
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.dayWeek = dayWeek;
        this.locationId = locationId;
    }

    public LocationTimeRange()
    {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public int getDayWeek() {
        return dayWeek;
    }

    public void setDayWeek(int dayWeek) {
        this.dayWeek = dayWeek;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
