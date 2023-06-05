package com.spot.alert;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.spot.alert.database.AppDataBase;
import com.spot.alert.dataobjects.CalendarManagement;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.User;
import com.spot.alert.utils.CalendarUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AlarmManagerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        if (ActivityCompat.checkSelfPermission(context, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String action = intent.getStringExtra("action");

        if (!SpotAlertAppContext.CHECK_FOR_SHIFTING.equals(action)) {
            return;
        }

        if (SpotAlertAppContext.ACTIVE_USER != null) {

            String formattedDate = CalendarUtils.formattedDate(LocalDate.now());
            String formattedShortTime = CalendarUtils.formattedShortTime(LocalTime.now().plusHours(1).withMinute(0).withSecond(0));

            boolean adminState = false;

            List<CalendarManagement> calendarManagementForUser = null;
            if (SpotAlertAppContext.ACTIVE_USER.equals(SpotAlertAppContext.SPOT_ALERT_ADMIN_USER)) {
                calendarManagementForUser = AppDataBase.getDatabase(context).calendarManagementDao().getCalendarManagementForAdminUser(formattedDate, formattedShortTime);
                adminState = true;
            } else {
                calendarManagementForUser = AppDataBase.getDatabase(context).calendarManagementDao().getCalendarManagementForUser(formattedDate, formattedShortTime, SpotAlertAppContext.ACTIVE_USER.getUserId());
            }

            for (CalendarManagement calendarManagement : calendarManagementForUser) {

                Location location = AppDataBase.getDatabase(context).locationDao().getLocation(calendarManagement.getLocationId());

                User user = SpotAlertAppContext.ACTIVE_USER;

                StringBuilder sb = new StringBuilder();
                if (adminState) {
                    user = AppDataBase.getDatabase(context).userDao().getUser(calendarManagement.getUserId());
                    sb.append("בשעה ").append(formattedShortTime).append(" יש ל ").append(user.getUserName()).append(" משמרת ").append(" במיקום: ").append(location.getName());
                } else {
                    sb.append("בשעה ").append(formattedShortTime).append(" יש לך ").append(" משמרת ").append(" במיקום: ").append(location.getName());
                }


                sendNotification(context, location, sb.toString());
            }
        }
    }

    private static void sendNotification(Context context, Location location, String msg) {

        long[] vibrationPattern = {0, 300, 200, 300};

        Intent tapResultIntent = new Intent(context, MainActivity.class);
        tapResultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = getActivity(context, 0, tapResultIntent, FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE);

        Notification notification = new
                NotificationCompat.Builder(context, SpotAlertAppContext.LOCATION_CHANNEL_ID)
                .setContentTitle("תזכורת לתחילת משמרת")
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setVibrate(vibrationPattern)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);


        notificationManager.notify(1, notification);
    }
}