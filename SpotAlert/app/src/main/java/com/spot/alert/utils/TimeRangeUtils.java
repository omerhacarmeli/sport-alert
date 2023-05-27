package com.spot.alert.utils;

import java.text.DecimalFormat;

public class TimeRangeUtils {

    private static DecimalFormat df = new DecimalFormat("#.##");
    public static String getTimeLabel(Double time) {


         int hours = time.intValue();

        int minutes = Double.valueOf(df.format(((time - ((double) hours)) * (double) 100))).intValue();

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

        double time = (double) hours + (double) minutes / 100.0;

        return time;
    }
}
