package com.spot.alert;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.utils.TimeRangeUtils;
import com.spot.alert.validators.LocationValidation;
import com.spot.alert.validators.ValidateResponse;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    TextView textView;
    long dateTime;
    int minutes;
    int hours;
    LocationTimeRange locationTimeRange;
    String fromto;
    boolean ignoreTime;

    public DatePickerFragment(TextView textView, LocationTimeRange locationTimeRange, String fromto) {
        this.textView = textView;
        this.locationTimeRange = locationTimeRange;
        this.fromto = fromto;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute, true);
        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

    /*    if (selectedMinute % 15 != 0) {

            Toast toast = Toast.makeText(getActivity(), "הבחירת הדקות אינה תקינה, הדקות צריכות להיות בחלקי 15 דקות", Toast.LENGTH_SHORT);
            toast.show();
            ignoreTime = true;
            return;
        }*/

        ignoreTime = false;
        minutes = selectedMinute;
        hours = selectedHour;

        Double timeNum = TimeRangeUtils.getTimeNumber(selectedHour, selectedMinute);

        if (SpotAlertAppContext.FROM_TIME.equals(fromto)) {
            ValidateResponse validation = LocationValidation.validateTimeFromTo(timeNum, locationTimeRange.getToTime());
            if (!validation.isValidate()) {
                Toast toast = Toast.makeText(getActivity(), validation.getMsg(), Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                locationTimeRange.setFromTime(timeNum);
            }

        } else {
            ValidateResponse validation = LocationValidation.validateTimeFromTo(locationTimeRange.getFromTime(), timeNum);
            if (!validation.isValidate()) {
                Toast toast = Toast.makeText(getActivity(), validation.getMsg(), Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                locationTimeRange.setToTime(timeNum);
            }
        }

        this.textView.setText(TimeRangeUtils.getTimeLabel(timeNum));

    }

    public boolean checkTimeInputValidation5() {
        if (locationTimeRange.getToTime() == null) {
            return true;
        }
        if (locationTimeRange.getFromTime() > locationTimeRange.getToTime()) {
            Toast toast = Toast.makeText(getActivity(), "הבחירת השעות אינה תקינה, הנא בחר שוב", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;
        }
    }

    public int getMinutes() {
        return this.minutes;
    }

    public int getHours() {
        return this.hours;
    }

    public boolean isIgnoreTime() {
        return this.ignoreTime;
    }
}
