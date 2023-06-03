package com.spot.alert.adapter.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import com.spot.alert.R;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.dataobjects.User;
import com.spot.alert.utils.CalendarUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourEvent> {

    List<User> allUserByIds;

    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents, List<User> allUserByIds) {
        super(context, 0, hourEvents);

        this.allUserByIds = allUserByIds;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HourEvent event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);

        setHour(convertView, event.time);
        setEvents(convertView, event.events);

        return convertView;
    }

    private void setHour(View convertView, LocalTime time) {
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        timeTV.setText(CalendarUtils.formattedShortTime(time));
    }

    private void setEvents(View convertView, List<Event> events) {
        TextView event = convertView.findViewById(R.id.event1);
        LinearLayout selectionLayout = convertView.findViewById(R.id.selectionLayout);
        Spinner spinner = convertView.findViewById(R.id.selectG);

        List<String> options = new ArrayList<>();
/*
        for (User user : allUserByIds) {
            options.add(user.getUserName());
        }*/

        ArrayAdapter<User> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, allUserByIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinner.getVisibility() == View.VISIBLE) {
                    spinner.setVisibility(View.GONE);
                } else {
                    spinner.setVisibility(View.VISIBLE);
                    spinner.performClick();
                }
            }
        });
        spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    spinner.setVisibility(View.GONE);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                User selectedOption = (User)adapter.getItem(position);
                event.setText(selectedOption.getUserName());
                events.get(0).setName(selectedOption.getUserName());
                spinner.setVisibility(View.GONE);
                Log.i("About Selection", selectedOption.getUserName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setVisibility(View.GONE);
            }
        });

        if (events.size() == 0) {
            hideEvent(event);
        } else {
            setEvent(event, events.get(0));
        }
    }

    private void setEvent(TextView textView, Event event) {
        textView.setText(event.getName());
        textView.setVisibility(View.VISIBLE);
    }

    private void hideEvent(TextView tv) {
        tv.setVisibility(View.INVISIBLE);
    }
}













