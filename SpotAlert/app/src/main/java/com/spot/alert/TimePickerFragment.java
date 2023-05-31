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

import com.spot.alert.adapter.timerange.ITimeRange;
import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.utils.TimeRangeUtils;
import com.spot.alert.validators.LocationValidation;
import com.spot.alert.validators.TimeRangeValidation;
import com.spot.alert.validators.ValidateResponse;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    TextView textView;
    TextView errorView;

    long dateTime;
    int minutes;
    int hours;
    ITimeRange timeRange;
    String fromto;
    boolean ignoreTime;

    public TimePickerFragment(TextView textView, TextView errorView, ITimeRange timeRange, String fromto) {
        this.textView = textView;
        this.timeRange = timeRange;
        this.fromto = fromto;
        this.errorView = errorView;
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
            ValidateResponse validation = TimeRangeValidation.validateTimeFromTo(timeNum, timeRange.getToTime());
            if (!validation.isValidate()) {
                Toast toast = Toast.makeText(getActivity(), validation.getMsg(), Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                timeRange.setFromTime(timeNum);
            }

        } else {
            ValidateResponse validation = TimeRangeValidation.validateTimeFromTo(timeRange.getFromTime(), timeNum);
            if (!validation.isValidate()) {
                Toast toast = Toast.makeText(getActivity(), validation.getMsg(), Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                timeRange.setToTime(timeNum);
            }
        }

        this.textView.setText(TimeRangeUtils.getTimeLabel(timeNum));

        validateTimeRange();
    }



    public boolean checkTimeInputValidation5() {
        if (timeRange.getToTime() == null) {
            return true;
        }
        if (timeRange.getFromTime() > timeRange.getToTime()) {
            Toast toast = Toast.makeText(getActivity(), "הבחירת השעות אינה תקינה, הנא בחר שוב", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;
        }
    }

    private ValidateResponse validateTimeRange() {
        ValidateResponse validateResponse = TimeRangeValidation.validateTimeRange(timeRange);

        if (!validateResponse.isValidate()) {
            errorView.setError("טווח שעות אינו תקין");
            errorView.setText("טווח שעות אינו תקין");
        }
        else {
            errorView.setError(null);
            errorView.setText(null);
        }

        return validateResponse;
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