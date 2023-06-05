package com.spot.alert.validators;

import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;

import java.util.List;
import java.util.regex.Pattern;

public class LocationValidation {

    public static ValidateResponse validateName(Location location) {
        if (location.getName() != null && !location.getName().isEmpty()) {
            return new ValidateResponse(true, "שם מקום תקין");
        }

        return new ValidateResponse(false, "שם מקום אינו תקין");
    }

    public static ValidateResponse validateLocation(Location location) {
        if (location.getLatitude() == null || location.getLongitude() == null) {

            return new ValidateResponse(false, "מיקום לא תקין");
        }

        return new ValidateResponse(true, "מיקום תקין");
    }
}
