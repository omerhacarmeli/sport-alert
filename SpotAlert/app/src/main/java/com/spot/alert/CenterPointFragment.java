package com.spot.alert;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.LocationDao;
import com.spot.alert.dataobjects.Location;

public class CenterPointFragment extends Fragment implements OnMapReadyCallback {

    private LocationDao locationDao;

    private double latitude, longitude;
    private GoogleMap mMap;
    private Location location;

    private LatLng latLng;

    private Marker marker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.center_point_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao();
        location = new Location();
        location.setLabel("מוקד אבטחה");
        location.setLevel(1);
        location.setRadius(10);
        location.setName("מוקד אבטחה");
        location.setLatitude(31.557886);
        location.setLongitude(34.646048);

        EditText widthEditText = view.findViewById(R.id.width);
        EditText lengthEditText = view.findViewById(R.id.length);

        widthEditText.setText(String.valueOf(location.getLatitude()));
        lengthEditText.setText(String.valueOf(location.getLongitude()));

        Button approval = view.findViewById(R.id.approval);
        approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Double latitude = Double.parseDouble(widthEditText.getText().toString());
                Double longitude = Double.parseDouble(lengthEditText.getText().toString());

                Location location = new Location();
                location.setLabel("מוקד אבטחה");
                location.setLevel(1);
                location.setRadius(10);
                location.setName("מוקד אבטחה");
                location.setLatitude(latitude);
                location.setLongitude(longitude);

                locationDao.insertLocation(location);
                updateLocationOnMap(location);
            }
        });

        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                , android.Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLocationOnMap(location);
    }

    private void updateLocationOnMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if( this.marker!=null)
        {
            this.marker.setPosition(latLng);
        }
        else {
            this.marker = mMap.addMarker(new MarkerOptions().position(latLng).title(location.getLabel()));
        }
        this.marker.showInfoWindow();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14.0f);
        mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);
    }

}