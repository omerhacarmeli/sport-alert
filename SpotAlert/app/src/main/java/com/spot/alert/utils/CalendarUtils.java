package com.spot.alert.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CalendarUtils {
    public static LocalDate selectedDate = LocalDate.now();

    public static String monthDayFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d");
        return date.format(formatter);
    }

    public static String formattedShortTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    public static int getDayOfWeek() {
        int dayOfWeekValue = selectedDate.getDayOfWeek().getValue();
        int adjustedDayOfWeek = (dayOfWeekValue % 7) + 1;

        return adjustedDayOfWeek;
    }

    public static String formattedDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}
