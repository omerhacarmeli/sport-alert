package com.spot.alert;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import com.spot.alert.R;
import com.spot.alert.SpotAlertAppContext;
import com.spot.alert.adapter.ClickListener;
import com.spot.alert.adapter.location.LocationAdapter;
import com.spot.alert.adapter.timerange.TimeRangeAdapter;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.LocationDao;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;

import java.util.ArrayList;
import java.util.List;

public class CreateLocationFragment extends Fragment implements OnMapReadyCallback {

    private LocationDao locationDao;

    private double latitude, longitude;
    private GoogleMap mMap;
    private Location location;

    private LatLng latLng;
    private Marker marker;

    private TimeRangeAdapter timeRangeAdapter;
    private RecyclerView recyclerView;

    private ClickListener deleteListener;
    private ClickListener editListener;
    private ClickListener clickListener;

    private List<LocationTimeRange> locationTimeRangeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_location_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao();
        this.location = locationDao.getLocationByName(SpotAlertAppContext.CENTER_POINT_STRING);

        //EditText createLocationNameEditText = view.findViewById(R.id.createLocationName);
        //EditText createLocationNameLocationEditText = view.findViewById(R.id.createLocationNameLocation);

        if (location != null) {

        }
        deleteListener = new ClickListener() {
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.LocationTimeRange) {

                    LocationTimeRange locationTimeRange = (LocationTimeRange) obj;

                    locationTimeRangeList.remove(locationTimeRange);

                    timeRangeAdapter.setDataChanged(locationTimeRangeList);

                  //  locationDao.deleteLocation(location);
                    Toast.makeText(getActivity(), "נמחק בהצלחה", Toast.LENGTH_LONG).show();
                }
            }
        };

        editListener = new ClickListener() {
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.Location) {
/*
                    com.spot.alert.dataobjects.Location location = (com.spot.alert.dataobjects.Location) obj;
                    ((MainActivity) getActivity()).moveEditLocation(location);
                    Toast.makeText(getActivity(), "Edit Location " + location.getName(), Toast.LENGTH_LONG).show();*/
                }
            }
        };


        Button approval = view.findViewById(R.id.approval);
        approval.setOnClickListener(new View.OnClickListener() {
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
                updateLocationOnMap(location);
            }
        });

        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                , android.Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        recyclerView
                = (RecyclerView) view.findViewById(
                R.id.recyclerView);
        timeRangeAdapter = new TimeRangeAdapter(getActivity(), deleteListener, editListener, clickListener);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(location != null)
        {
            updateLocationOnMap(location);
        }
    }

    private void updateLocationOnMap(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (this.marker != null) {
            this.marker.setPosition(latLng);
        } else {
            this.marker = mMap.addMarker(new MarkerOptions().position(latLng).title(location.getLabel()));
        }
        this.marker.showInfoWindow();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,location.getZoom().floatValue());
        mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);
    }

}