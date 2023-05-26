package com.spot.alert.validators;

import android.widget.Toast;

import com.spot.alert.dataobjects.LocationTimeRange;

public class LocationValidation {
    public static ValidateResponse checkTimeInputValidation(Double timeFrom,Double timeTo) {
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
