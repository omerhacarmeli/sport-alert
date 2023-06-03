package com.spot.alert.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.spot.alert.dataobjects.CalendarManagement;
import com.spot.alert.dataobjects.ImageEntity;

@Dao
public interface CalendarManagementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertImageEntity(CalendarManagement calendarManagement);

    @Delete
    void deleteImageEntity(CalendarManagement calendarManagement);

    @Update
    void updateImageEntity(CalendarManagement calendarManagement);

    @Query("SELECT * FROM CalendarManagement WHERE date =:date and time =:time and locationId =:locationId and userId =:userId")
    CalendarManagement getCalendarManagement(String date, String time, Long locationId, Long userId);
}
