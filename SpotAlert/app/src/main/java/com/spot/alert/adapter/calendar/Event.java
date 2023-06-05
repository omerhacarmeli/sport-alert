package com.spot.alert.adapter.calendar;

import com.spot.alert.dataobjects.CalendarManagement;
import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.dataobjects.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {
    private LocalDate date;
    private LocalTime time;

    CalendarManagement calendarManagement;

    LocationTimeRange locationTimeRange;

    public Event(LocalDate date, LocalTime time, LocationTimeRange locationTimeRange,CalendarManagement calendarManagement) {

        this.date = date;
        this.time = time;
        this.locationTimeRange = locationTimeRange;
        this.calendarManagement = calendarManagement ;
    }

    public LocationTimeRange getLocationTimeRange() {
        return locationTimeRange;
    }

    public void setLocationTimeRange(LocationTimeRange locationTimeRange) {
        this.locationTimeRange = locationTimeRange;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public CalendarManagement getCalendarManagement() {
        return calendarManagement;
    }

    public void setCalendarManagement(CalendarManagement calendarManagement) {
        this.calendarManagement = calendarManagement;
    }
}
