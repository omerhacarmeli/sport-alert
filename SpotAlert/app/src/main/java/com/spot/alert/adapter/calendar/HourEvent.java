package com.spot.alert.adapter.calendar;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourEvent {
    LocalTime time;
    Event event;

    public HourEvent(LocalTime time, Event event) {
        this.time = time;
        this.event = event;
    }
}
