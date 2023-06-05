package com.spot.alert;

import java.time.LocalDate;
import java.time.LocalTime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.spot.alert.database.CalendarManagementDao;
import com.spot.alert.database.ImageEntityDao;
import com.spot.alert.database.LocationDao;
import com.spot.alert.database.LocationTimeRangeDao;
import com.spot.alert.database.UserDao;
import com.spot.alert.database.UserTimeRangeDao;
import com.spot.alert.dataobjects.CalendarManagement;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.dataobjects.User;
import com.spot.alert.dataobjects.UserTimeRange;
import com.spot.alert.utils.CalendarUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarManagementFragment extends Fragment {
    private UserDao userDao;
    private LocationDao locationDao;

    private UserTimeRangeDao userTimeRangeDao;
    private LocationTimeRangeDao locationTimeRangeDao;

    private CalendarManagementDao calendarManagementDao;

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
    private List<User> allUserByIds;
    private List<UserTimeRange> userTimeRangesByUserAndDay;

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
        this.calendarManagementDao = AppDataBase.getDatabase(getActivity()).calendarManagementDao();
        this.locationTimeRangeDao = AppDataBase.getDatabase(getActivity()).locationTimeRangeDao();
        this.userTimeRangeDao = AppDataBase.getDatabase(getActivity()).userTimeRangeDao();


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
        setLocationIds();
        createData();
    }

    private void setDayView() {
        String dayOfMonth = CalendarUtils.monthDayFromDate(CalendarUtils.selectedDate);
        String dayOfWeek = CalendarUtils.selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        monthDayText.setText(dayOfMonth + "\n" + dayOfWeek);
        setHourAdapter();
    }

    private void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getActivity(), hourEventList(), this.allUserByIds, this.userTimeRangesByUserAndDay);
        hourListView.setAdapter(hourAdapter);
    }

    private void createData() {
        setUserIds();
        setLocationByDay();
        setDayView();
    }

    private ArrayList<HourEvent> hourEventList() {

        List<LocationTimeRange> locationTimeRangeList;
        List<CalendarManagement> calendarManagements;
        if (!locationIds.isEmpty()) {
            Long locationId = locationIds.get(this.currentLocationIndex);
            locationTimeRangeList = locationTimeRangeDao.getLocationTimeRangesByLocationId(locationId);
            calendarManagements = calendarManagementDao.getCalendarManagements(CalendarUtils.formattedDate(CalendarUtils.selectedDate), locationId);
        } else {
            locationTimeRangeList = new ArrayList<>();
            calendarManagements = new ArrayList<>();
        }


        Map<String, CalendarManagement> calendarManagementMap = getCalendarMapByTime(calendarManagements);


        ArrayList<HourEvent> list = new ArrayList<>();

        for (int hour = 6; hour < 24; hour++) {
            LocalTime time = LocalTime.of(hour, 0);

            CalendarManagement calendarManagement = calendarManagementMap.get(CalendarUtils.formattedShortTime(time));
            Event event = getEventsForDateAndTime(locationTimeRangeList, time, CalendarUtils.selectedDate,calendarManagement);
            HourEvent hourEvent = new HourEvent(time, event);
            list.add(hourEvent);
        }

        for (int hour = 0; hour < 6; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            CalendarManagement calendarManagement = calendarManagementMap.get(CalendarUtils.formattedShortTime(time));
            Event event = getEventsForDateAndTime(locationTimeRangeList, time, CalendarUtils.selectedDate,calendarManagement);
            HourEvent hourEvent = new HourEvent(time, event);
            list.add(hourEvent);
        }

        return list;
    }

    private Map<String, CalendarManagement> getCalendarMapByTime(List<CalendarManagement> calendarManagements) {
        Map<String, CalendarManagement> calendarManagementMap = new HashMap<>();
        for (CalendarManagement calendarManagement : calendarManagements) {

            calendarManagementMap.put(calendarManagement.getTime(), calendarManagement);
        }

        return calendarManagementMap;
    }

    private Event getEventsForDateAndTime(List<LocationTimeRange> locationTimeRangeList, LocalTime time, LocalDate dateTime,CalendarManagement calendarManagement) {

        for (LocationTimeRange locationTimeRange : locationTimeRangeList) {

            int cellHour = time.getHour();
            if (cellHour >= locationTimeRange.fromTime.intValue() && cellHour < locationTimeRange.toTime) {

                Event event = new Event(dateTime, time, locationTimeRange,calendarManagement);
                return event;
            }
        }

        return null;
    }

    public void previousDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        setLocationIds();
        createData();
    }

    public void nextDayAction(View view) {

        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        setLocationIds();
        createData();
    }

    private void setUserIds() {
        int dayNumber = CalendarUtils.getDayOfWeek();
        List<Long> userIds = userTimeRangeDao.getUserIdsAndDay(dayNumber);
        this.allUserByIds = userDao.getAllUserByIds(userIds);
        if (!this.allUserByIds.isEmpty()) {

            this.userTimeRangesByUserAndDay = this.userTimeRangeDao.getTimeRangesByUserAndDay(userIds, dayNumber);
        } else {
            this.userTimeRangesByUserAndDay = new ArrayList<>();
        }
    }

    private void nextLocationAction(View v) {
        if (this.currentLocationIndex < this.locationIds.size() - 1) {
            this.currentLocationIndex++;
            createData();
        }
    }

    private void previousLocationAction(View v) {
        if (this.currentLocationIndex > 0) {
            this.currentLocationIndex--;
            createData();
        }
    }

    private void setLocationByDay() {
        if (!this.locationIds.isEmpty()) {
            Long locationId = this.locationIds.get(this.currentLocationIndex);
            Location location = this.locationDao.getLocation(locationId);
            this.currentLocation.setText(location.getName() + "     (" + (this.currentLocationIndex + 1) + " מ- " + this.locationIds.size() + ")");
        } else {
            this.currentLocation.setText("אין שיבוץ להיום");
        }
    }

    private void setLocationIds() {
        this.currentLocationIndex = 0;
        int dayNumber = CalendarUtils.getDayOfWeek();
        this.locationIds = locationTimeRangeDao.getLocationIdsByDayWeek(dayNumber);
    }

    private void loadLiveData() {

    }
}

