package com.spot.alert.adapter.timerange;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.R;
import com.spot.alert.SpotAlertAppContext;
import com.spot.alert.TimePickerFragment;
import com.spot.alert.adapter.ClickListener;
import com.spot.alert.utils.TimeRangeUtils;
import com.spot.alert.validators.TimeRangeValidation;
import com.spot.alert.validators.ValidateResponse;

import java.util.Collections;
import java.util.List;

//this class is for the user to peak the time ranges he wants the workers to work on
public class TimeRangeAdapter
        extends RecyclerView.Adapter<TimeRangeViewHolder> {

    List<ITimeRange> timeRangeList = Collections.emptyList();
    Context context;
    ClickListener deleteListener;
    ClickListener clickListener;

    public TimeRangeAdapter(Context context, ClickListener deleteListener) {//the adapter time range constractor
        this.context = context;
        this.deleteListener = deleteListener;
    }

    public TimeRangeAdapter(Context context, ClickListener deleteListener, ClickListener clickListener) {//the adapter time range constractor
        this.context = context;
        this.deleteListener = deleteListener;
        this.clickListener = clickListener;
    }

    @Override
    public TimeRangeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {// creating new view and holder

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View timeRangeView = inflater.inflate(R.layout.timerange_item, parent, false);//create view of user timeRange item
        TimeRangeViewHolder viewHolder = new TimeRangeViewHolder(timeRangeView);// here we create the object of the holder
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TimeRangeViewHolder viewHolder, final int position) {// updete the reacelce view
        final int index = viewHolder.getAdapterPosition();
      //creating adapter for dropdown list of days (days from string Resource)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context.getApplicationContext(), R.array.days, R.layout.spinner_item);// it create an ArrayAdapter to the drop down list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        viewHolder.spinnerDays.setAdapter(adapter);

        ITimeRange timeRange = timeRangeList.get(position);//bring the time range and days in position from the list

        validateTimeRange(viewHolder.errorView, timeRange);// check if the time range is validate

        viewHolder.spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {// here we are doing a listener to the drop down days
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeRange.setDayWeek(position + 1);// adding 1 to the day because its starts with number 0
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (timeRange.getFromTime() != null) {// if start time the that was pick is not empty

            viewHolder.fromTime.setText(TimeRangeUtils.getTimeLabel(timeRange.getFromTime())); //set the time range and show the time
        } else {// if it is empty
            viewHolder.fromTime.setText("----:----");// leave empty
        }

        if (timeRange.getToTime() != null) {//if the end time (get to) is not empty and show the time
            viewHolder.toTime.setText(TimeRangeUtils.getTimeLabel(timeRange.getToTime()));//set the time range and show the time
        } else {// if it is empty
            viewHolder.toTime.setText("----:----");// leave empty
        }

        viewHolder.fromTimePickerImage.setOnClickListener(new View.OnClickListener() {//clock, taking the FromTime
            @Override
            public void onClick(View view) {
                //creating time piker fragment, he gets textView of from time, error, time range and fromto
                TimePickerFragment fromDatePickerFragment = new TimePickerFragment(viewHolder.fromTime, viewHolder.errorView, timeRange, SpotAlertAppContext.FROM_TIME);
                fromDatePickerFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE PICK");//how we open the fragment time picker
            }
        });

        viewHolder.toTimePickerImage.setOnClickListener(new View.OnClickListener() {//clock, taking the ToTIme
            @Override
            public void onClick(View view) {

                if (timeRange.getFromTime() != null) {//only if from time is defined
                    //craeting time piker fragmernt, he gets textView of from time, error, time range and fromto
                    TimePickerFragment toDatePickerFragment = new TimePickerFragment(viewHolder.toTime, viewHolder.errorView, timeRange, SpotAlertAppContext.TO_TIME);
                    toDatePickerFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE PICK");//how we open the fragment time pieker


                } else {//if not
                    Toast.makeText(context, "אנא הגדר שעת התחלה", Toast.LENGTH_LONG).show();// toast message

                }
            }
        });

        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {//this function is for deleting a user
            @Override
            public void onClick(View view) {
                //deleting animtion
                Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(),
                        android.R.anim.slide_out_right);
                anim.setDuration(300);
                viewHolder.view.startAnimation(anim);

                new Handler().postDelayed(() -> {
                    deleteListener.click(timeRange);//we send the time range we want to delete to the parant fragment it can be create user, update location and delete

                }, anim.getDuration());
            }
        });

        viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {// after a long click the icon will appear
            @Override
            public boolean onLongClick(View view) {//

                viewHolder.deleteItem.setVisibility(View.VISIBLE);// delete icon will be visible

                new Handler().postDelayed(() -> {// after 4 secodes it will disappear

                            viewHolder.deleteItem.setVisibility(View.INVISIBLE);
                        }
                        , 4000);
                return true;
            }
        });

        viewHolder.spinnerDays.setSelection(timeRange.getDayWeek() - 1);//set witch day we are right now

    }

    @Override
    public int getItemCount() {
        return timeRangeList.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setDataChanged(List<ITimeRange> timeRangeList) {//sending the list to the adapter

        this.timeRangeList = timeRangeList;// השמה

        this.notifyDataSetChanged();// refresh the data
    }

    private ValidateResponse validateTimeRange(TextView errorView, ITimeRange timeRange) {
        ValidateResponse validateResponse = TimeRangeValidation.validateTimeRange(timeRange);

        if (!validateResponse.isValidate()) {
            errorView.setError("טווח שעות אינו תקין");
            errorView.setText("טווח שעות אינו תקין");
        } else {
                        errorView.setError(null);
            errorView.setText(null);
        }

        return validateResponse;
    }
}