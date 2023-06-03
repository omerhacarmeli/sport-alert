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
import java.util.Optional;

public class HourAdapter extends ArrayAdapter<HourEvent> {

    List<User> allUserByIds;

    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents, List<User> allUserByIds) {
        super(context, 0, hourEvents);

        this.allUserByIds = allUserByIds;

        User user = new User();
        user.setUserName("השמה ריקה");
        user.setUserId(-1);
        this.allUserByIds.add(0, user);
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

        Spinner spinner = convertView.findViewById(R.id.selectG);

        if (events.isEmpty()) {
            spinner.setVisibility(View.GONE);
            return;
        }

        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<User> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, allUserByIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isUserSelection = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserSelection) {
                    if (!events.isEmpty()) {
                        User selectedUser = (User) adapter.getItem(position);
                        if (selectedUser.getUserId() != -1) {
                            events.get(0).setUser(selectedUser);
                        } else {
                            events.get(0).setUser(null);
                        }
                    }
                } else {
                    isUserSelection = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int userIndex = getUserIndex(events.get(0).getUser());
        if (userIndex != -1) {

            spinner.setSelection(userIndex);
        }
    }

    private int getUserIndex(User user) {

        if (user == null) {
            return -1;
        }

        for (int i = 0; i < this.allUserByIds.size(); i++) {
            if (user.getUserId() == this.allUserByIds.get(i).getUserId()) {
                return i;
            }
        }

        return 0;
    }
}













