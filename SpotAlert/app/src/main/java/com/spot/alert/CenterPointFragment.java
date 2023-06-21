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
import com.spot.alert.utils.GeoUtils;
import com.spot.alert.validators.LocationValidation;
import com.spot.alert.validators.ValidateResponse;

public class CenterPointFragment extends Fragment implements OnMapReadyCallback {
    private LocationDao locationDao;
    private GoogleMap mMap;
    private Location location;

    private LatLng latLng;
    private Marker marker;
    private EditText editTextLongitude;
    private EditText editTextLatitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.center_point_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao();
        this.location = locationDao.getLocationByName(SpotAlertAppContext.CENTER_POINT_STRING);

        editTextLongitude = view.findViewById(R.id.width);
        editTextLatitude = view.findViewById(R.id.length);
        EditText zoomEditText = view.findViewById(R.id.zoom);
        if (location != null && location.getLatitude()!=null && location.getLongitude()!=null) {
            editTextLatitude.setText(String.valueOf(location.getLatitude()));
            editTextLongitude.setText(String.valueOf(location.getLongitude()));
            zoomEditText.setText(String.valueOf(location.getZoom()));
        } else {
            location = new Location();
            location.setLabel(SpotAlertAppContext.CENTER_POINT_STRING);
            location.setLevel(1);
            location.setRadius(10);
            location.setName(SpotAlertAppContext.CENTER_POINT_STRING);
            location.setId(locationDao.insertLocation(location));

            selectLocation();
        }

        Button approval = view.findViewById(R.id.createLocationApproval);
        approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Double latitude = Double.parseDouble(editTextLatitude.getText().toString());
                Double longitude = Double.parseDouble(editTextLongitude.getText().toString());
                Double zoom = Double.parseDouble(zoomEditText.getText().toString());

                location.setLatitude(latitude);
                location.setLongitude(longitude);
                location.setZoom(zoom);
                locationDao.updateLocation(location);

                Toast toast = Toast.makeText(getActivity(), "מוקד נשמר בהצלחה", Toast.LENGTH_SHORT);
                toast.show();

                updateLocationOnMap(location);
            }
        });
        ImageButton locationItemButton = view.findViewById(R.id.locationItemButton);

        locationItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLocation();
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                updateNewLocation(latLng);
            }
        });

        if (location != null && location.getLatitude()!=null && location.getLongitude()!=null) {
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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, location.getZoom().floatValue());
        mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);
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

    private void updateNewLocation(LatLng latLng) {

        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);

        editTextLatitude.setText(GeoUtils.getFormattedPoint(latLng.latitude));
        editTextLongitude.setText(GeoUtils.getFormattedPoint(latLng.longitude));

        validateLocationPoint();

        updateLocationOnMap(location);
    }

    private ValidateResponse validateLocationPoint() {

        ValidateResponse validateResponse = LocationValidation.validateLocation(location);

        if (!validateResponse.isValidate()) {
            editTextLongitude.setError(validateResponse.getMsg());
            editTextLatitude.setError(validateResponse.getMsg());
        } else {
            editTextLongitude.setError(null);
            editTextLatitude.setError(null);

        }

        return validateResponse;
    }

}