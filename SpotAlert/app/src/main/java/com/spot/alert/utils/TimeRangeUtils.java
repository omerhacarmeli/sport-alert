package com.spot.alert.utils;

public class TimeRangeUtils {
    public static String getTimeLabel(Double time) {


        int hours = time.intValue();
        int minutes = (int) ((time - ((double)hours)) * (double)60);

        String timeLabelHours = "";
        String timeLabelMinutes = "";

        if (hours < 10) {
            timeLabelHours += "0" + hours;
        } else {
            timeLabelHours += hours;
        }

        if (minutes < 10) {
            timeLabelMinutes += "0" + minutes;
        } else {
            timeLabelMinutes += minutes;
        }

        return timeLabelHours + ":" + timeLabelMinutes;

    }

    public static Double getTimeNumber(int hours, int minutes) {

        double time = (double) hours + (double) minutes / (double) 60;
        return time;
    }
}
