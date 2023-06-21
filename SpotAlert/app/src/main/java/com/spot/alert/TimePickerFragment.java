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
import com.spot.alert.utils.TimeRangeUtils;
import com.spot.alert.validators.TimeRangeValidation;
import com.spot.alert.validators.ValidateResponse;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private TextView textView;
    private TextView errorView;
    private int minutes;
    private int hours;
    private ITimeRange timeRange;
    private String fromto;
    public TimePickerFragment(TextView textView, TextView errorView, ITimeRange timeRange, String fromto) {// constructor
        this.textView = textView;
        this.timeRange = timeRange;
        this.fromto = fromto;
        this.errorView = errorView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();//defining the clock
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//returning the hour of day
        int minute = calendar.get(Calendar.MINUTE);//returning the minutes of day
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute, true);
        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {//setting the time
        minutes = selectedMinute;//here we are doing insert to the minutes
        hours = selectedHour;//here we are doing insert to the hours

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

    private ValidateResponse validateTimeRange() {
        ValidateResponse validateResponse = TimeRangeValidation.validateTimeRange(timeRange);//check if the time range is good

        if (!validateResponse.isValidate()) {// checking if the timeRange isn't validate
            errorView.setError("טווח שעות אינו תקין");// setting that the time range isn't validate and putting a message
            errorView.setText("טווח שעות אינו תקין");// setting that the time range isn't validate and putting a message
        } else {//if it is validate, putting an null on the error
            errorView.setError(null);
            errorView.setText(null);
        }

        return validateResponse;// returning the value
    }
}
