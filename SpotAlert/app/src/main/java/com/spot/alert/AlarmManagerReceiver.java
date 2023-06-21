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
import com.spot.alert.database.UserDao;
import com.spot.alert.database.UserTimeRangeDao;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.User;
import com.spot.alert.utils.CalendarUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AlarmManagerReceiver extends BroadcastReceiver {

    private static  List<Long> userIds = new ArrayList<>();

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


            UserTimeRangeDao userTimeRangeDao = AppDataBase.getDatabase(context).userTimeRangeDao();
            UserDao userDao = AppDataBase.getDatabase(context).userDao();

            int dayNumber = CalendarUtils.getDayOfWeek();

            List<Long> allUserIds = userTimeRangeDao.getUserIdsAndDay(dayNumber);
            if (diffInUsersList(allUserIds)) {

            List<User> allUserByIds = userDao.getAllUserByIds(allUserIds);


                StringBuilder sb = new StringBuilder();
                sb.append("רשימת הזקיפים להיום: ");

                for (User user : allUserByIds) {
                    sb.append(user.getUserName() + ",");
                }

                sendNotification(context, sb.toString());
            }
        }
    }

    private boolean diffInUsersList(List<Long> allUserIds) {

        if (allUserIds.size() != userIds.size()) {

            userIds = allUserIds;
            return true;
        }

        userIds = allUserIds;

        return false;
    }


    private static void sendNotification(Context context, String msg) {
        Intent tapResultIntent = new Intent(context, MainActivity.class);
        tapResultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = getActivity(context, 0, tapResultIntent, FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(msg); // Set the expanded message
        bigTextStyle.setSummaryText("תזכורת לגבי זקיפים"); // Set a summary for the expanded message

        Notification notification = new
                NotificationCompat.Builder(context, SpotAlertAppContext.LOCATION_CHANNEL_ID)
                .setContentTitle("תזכורת לגבי זקיפים")
                .setContentText(msg)
                .setStyle(bigTextStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(1, notification);
    }
}