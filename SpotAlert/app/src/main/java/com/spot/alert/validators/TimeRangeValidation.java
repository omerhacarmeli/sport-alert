package com.spot.alert.validators;

import com.spot.alert.adapter.timerange.ITimeRange;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;

import java.util.List;
import java.util.regex.Pattern;

public class TimeRangeValidation {

    public static ValidateResponse validateTimeRange(List<ITimeRange> timeRangeList) {
        if (timeRangeList == null || timeRangeList.isEmpty()) {

            return new ValidateResponse(false, "שעות פעילות לא מוגדרות");
        }

        ValidateResponse validateResponse = validateTimeRangeIsEmptyHours(timeRangeList);

        if (!validateResponse.isValidate()) {
            return validateResponse;
        }

        return new ValidateResponse(true, "טווח שעות תקין");
    }

    private static ValidateResponse validateTimeRangeIsEmptyHours(List<ITimeRange> timeRangeList) {

        for (ITimeRange timeRange : timeRangeList) {

            if (timeRange.getFromTime() == null || timeRange.getToTime() == null) {
                return new ValidateResponse(false, "טווח שעות אינו מלא");
            }
        }

        return new ValidateResponse(true, "טווח שעות תקין");
    }

    public static ValidateResponse validateTimeRange(ITimeRange timeRange) {

        if (timeRange.getFromTime() == null || timeRange.getToTime() == null) {
            return new ValidateResponse(false, "טווח שעות אינו מלא");
        }
        return new ValidateResponse(true, "טווח שעות תקין");
    }
    public static ValidateResponse validateTimeFromTo(Double timeFrom, Double timeTo) {
        if (timeTo == null) {
            return new ValidateResponse(true);
        }
        if (timeFrom >= timeTo) {
            return new ValidateResponse(false, "הבחירת השעות אינה תקינה, הנא בחר שוב");
        } else {
            return new ValidateResponse(true, "בחירת שעות תקינה");
        }
    }

}
