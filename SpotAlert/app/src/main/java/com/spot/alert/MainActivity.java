package com.spot.alert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar mSnackbar = Snackbar.make(view, "התראת מצוקה נשלחה למוקד", Snackbar.LENGTH_LONG);
                View mView = mSnackbar.getView();
                TextView textView = mView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextColor(Color.RED);
                mSnackbar.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_location);

        FragmentManager fragmentManager = getSupportFragmentManager();
        LocationFragment fragment = new LocationFragment();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_location) {
            fragment = new LocationFragment();
        } else if (id == R.id.nav_gallery) {
            fragment = new GalleryFragment();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {


        } else if (id == R.id.nav_magic_stick) {
                magicStick();
        } else if (id == R.id.nav_center) {
            fragment = new CenterPointFragment();
        } else if (id == R.id.logout) {
            logout();
        }


        if(fragment!=null) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out  // popExit
                    )
                    .replace(R.id.frameLayout, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void magicStick() {
        new MagicStickManager().run(this);

        Toast toast = Toast.makeText(this, "Magic Stick Completed", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void logout() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("המיקום של המכשיר כבוי, האם אתה מעונין להדליק?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "כן",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SpotAlertAppContext.ACTIVE_USER = null;
                        startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                " לא",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();


    }
}
