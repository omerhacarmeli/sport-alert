package com.spot.alert;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.spot.alert.adapter.ClickListener;
import com.spot.alert.adapter.calendar.Event;
import com.spot.alert.adapter.calendar.HourAdapter;
import com.spot.alert.adapter.calendar.HourEvent;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.ImageEntityDao;
import com.spot.alert.database.LocationDao;
import com.spot.alert.database.LocationTimeRangeDao;
import com.spot.alert.database.UserDao;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.User;
import com.spot.alert.utils.CalendarUtils;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalendarManagementFragment extends Fragment {
    private UserDao userDao;
    private LocationDao locationDao;

    private LocationTimeRangeDao locationTimeRangeDao;
    private ImageEntityDao imageEntityDao;

    private ClickListener deleteListener;
    private ClickListener editListener;
    private List<User> users;

    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;
    private ImageButton nextDayAction;
    private ImageButton previousDayAction;
    private TextView currentLocation;
    private ImageButton nextLocationAction;
    private ImageButton previousLocationAction;
    private int currentLocationIndex = 0;
    private List<Long> locationIds;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calendar_management_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.userDao = AppDataBase.getDatabase(getActivity()).userDao();
        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao();
        this.imageEntityDao = AppDataBase.getDatabase(getActivity()).imageEntityDao();
        this.locationTimeRangeDao = AppDataBase.getDatabase(getActivity()).locationTimeRangeDao();

        //day views
        monthDayText = view.findViewById(R.id.monthDayText);
        hourListView = view.findViewById(R.id.hourListView);
        previousDayAction = view.findViewById(R.id.previousDayAction);
        nextDayAction = view.findViewById(R.id.nextDayAction);

        previousDayAction.setOnClickListener(v -> previousDayAction(v));
        nextDayAction.setOnClickListener(v -> nextDayAction(v));

        //location views
        currentLocation = view.findViewById(R.id.currentLocation);
        previousLocationAction = view.findViewById(R.id.previousLocationAction);
        nextLocationAction = view.findViewById(R.id.nextLocationAction);

        previousLocationAction.setOnClickListener(v -> previousLocationAction(v));
        nextLocationAction.setOnClickListener(v -> nextLocationAction(v));
    }

    @Override
    public void onResume() {
        super.onResume();
        setDayView();
        setLocationIds();
        setLocationByDay();
    }

    private void setDayView() {
        String dayOfMonth = CalendarUtils.monthDayFromDate(CalendarUtils.selectedDate);
        String dayOfWeek = CalendarUtils.selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        monthDayText.setText(dayOfMonth + "\n" + dayOfWeek);
        setHourAdapter();
    }

    private void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getActivity(), hourEventList());
        hourListView.setAdapter(hourAdapter);
    }

    private ArrayList<HourEvent> hourEventList() {
        ArrayList<HourEvent> list = new ArrayList<>();

        for (int hour = 6; hour < 24; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Event> events = Event.eventsForDateAndTime(CalendarUtils.selectedDate, time);
            HourEvent hourEvent = new HourEvent(time, events);
            list.add(hourEvent);
        }

        for (int hour = 0; hour < 6; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Event> events = Event.eventsForDateAndTime(CalendarUtils.selectedDate, time);
            HourEvent hourEvent = new HourEvent(time, events);
            list.add(hourEvent);
        }

        return list;
    }

    public void previousDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        setDayView();
        setLocationIds();
        setLocationByDay();
    }

    public void nextDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        setDayView();
        setLocationIds();
        setLocationByDay();
    }

    private void previousLocationAction(View v) {
        if (this.currentLocationIndex < this.locationIds.size() - 1) {
            this.currentLocationIndex++;
            setLocationByDay();
        }

    }

    private void nextLocationAction(View v) {
        if (this.currentLocationIndex >0) {
            this.currentLocationIndex--;
            setLocationByDay();
        }
    }

    private void setLocationByDay() {
        if(!this.locationIds.isEmpty()) {
            Long locationId = this.locationIds.get(this.currentLocationIndex);
            Location location = this.locationDao.getLocation(locationId);
            this.currentLocation.setText(location.getName() + "     (" + (this.currentLocationIndex+1) + " מ- " + this.locationIds.size() + ")");
        }
        else {
            this.currentLocation.setText("אין שיבוץ להיום");
        }
    }

    private void setLocationIds() {
        this.currentLocationIndex = 0;
        int dayNumber = CalendarUtils.selectedDate.getDayOfWeek().getValue() + 1;
       this.locationIds = locationTimeRangeDao.getLocationIdsByDayWeek(dayNumber);

    }

    private void loadLiveData() {

    }
}

