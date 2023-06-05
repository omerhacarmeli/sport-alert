package com.spot.alert.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.spot.alert.dataobjects.CalendarManagement;
import com.spot.alert.dataobjects.ImageEntity;

import java.util.List;

@Dao
public interface CalendarManagementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCalendarManagement(CalendarManagement calendarManagement);

    @Delete
    void deleteCalendarManagement(CalendarManagement calendarManagement);

    @Update
    void updateCalendarManagement(CalendarManagement calendarManagement);

    @Query("SELECT * FROM CalendarManagement WHERE date =:date and locationId =:locationId")
    List<CalendarManagement> getCalendarManagements(String date, Long locationId);
}
