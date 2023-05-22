package com.spot.alert;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.LocationDao;


public class LocationFragment extends Fragment implements LocationReceiver.OnLocationStateListener,LocationListener, OnMapReadyCallback {

    private LocationDao locationDao;
    private LocationReceiver locationReceiver ;

    private double latitude, longitude;
    private GoogleMap mMap;

    private LatLng locationChanged;

    private SupportMapFragment supportMapFragment;
    //private LocationActivity locationActivity;
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.locationReceiver = new LocationReceiver(this);
        this.locationDao  = AppDataBase.getDatabase(getActivity()).locationDao();

        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                , android.Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Request location updates
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Log.i("About GPS", "GPS is Enabled in your devide");
        } else {
            alertDialogEnableLocation();
        }

        FragmentManager fm = getActivity().getSupportFragmentManager();
        supportMapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }

    protected void alertDialogEnableLocation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("המיקום של המכשיר כבוי, האם אתה מעונין להדליק?");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setPositiveButton(
                "כן",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.setNegativeButton(
                " לא",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = alertDialogBuilder.create();
        alert11.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(LocationManager.MODE_CHANGED_ACTION);
        getActivity().registerReceiver(locationReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(locationReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        SpotAlertAppContext.googleMap = googleMap;
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                locationChanged = latLng;
            }
        });

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.addMarker(new MarkerOptions().position(new LatLng(31.5094458, 34.5918490)).title("ספיר"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(31.5080314, 34.6004334)).title("גבים"));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(31.509445865991342, 34.59184910433942), 16.0f);
        mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);
        double distance = getDistanceFromLatLonInKm( 31.5094458, 34.5918490, 31.5080314, 34.6004334);

        Log.d("distance: ", distance + "KM");
    }


    private double getDistanceFromLatLonInKm(double lat1,double lon1,double lat2,double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
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
    public void onLocationStateChange() {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Log.i("About GPS", "GPS is Enabled in your device");
            Toast toast = Toast.makeText(getActivity(), "המיקום שלך הופעל", Toast.LENGTH_SHORT);
            toast.show();

        } else {
            alertDialogEnableLocation();
        }
    }
}
