package com.spot.alert.utils;

import java.text.DecimalFormat;

public class TimeRangeUtils {

    private static DecimalFormat df = new DecimalFormat("#.##");//subtracting the the numbers after the first 3

    public static String getTimeLabel(Double time) {

        int hours = time.intValue(); //taking only the hours

        int minutes = Double.valueOf(df.format(((time - ((double) hours)) * (double) 100))).intValue();// taking only the minutes and double it by 100
        // creating the strings for the hours and minutes
        String timeLabelHours = "";
        String timeLabelMinutes = "";

        if (hours < 10) {// if hours are smaller than 10 than im adding a 0
            timeLabelHours += "0" + hours;
        } else {// stays the same
            timeLabelHours += hours;
        }

        if (minutes < 10) {//// if minutes are smaller than 10 than im adding a 0
            timeLabelMinutes += "0" + minutes;
        } else {// stays the same
            timeLabelMinutes += minutes;
        }

        return timeLabelHours + ":" + timeLabelMinutes;//returning the time

    }

    public static Double getTimeNumber(int hours, int minutes) {// getting the time combination of the hours and minutes

        double time = (double) hours + (double) minutes / 100.0;

        return time;
    }
}
