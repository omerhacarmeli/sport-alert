package com.spot.alert.adapter.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.spot.alert.R;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.CalendarManagementDao;
import com.spot.alert.dataobjects.CalendarManagement;
import com.spot.alert.dataobjects.User;
import com.spot.alert.dataobjects.UserTimeRange;
import com.spot.alert.utils.CalendarUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HourAdapter extends ArrayAdapter<HourEvent> {

    List<User> allUserByIds;

    List<UserTimeRange> userTimeRangesByUserAndDay;

    Map<Long, User> userMap;

    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents, List<User> allUserByIds, List<UserTimeRange> userTimeRangesByUserAndDay) {
        super(context, 0, hourEvents);

        this.allUserByIds = allUserByIds;
        this.userTimeRangesByUserAndDay = userTimeRangesByUserAndDay;
        this.userMap = new HashMap<>();
        for (User user : allUserByIds) {
            this.userMap.put(user.getUserId(), user);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HourEvent hourEvent = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);

        setHour(convertView, hourEvent.time);
        setEvents(convertView, hourEvent.event);

        return convertView;
    }

    private void setHour(View convertView, LocalTime time) {
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        timeTV.setText(CalendarUtils.formattedShortTime(time));
    }

    private void setEvents(View convertView, Event event) {

        Spinner spinner = convertView.findViewById(R.id.selectG);

        if (event == null) {
            spinner.setVisibility(View.GONE);
            return;
        }

        List<User> filterUser = filterUserByTimeRanges(event);

        fillUser(filterUser);


        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<User> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, filterUser);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isUserSelection = false;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserSelection) {
                    if (event != null) {
                        User selectedUser = (User) adapter.getItem(position);
                        if (selectedUser.getUserId() != -1) {

                            updateCalendarManagement(getContext(), event, selectedUser);

                        } else {

                            deleteCalendarManagement(getContext(), event, selectedUser);
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

        int userIndex = getUserIndex(event.getCalendarManagement(), filterUser);
        if (userIndex != -1) {

            spinner.setSelection(userIndex);
        }
    }

    private static void fillUser(List<User> filterUser) {

        User user = new User();
        user.setUserId(-1L);

        if (filterUser.isEmpty()) {
            user.setUserName("השמה ריקה");
        } else {
            user.setUserName("אין עובד זמין");
        }

        filterUser.add(0, user);
    }

    private void deleteCalendarManagement(Context context, Event event, User selectedUser) {
        if (event.getCalendarManagement() != null) {

            CalendarManagementDao calendarManagementDao = AppDataBase.getDatabase(context).calendarManagementDao();
            calendarManagementDao.deleteCalendarManagement(event.getCalendarManagement());

            event.setCalendarManagement(null);
        }
    }

    private void updateCalendarManagement(Context context, Event event, User selectedUser) {
        CalendarManagementDao calendarManagementDao = AppDataBase.getDatabase(context).calendarManagementDao();

        if (event.getCalendarManagement() != null) {
            AppDataBase.databaseWriteExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    CalendarManagement calendarManagement = event.getCalendarManagement();
                    calendarManagement.setUserId(selectedUser.getUserId());
                    calendarManagementDao.updateCalendarManagement(calendarManagement);
                }
            });
        } else {

            AppDataBase.databaseWriteExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    CalendarManagement calendarManagement = new CalendarManagement();

                    calendarManagement.setLocationId(event.getLocationTimeRange().getLocationId());
                    calendarManagement.setUserId(selectedUser.getUserId());
                    calendarManagement.setLocationTimeRangeId(event.getLocationTimeRange().getId());
                    calendarManagement.setTime(CalendarUtils.formattedShortTime(event.getTime()));
                    calendarManagement.setDate(CalendarUtils.formattedDate(event.getDate()));

                    Long id = calendarManagementDao.insertCalendarManagement(calendarManagement);

                    calendarManagement.setId(id);

                    event.setCalendarManagement(calendarManagement);
                }
            });
        }
    }

    private List<User> filterUserByTimeRanges(Event event) {
        List<User> userList = new ArrayList<>();

        for (UserTimeRange userTimeRange : this.userTimeRangesByUserAndDay) {
            int cellHour = event.getTime().getHour();
            if (cellHour >= userTimeRange.fromTime.intValue() && cellHour < userTimeRange.toTime) {
                userList.add(userMap.get(userTimeRange.getUserId()));
            }
        }

        return userList;
    }

    private int getUserIndex(CalendarManagement calendarManagement, List<User> filterUser) {

        if (calendarManagement == null || calendarManagement.getUserId() == null) {
            return -1;
        }

        Long userId = calendarManagement.getUserId();

        for (int i = 0; i < filterUser.size(); i++) {
            if (userId == filterUser.get(i).getUserId()) {
                return i;
            }
        }

        return 0;
    }
}













