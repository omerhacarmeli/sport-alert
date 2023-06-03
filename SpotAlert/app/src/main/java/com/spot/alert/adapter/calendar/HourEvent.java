package com.spot.alert.adapter.calendar;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourEvent
{
    LocalTime time;
    List<Event> events;

    public HourEvent(LocalTime time, List<Event> events)
    {
        this.time = time;
        this.events = events;
    }

    public LocalTime getTime()
    {
        return time;
    }

    public void setTime(LocalTime time)
    {
        this.time = time;
    }

    public List<Event> getEvents()
    {
        return events;
    }

    public void setEvents(List<Event> events)
    {
        this.events = events;
    }
}
