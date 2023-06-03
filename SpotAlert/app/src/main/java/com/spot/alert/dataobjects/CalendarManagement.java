package com.spot.alert.dataobjects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"date", "time", "locationId", "userId"}, foreignKeys = {@ForeignKey(entity = Location.class,
        parentColumns = "id",
        childColumns = "locationId",
        onDelete = ForeignKey.CASCADE), @ForeignKey(entity = User.class,
        parentColumns = "userId",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE)
})
public class CalendarManagement {
    @NonNull
    private String date;
    @NonNull
    private String time;
    @NonNull
    private Long locationId;
    @NonNull
    private Long userId;

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
}
