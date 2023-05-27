package com.spot.alert;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.spot.alert.adapter.timerange.TimeRangeAdapter;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.LocationDao;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.utils.GeoUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CreateLocationFragment extends Fragment implements OnMapReadyCallback {

    private LocationDao locationDao;

    private double latitude, longitude;
    private GoogleMap mMap;
    private Location centerlocation;
    private Location newlocation;

    private LatLng latLng;
    private Marker centerLocationMarker;

    private Marker newLocationMarker;
    private TimeRangeAdapter timeRangeAdapter;
    private RecyclerView recyclerView;
    private ClickListener deleteListener;
    private ClickListener clickListener;

    private EditText createLocationNameEditText;

    private EditText createLocationSpotEditText;

    private List<LocationTimeRange> locationTimeRangeList = new ArrayList<>();

    private DecimalFormat df = new DecimalFormat("#.#####");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_location_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao();
        this.centerlocation = locationDao.getLocationByName(SpotAlertAppContext.CENTER_POINT_STRING);
        this.newlocation = new Location();

        createLocationNameEditText = view.findViewById(R.id.createLocationName);
        createLocationSpotEditText = view.findViewById(R.id.createLocationSpot);

        deleteListener = new ClickListener() {
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.LocationTimeRange) {

                    LocationTimeRange locationTimeRange = (LocationTimeRange) obj;

                    locationTimeRangeList.remove(locationTimeRange);

                    timeRangeAdapter.setDataChanged(locationTimeRangeList);

                    Toast.makeText(getActivity(), "הגדרת שעה נמחקה בהצלחה", Toast.LENGTH_LONG).show();
                }
            }
        };

        Button createLocationApproval = view.findViewById(R.id.createLocationApproval);
        Button createLocationCancel = view.findViewById(R.id.createLocationCancel);
        ImageButton locationItemButton = view.findViewById(R.id.locationItemButton);
        locationItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLocation();
            }
        });

        createLocationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).moveLocation();
            }
        });

        createLocationApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                Double latitude = Double.parseDouble(widthEditText.getText().toString());
                Double longitude = Double.parseDouble(lengthEditText.getText().toString());
                Double zoom = Double.parseDouble(zoomEditText.getText().toString());

                if (location == null) {
                    location = new Location();
                    location.setLabel(SpotAlertAppContext.CENTER_POINT_STRING);
                    location.setLevel(1);
                    location.setRadius(10);
                    location.setName(SpotAlertAppContext.CENTER_POINT_STRING);
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    location.setZoom(zoom);
                    location.setId(locationDao.insertLocation(location));
                } else {
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    location.setZoom(zoom);
                    locationDao.updateLocation(location);

                    Toast toast = Toast.makeText(getActivity(), "מוקד נשמר בהצלחה", Toast.LENGTH_SHORT);
                    toast.show();
                }
        */

            }
        });

        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                , android.Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        recyclerView
                = (RecyclerView) view.findViewById(
                R.id.recyclerView);
        timeRangeAdapter = new TimeRangeAdapter(getActivity(), deleteListener, clickListener);
        recyclerView.setAdapter(timeRangeAdapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        FloatingActionButton addLocationFB = (FloatingActionButton) view.findViewById(
                R.id.addTimeRageFB);

        addLocationFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationTimeRange locationTimeRange = new LocationTimeRange();
                locationTimeRange.setDayWeek(1);
                locationTimeRangeList.add(locationTimeRange);
                timeRangeAdapter.setDataChanged(locationTimeRangeList);
            }
        });

        FragmentManager fm = getActivity().getSupportFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }

    private void selectLocation() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Request location updates
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            android.location.Location spotLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (spotLocation != null) {
                updateNewLocation(new LatLng(spotLocation.getLatitude(), spotLocation.getLongitude()));
            }

        } else {

            Toast.makeText(getActivity(), "המיקום לא נבחר, יש צורך להדליק את המיקום במכשיר", Toast.LENGTH_LONG).show();
            GeoUtils.alertDialogEnableLocation(getActivity());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                updateNewLocation(latLng);
            }
        });

        if (centerlocation != null) {
            updateCenterLocationOnMap();
        }
    }

    private void updateNewLocation(LatLng latLng) {

        newlocation.setLatitude(latLng.latitude);
        newlocation.setLongitude(latLng.longitude);

        createLocationSpotEditText.setText("(" + df.format(latLng.latitude) + "," + df.format(latLng.longitude) + ")");

        updateNewLocationOnMap();
    }

    private void updateCenterLocationOnMap() {

        LatLng latLng = new LatLng(centerlocation.getLatitude(), centerlocation.getLongitude());

        if (this.centerLocationMarker != null) {
            this.centerLocationMarker.setPosition(latLng);
        } else {
            this.centerLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(centerlocation.getLabel()));
        }
        this.centerLocationMarker.showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, centerlocation.getZoom().floatValue());
        mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);
    }

    private void updateNewLocationOnMap() {

        LatLng latLng = new LatLng(newlocation.getLatitude(), newlocation.getLongitude());

        if (this.newLocationMarker != null) {
            this.newLocationMarker.setPosition(latLng);
        } else {
            this.newLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(newlocation.getLabel()));
        }
        this.centerLocationMarker.showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);
    }
}