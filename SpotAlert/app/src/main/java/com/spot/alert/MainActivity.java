package com.spot.alert;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import static com.spot.alert.SpotAlertAppContext.LOCATION_CHANNEL_ID;
import static com.spot.alert.SpotAlertAppContext.LOCATION_CHANNEL_NAME;

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
import com.spot.alert.adapter.timerange.ITimeRange;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.User;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(view, "התראת מצוקה נשלחה למוקד", Snackbar.LENGTH_LONG);
                View mView = snackbar.getView();
                TextView textView = mView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextColor(Color.RED);
                snackbar.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //this toggle is to open to open the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_location);

//check that user current user is admin if no the magic will not appear on menu
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

        if (ActivityCompat.checkSelfPermission(this,POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // The permission to post notifications is granted
            // Your code logic here
        } else {
            // The permission is not granted
            // You can request the permission from the user
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 11122);
        }


        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(LOCATION_CHANNEL_ID, LOCATION_CHANNEL_NAME, importance);

        // Set any additional notification channel settings (e.g., sound, vibration, etc.)
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void setAlarmManager() {

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // Create an intent for your alarm receiver
        Intent intent = new Intent(this, AlarmManagerReceiver.class);

        // Set any extra data you want to pass to your receiver (optional)
        intent.putExtra("action", SpotAlertAppContext.CHECK_FOR_SHIFTING);

        // Create a PendingIntent to be triggered when the alarm fires
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm to trigger every 1 hour starting from the current time
        long intervalMillis = AlarmManager.INTERVAL_HALF_HOUR;
        long triggerAtMillis = System.currentTimeMillis() + 5000;

        // Set the alarm using the AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, 1000*30, pendingIntent);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_location) {
            fragment = new LocationFragment();
        } else if (id == R.id.nav_guards) {
            fragment = new UserFragment();
        } else if (id == R.id.nav_calendar_management) {
            fragment = new CalendarManagementFragment();
        } else if (id == R.id.nav_magic_stick) {
            magicStick();
        } else if (id == R.id.nav_center) {
            fragment = new CenterPointFragment();
        } else if (id == R.id.logout) {
            logout();
        }

        if (fragment != null) {
            moveFragment(fragment);
        }

        return true;
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
