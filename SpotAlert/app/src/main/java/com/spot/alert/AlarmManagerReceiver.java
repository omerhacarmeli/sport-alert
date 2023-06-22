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
import com.spot.alert.dataobjects.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlarmManagerReceiver extends BroadcastReceiver {

    private static  List<Long> userIds = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {


        if (ActivityCompat.checkSelfPermission(context, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (SpotAlertAppContext.ACTIVE_USER != null) {
            UserTimeRangeDao userTimeRangeDao = AppDataBase.getDatabase(context).userTimeRangeDao();
            UserDao userDao = AppDataBase.getDatabase(context).userDao();

            int dayNumber = getDayOfWeek();

            List<Long> allUserIds = userTimeRangeDao.getUserIdsAndDay(dayNumber);
            if (diffInUsersList(allUserIds)) {

            List<User> allUserByIds = userDao.getAllUserByIds(allUserIds);

//in here we create the message for the notification
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
//this give a drop down
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(msg); // Set the expanded message
        bigTextStyle.setSummaryText("תזכורת לגבי זקיפים"); // Set a summary for the expanded message

        Notification notification = new
                NotificationCompat.Builder(context, SpotAlertAppContext.LOCATION_CHANNEL_ID)//giving the channel
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

        notificationManager.notify(1, notification); //send the notification
    }

    public static int getDayOfWeek() {
        int dayOfWeekValue = LocalDate.now().getDayOfWeek().getValue();
        int adjustedDayOfWeek = (dayOfWeekValue % 7) + 1;

        return adjustedDayOfWeek;
    }
}
