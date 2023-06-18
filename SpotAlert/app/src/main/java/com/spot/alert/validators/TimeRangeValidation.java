package com.spot.alert.validators;

import com.spot.alert.adapter.timerange.ITimeRange;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;

import java.util.List;
import java.util.regex.Pattern;

public class TimeRangeValidation {

    public static ValidateResponse validateTimeRange(List<ITimeRange> timeRangeList) {//checking if the time range is validate
        if (timeRangeList == null || timeRangeList.isEmpty()) {//checking if the list is empty

            return new ValidateResponse(false, "שעות פעילות לא מוגדרות"); //return false the message that the work hours is not set
        }

        ValidateResponse validateResponse = validateTimeRangeIsEmptyHours(timeRangeList);// checking if the hours FROM and TO are set

        if (!validateResponse.isValidate()) {// checking if not validate
            return validateResponse;// returning the out come
        }

        return new ValidateResponse(true, "טווח שעות תקין");// if every thing is validate we are returning true
    }

    private static ValidateResponse validateTimeRangeIsEmptyHours(List<ITimeRange> timeRangeList) { //checking is all the hours are set

        for (ITimeRange timeRange : timeRangeList) {//going over the list and checking that everthing is set

            if (timeRange.getFromTime() == null || timeRange.getToTime() == null) {// checking if not set
                return new ValidateResponse(false, "טווח שעות אינו מלא");// returning false
            }
        }

        return new ValidateResponse(true, "טווח שעות תקין");// returning true
    }

    public static ValidateResponse validateTimeRange(ITimeRange timeRange) {//in this function we check if the time span is validate

        if (timeRange.getFromTime() == null || timeRange.getToTime() == null) {
            return new ValidateResponse(false, "טווח שעות אינו מלא");
        }
        return new ValidateResponse(true, "טווח שעות תקין");
    }
    public static ValidateResponse validateTimeFromTo(Double timeFrom, Double timeTo) { // in this function the time range FROM is smaller that time range TO
        if (timeTo == null) {
            return new ValidateResponse(true);
        }
        if (timeFrom >= timeTo) {// if timeFrom bigger than timeTo than we returning false
            return new ValidateResponse(false, "הבחירת השעות אינה תקינה, הנא בחר שוב");
        } else {
            return new ValidateResponse(true, "בחירת שעות תקינה");
        }
    }

}
