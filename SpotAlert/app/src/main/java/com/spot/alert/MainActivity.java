package com.spot.alert;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        setContentView(R.layout.activity_main); //הליאוט הוא של main
        Toolbar toolbar = findViewById(R.id.toolbar);//-tool barפה יוצרים את ה
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);// פנ יוצרים את הפאב
        fab.setOnClickListener(new View.OnClickListener() {// בעת לחיצה
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(view, "התראת מצוקה נשלחה למוקד", Snackbar.LENGTH_LONG);//מופיע התראה למטה
                View mView = snackbar.getView();//viewפה לוקחים את ה
                TextView textView = mView.findViewById(com.google.android.material.R.id.snackbar_text);//כאן זה הקופסאת טקסט
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);//כאן שמים אותו במרכז
                textView.setTextColor(Color.RED);// כאן צובעים אותו בצבע אדום
                snackbar.show();
            }
        });
        //טיפול בסגירה ופתיחה של הdrawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //this toggle is to open to open the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_location);

        //בדוק שהמשתמש הנוכחי הוא אדמין אם לא הקסם לא יופיע בתפריט
        if (!SpotAlertAppContext.ACTIVE_USER.getEmail().equals(SpotAlertAppContext.SPOT_ALERT_ADMIN_EMAIL)) {
            MenuItem item = navigationView.getMenu().findItem(R.id.nav_magic_stick);
            item.setVisible(false);
        }

        // Get the AlarmManager service
        createNotificationChannel();
        setAlarmManager();

        FragmentManager fragmentManager = getSupportFragmentManager();
        LocationFragment fragment = new LocationFragment();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }

    private void createNotificationChannel() {

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // The permission to post notifications is granted
            // Your code logic here
        } else {
            // The permission is not granted
            // You can request the permission from the user
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 11122);
        }

        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(SpotAlertAppContext.LOCATION_CHANNEL_ID, SpotAlertAppContext.LOCATION_CHANNEL_NAME, importance);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void setAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // צור כוונה למקלט כשזמן ההתראה מגיע
        Intent intent = new Intent(this, AlarmManagerReceiver.class);
        // הגדר כל מידע נוסף שתרצה להעביר למקלט שלך (אופציונלי)
        intent.putExtra("action", SpotAlertAppContext.CHECK_FOR_SHIFTING);//פרמטרים של ההודעה יכול להיות כל ערך שנחליט
        // צור PendingIntent שתופעל כאשר האזעקה מופעלת
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        //עושים התראה עם דיליי 5 שניות
        long triggerAtMillis = System.currentTimeMillis() + 5000;
        // הגדר את האזעקה באמצעות AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, 1000 * 30, pendingIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            logout();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // לנפח את התפריט; זה מוסיף פריטים לסרגל הפעולות אם הוא קיים
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // טיפול בפריט בסרגל הפעולה לחץ כאן. סרגל הפעולה יהיה
        // לטפל אוטומטית בלחיצות על כפתור הבית/מעלה, כל כך הרבה זמן
        // כפי שאתה מציין פעילות אב ב-AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {//כאן עושים את הבחירה לאיזה מסך לעבור מה-navigation drawer
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;//בהתחלה עושים את הפרגמנט נל
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_location) {// אם ההזהות שווה למיקום
            fragment = new LocationFragment();//פרגמנט יהיה שווה למיקום
        } else if (id == R.id.nav_guards) {// אם ההזהות שווה לשומרים פרגמנט
            fragment = new UserFragment();//פרגמנט יהיה שווה לשומר פרגמנט
        } else if (id == R.id.nav_magic_stick) {//אם הזהות שווה למקל הקסם
            magicStick();// עוברים לפונקציה
        } else if (id == R.id.nav_center) {//אם ההזהות שווה למיקום מוקד
            fragment = new CenterPointFragment();//פרגמנט יהיה שווה למוקד פרגמנט
        } else if (id == R.id.logout) {//אם ההזהות שווה ליצאה
            logout();// עוברים לפונקציית יצאה
        } else if (id == R.id.nav_zoki) {//אם ההזהות שווה ליצאה

            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);//פה אני יוצר אלרט ואני שואל האם הוא בטוח לגבי יצירת המשתמש
            builder1.setMessage("האם אתה מעוניין לעבור למוקד??");//ההודעה
            builder1.setCancelable(true);//אם הוא לוחץ ביטול
            builder1.setPositiveButton(//במקרה של אם כן
                    "כן",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            moveCenterPoint();
                        }
                    });
            builder1.setNegativeButton(//במקרה שהמשתמש לוחץ לא, האפליקציה לא עושה כלום
                    " לא",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();//ביטול
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        if (fragment != null) { //כאן בודקים אם הפרגמנט הוא שונה מנל
            moveFragment(fragment);//אם הוא כן אז עוברים לפונקציה שמעבירה מסך
        }


        return true;
    }

    private void moveCenterPoint() {
        Fragment fragment = new CenterPointFragment();
        moveFragment(fragment);
    }

    private void magicStick() {
        new MagicStickManager().run(this);

        Toast toast = Toast.makeText(this, "Magic Stick Completed", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void logout() {
        new AlertDialog.Builder(this).setMessage("האם אתה מעוניין להתנתק?")
                .setCancelable(true).setPositiveButton(
                        "כן",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SpotAlertAppContext.ACTIVE_USER = null;
                                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(
                        " לא",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create().show();
    }

    private void moveFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.frameLayout, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void moveEditLocation(Location location) {

        Fragment fragment = new EditLocationFragment();
        moveFragment(fragment);
    }

    public void moveCreateLocation() {
        Fragment fragment = new CreateLocationFragment();
        moveFragment(fragment);
    }

    public void moveLocation() {
        LocationFragment fragment = new LocationFragment();
        moveFragment(fragment);
    }

    public void moveEditUser(User user) {

        EditUserFragment fragment = new EditUserFragment();
        moveFragment(fragment);
    }

    public void moveCreateUser() {
        CreateUserFragment fragment = new CreateUserFragment();
        moveFragment(fragment);
    }

    public void moveUser() {
        UserFragment fragment = new UserFragment();
        moveFragment(fragment);
    }
}
