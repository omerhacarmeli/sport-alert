package com.spot.alert.adapter.calendar;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourEvent
{
    LocalTime time;
    Event event;

    public HourEvent(LocalTime time, Event event)
    {
        this.time = time;
        this.event = event;
    }

    public LocalTime getTime()
    {
        return time;
    }

    public void setTime(LocalTime time)
    {
        this.time = time;
    }

    public Event getEvent()
    {
        return event;
    }

    public void setEvent(Event event)
    {
        this.event = event;
    }
}
