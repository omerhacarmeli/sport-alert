package com.spot.alert.adapter.calendar;

import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.dataobjects.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {
    private User user;
    private LocalDate date;
    private LocalTime time;

    LocationTimeRange locationTimeRange;

    public Event(User user, LocalDate date, LocalTime time, LocationTimeRange locationTimeRange) {
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
