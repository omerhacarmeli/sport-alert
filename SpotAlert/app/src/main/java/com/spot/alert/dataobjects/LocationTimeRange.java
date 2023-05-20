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

    @ColumnInfo(name = "locationId")
    public Long locationId;
}
