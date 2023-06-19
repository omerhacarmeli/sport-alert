package com.spot.alert.dataobjects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.spot.alert.adapter.timerange.ITimeRange;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "userId",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE))
public class UserTimeRange implements ITimeRange {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "fromTime")
    public Double fromTime;

    @ColumnInfo(name = "toTime")
    public Double toTime;

    @ColumnInfo(name = "dayWeek")
    public int dayWeek;

    @ColumnInfo(name = "userId")
    public Long userId;

    public UserTimeRange(int Long, Double fromTime, Double toTime, int dayWeek, Long userId) {
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.dayWeek = dayWeek;
        this.userId = userId;
    }

    public UserTimeRange() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getFromTime() {
        return fromTime;
    }

    public void setFromTime(Double fromTime) {
        this.fromTime = fromTime;
    }

    public Double getToTime() {
        return toTime;
    }

    public void setToTime(Double toTime) {
        this.toTime = toTime;
    }

    public int getDayWeek() {
        return dayWeek;
    }

    public void setDayWeek(int dayWeek) {
        this.dayWeek = dayWeek;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
