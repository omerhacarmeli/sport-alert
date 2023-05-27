package com.spot.alert.validators;

public class LocationValidation {
    public static ValidateResponse validateTimeFromTo(Double timeFrom, Double timeTo) {
        if (timeTo == null) {
            return new ValidateResponse(true);
        }
        if (timeFrom> timeTo) {
            return new ValidateResponse(false, "הבחירת השעות אינה תקינה, הנא בחר שוב");
        } else {
            return new ValidateResponse(true, "בחירת שעות תקינה");
        }
    }
}
