package com.spot.alert.validators;

import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;

import java.util.List;
import java.util.regex.Pattern;

public class LocationValidation {


    public static Pattern locationNamePattern = Pattern.compile(" [A-Za-z0-9.-_]\\w{2,20}$");

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

    public static ValidateResponse validateName(Location location) {
        if (location.getName() != null && !location.getName().isEmpty()) {
            return new ValidateResponse(true, "שם מקום תקין");
        }

        return new ValidateResponse(false, "שם מקום לא תקין");
    }

    public static ValidateResponse validateLocation(Location location) {
        if (location.getLatitude() == null || location.getLongitude() == null) {

            return new ValidateResponse(false, "מיקום לא תקין");
        }

        return new ValidateResponse(true, "מיקום תקין");
    }

    public static ValidateResponse validateLocationTimeRange(List<LocationTimeRange> locationTimeRangeList) {
        if (locationTimeRangeList == null || locationTimeRangeList.isEmpty()) {

            return new ValidateResponse(false, "שעות פעילות בנקודה לא מוגדרות");
        }

        ValidateResponse validateResponse = validateLocationTimeRangeIsEmptyHours(locationTimeRangeList);

        if (!validateResponse.isValidate()) {
            return validateResponse;
        }

        return new ValidateResponse(true, "מיקום תקין");
    }

    private static ValidateResponse validateLocationTimeRangeIsEmptyHours(List<LocationTimeRange> locationTimeRangeList) {

        for (LocationTimeRange locationTimeRange : locationTimeRangeList) {

            if (locationTimeRange.getFromTime() == null || locationTimeRange.getToTime() == null) {
                return new ValidateResponse(false, "טווח שעות אינו מלא");
            }
        }

        return new ValidateResponse(true, "מיקום תקין");
    }

    public static ValidateResponse validateLocationTimeRange(LocationTimeRange locationTimeRange) {

        if (locationTimeRange.getFromTime() == null || locationTimeRange.getToTime() == null) {
            return new ValidateResponse(false, "טווח שעות אינו מלא");
        }
        return new ValidateResponse(true, "מיקום תקין");
    }
}
