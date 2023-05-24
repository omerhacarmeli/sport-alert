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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spot.alert.adapter.ClickListener;
import com.spot.alert.adapter.LocationAdapter;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.LocationDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocationFragment extends Fragment implements LocationReceiver.OnLocationStateListener, LocationListener, OnMapReadyCallback {

    private LocationDao locationDao;
    private LocationReceiver locationReceiver;

    private double latitude, longitude;
    private GoogleMap mMap;

    private LatLng locationChanged;

    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;

    private LocationAdapter adapter;
    private RecyclerView recyclerView;

    private ClickListener deleteListener;
    private ClickListener editListener;

    private ClickListener clickListener;

    private Map<Long, Marker> markerMap = new HashMap<>();

    private List<com.spot.alert.dataobjects.Location> locations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.locationReceiver = new LocationReceiver(this);

        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao();

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


        List<com.spot.alert.dataobjects.Location> list = new ArrayList<>();


        recyclerView
                = (RecyclerView) view.findViewById(
                R.id.recyclerView);
        deleteListener = new ClickListener() {
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.Location) {

                    com.spot.alert.dataobjects.Location location = (com.spot.alert.dataobjects.Location) obj;

                    if (markerMap.get(location.getId()) != null) {

                        Marker removedMarker = markerMap.remove(location.getId());
                        removedMarker.remove();
                    }

                    locationDao.deleteLocation(location);
                    Toast.makeText(getActivity(), location.getName() + " נמחק בהצלחה", Toast.LENGTH_LONG).show();
                }
            }
        };

        editListener = new ClickListener() {
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.Location) {

                    com.spot.alert.dataobjects.Location location = (com.spot.alert.dataobjects.Location) obj;

                    ((MainActivity)getActivity()).moveEditLocation(location);

                    Toast.makeText(getActivity(), "Edit Location " + location.getName(), Toast.LENGTH_LONG).show();
                }
            }
        };

        clickListener = new ClickListener() {
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.Location) {
                    com.spot.alert.dataobjects.Location location = (com.spot.alert.dataobjects.Location) obj;

                    if (markerMap.get(location.getId()) != null) {
                        markerMap.get(location.getId()).showInfoWindow();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                        mMap.animateCamera(cameraUpdate);
                        mMap.moveCamera(cameraUpdate);
                    }
                }
            }
        };

        FloatingActionButton addLocationFB = (FloatingActionButton) view.findViewById(
                R.id.addLocationFB);


        addLocationFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).moveCreateLocation();
            }
        });

        adapter = new LocationAdapter(getActivity(), deleteListener, editListener, clickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    addLocationFB.hide();
                } else {
                    addLocationFB.show();
                }
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        loadLiveData();

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

        updateLocationsOnMap();
    }


    private double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
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

        if (locationManager.isLocationEnabled()) {

            Log.i("About GPS", "GPS is Enabled in your device");
            Toast toast = Toast.makeText(getActivity(), "המיקום שלך הופעל", Toast.LENGTH_SHORT);
            toast.show();

        } else {
            alertDialogEnableLocation();
        }
    }

    private void loadLiveData() {
        this.locationDao.getLocations().observe(getActivity(), new Observer<List<com.spot.alert.dataobjects.Location>>() {
            @Override
            public void onChanged(List<com.spot.alert.dataobjects.Location> locationList) {

                locations = locationList;

                adapter.setDataChanged(locations);

                updateLocationsOnMap();
            }
        });
    }

    private void updateLocationsOnMap() {
        if (mMap != null && locations != null && !locations.isEmpty()) {

            com.spot.alert.dataobjects.Location center = locations.get(0);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(center.getLatitude(), center.getLongitude()), center.getZoom().floatValue());

            mMap.animateCamera(cameraUpdate);
            mMap.moveCamera(cameraUpdate);

            List<Marker> markers = new ArrayList<>();

            for (com.spot.alert.dataobjects.Location location : locations) {

                if (!markerMap.containsKey(location.getId())) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(location.getLabel()));
                    markerMap.put(location.getId(), marker);
                    markers.add(marker);
                }
                else {
                    markers.add(markerMap.get(location.getId()));
                }
            }

            mMap.animateCamera(cameraUpdate);
            mMap.moveCamera(cameraUpdate);

            markers.get(0).showInfoWindow();
        }
    }
}
