package com.example.finalproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    GpsLocationReceiver gpsLocationReceiver = new GpsLocationReceiver();


    private final int FINE_PERMMISION_CODE = 1;

    Location currentLocation;
    private LocationManager locationManager;
    private double latitude, longitude;

    private GoogleMap mMap;

    private Location mLastLocation;

    private LatLng locationChanged;
    private SupportMapFragment supportMapFragment;
    LocationActivity locationActivity;

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(LocationManager.MODE_CHANGED_ACTION);

        registerReceiver(gpsLocationReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(gpsLocationReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        locationActivity = this;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Request location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Log.i("About GPS", "GPS is Enabled in your devide");
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(LocationActivity.this);
            builder1.setMessage("המיקום של המכשיר כבוי, האם אתה מעונין להדליק?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "כן",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
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


        FragmentManager fm = this.getSupportFragmentManager();/// getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);

       /* Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();*/
/*
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation(fusedLocationProviderClient);*/
    }
     /*   Places.initialize(getApplicationContext(), "AIzaSyDJF_z4BxiZbZFLdHyrMmyBcoDZOBrLp3k");
        PlacesClient placesClient = Places.createClient(this);

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();
        Task<FindCurrentPlaceResponse> task = placesClient.findCurrentPlace(request);

        task.addOnSuccessListener(response -> {
            List<PlaceLikelihood> placeLikelihoods = response.getPlaceLikelihoods();
            if (!placeLikelihoods.isEmpty()) {
                Place place = placeLikelihoods.get(0).getPlace();
                String placeId = place.getId();
                // Use the placeId string to get the Place ID of the current location
            }
        });

        task.addOnFailureListener(exception -> {
            // Handle any errors that occurred while fetching the current place
        });
    }*/


    private void getLastLocation(FusedLocationProviderClient fusedLocationProviderClient) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Task<Location> lastLocation = fusedLocationProviderClient.getLastLocation();
        lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                locationChanged = latLng;
            }
        });


        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        double lat = 31.509445864880238;
        double lng = 34.59184909322831;
        LatLng latLng = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Sapir"));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

    }


    @Override
    public void onLocationChanged(Location location) {
        // Get the latitude and longitude
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // Do something with the coordinates
        Log.d("MyApp", "Latitude: " + latitude + ", Longitude: " + longitude);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}