package com.spot.alert.dataobjects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = Location.class,
        parentColumns = "id",
        childColumns = "locationId",
        onDelete = ForeignKey.CASCADE), @ForeignKey(entity = User.class,
        parentColumns = "userId",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE), @ForeignKey(entity = LocationTimeRange.class,
        parentColumns = "id",
        childColumns = "locationTimeRangeId",
        onDelete = ForeignKey.CASCADE)
})
public class CalendarManagement {

    public CalendarManagement(Long id, @NonNull String date, @NonNull String time, @NonNull Long locationId, @NonNull Long userId, @NonNull Long locationTimeRangeId) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.locationId = locationId;
        this.userId = userId;
        this.locationTimeRangeId = locationTimeRangeId;
    }

    public CalendarManagement()
    {

    }

    @PrimaryKey(autoGenerate = true)
    public Long id;
    @NonNull
    private String date;
    @NonNull
    private String time;
    @NonNull
    private Long locationId;
    @NonNull
    private Long userId;

    @NonNull
    private Long locationTimeRangeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @NonNull
    public Long getLocationTimeRangeId() {
        return locationTimeRangeId;
    }

    public void setLocationTimeRangeId(@NonNull Long locationTimeRangeId) {
        this.locationTimeRangeId = locationTimeRangeId;
    }
}
