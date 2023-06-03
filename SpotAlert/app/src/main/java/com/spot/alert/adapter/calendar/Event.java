package com.spot.alert.adapter.calendar;

import com.spot.alert.dataobjects.LocationTimeRange;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {
    private String name;
    private LocalDate date;
    private LocalTime time;

    LocationTimeRange locationTimeRange;

    public Event(String name, LocalDate date, LocalTime time, LocationTimeRange locationTimeRange) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.locationTimeRange = locationTimeRange;
    }

    public LocationTimeRange getLocationTimeRange() {
        return locationTimeRange;
    }

    public void setLocationTimeRange(LocationTimeRange locationTimeRange) {
        this.locationTimeRange = locationTimeRange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
