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
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.spot.alert.DatePickerFragment;
import com.spot.alert.R;
import com.spot.alert.SpotAlertAppContext;
import com.spot.alert.adapter.ClickListener;
import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.utils.TimeRangeUtils;

import java.util.Collections;
import java.util.List;

public class TimeRangeAdapter
        extends RecyclerView.Adapter<TimeRangeViewHolder> {

    List<LocationTimeRange> locationTimeRangeList = Collections.emptyList();
    Context context;
    ClickListener deleteListener;
    ClickListener clickListener;

    public TimeRangeAdapter(Context context, ClickListener deleteListener, ClickListener clickListener) {
        this.context = context;
        this.deleteListener = deleteListener;
        this.clickListener = clickListener;
    }

    @Override
    public TimeRangeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);

        View timeRangeView = inflater.inflate(R.layout.timerange_item, parent, false);

        TimeRangeViewHolder viewHolder = new TimeRangeViewHolder(timeRangeView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TimeRangeViewHolder viewHolder, final int position) {
        final int index = viewHolder.getAdapterPosition();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context.getApplicationContext(), R.array.days, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        viewHolder.spinnerDays.setAdapter(adapter);

        LocationTimeRange locationTimeRange = locationTimeRangeList.get(position);

        viewHolder.spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationTimeRange.setDayWeek(position + 1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (locationTimeRange.getFromTime() != null) {

            viewHolder.fromTime
                    .setText(TimeRangeUtils.getTimeLabel(locationTimeRange.getFromTime()));
        }
        else {
            viewHolder.fromTime
                    .setText("----:----");
        }

        if (locationTimeRange.getToTime() != null) {
            viewHolder.toTime
                    .setText(TimeRangeUtils.getTimeLabel(locationTimeRange.getToTime()));
        }
        else {
            viewHolder.toTime
                    .setText("----:----");
        }

        viewHolder.fromTimePickerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fromDatePickerFragment = new DatePickerFragment(viewHolder.fromTime, locationTimeRange, SpotAlertAppContext.FROM_TIME);
                fromDatePickerFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE PICK");
            }
        });

        viewHolder.toTimePickerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (locationTimeRange.getFromTime() != null) {
                    DatePickerFragment toDatePickerFragment = new DatePickerFragment(viewHolder.toTime, locationTimeRange, SpotAlertAppContext.TO_TIME);
                    toDatePickerFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE PICK");
                } else {
                    Toast.makeText(context, "קודם צריך להגדיר שעת התחלה", Toast.LENGTH_LONG).show();

                }
            }
        });

        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(),
                        android.R.anim.slide_out_right);
                anim.setDuration(300);
                viewHolder.view.startAnimation(anim);

                new Handler().postDelayed(() -> {
                    deleteListener.click(locationTimeRange);

                }, anim.getDuration());
            }
        });

        viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                viewHolder.deleteItem.setVisibility(View.VISIBLE);

                new Handler().postDelayed(() -> {

                            viewHolder.deleteItem.setVisibility(View.INVISIBLE);
                        }
                        , 4000);
                return true;
            }
        });

        viewHolder.spinnerDays.setSelection(locationTimeRange.getDayWeek() - 1);

    }

    @Override
    public int getItemCount() {
        return locationTimeRangeList.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setDataChanged(List<LocationTimeRange> locationTimeRangeList) {

        this.locationTimeRangeList = locationTimeRangeList;

        this.notifyDataSetChanged();
    }
}