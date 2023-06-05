package com.spot.alert;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.spot.alert.adapter.timerange.ITimeRange;
import com.spot.alert.adapter.timerange.TimeRangeAdapter;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.ImageEntityDao;
import com.spot.alert.database.LocationDao;
import com.spot.alert.database.LocationTimeRangeDao;
import com.spot.alert.dataobjects.ImageEntity;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.utils.CameraOnClickListenerHandler;
import com.spot.alert.utils.GeoUtils;
import com.spot.alert.validators.LocationValidation;
import com.spot.alert.validators.TimeRangeValidation;
import com.spot.alert.validators.ValidateResponse;

import java.util.ArrayList;
import java.util.List;

public class EditLocationFragment extends Fragment implements OnMapReadyCallback {
    private LocationDao locationDao;
    private LocationTimeRangeDao locationTimeRangeDao;
    private ImageEntityDao imageEntityDao;
    private GoogleMap mMap;
    private Location centerLocation;
    private Location editLocation;
    private ImageEntity imageEntity;
    private Marker centerLocationMarker;
    private Marker newLocationMarker;
    private TimeRangeAdapter timeRangeAdapter;
    private RecyclerView recyclerView;
    private ClickListener deleteListener;
    private EditText editLocationNameEditText;
    private EditText editLocationSpotEditText;
    private List<ITimeRange> locationTimeRangeList = new ArrayList<>();
    private List<ITimeRange> deletedTimeRangeList = new ArrayList<>();
    private CameraOnClickListenerHandler cameraOnClickListenerHandler;
    private ImageView locationImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_location_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.cameraOnClickListenerHandler = new CameraOnClickListenerHandler(this.getActivity(), this);

        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao();
        this.locationTimeRangeDao = AppDataBase.getDatabase(getActivity()).locationTimeRangeDao();
        this.imageEntityDao = AppDataBase.getDatabase(getActivity()).imageEntityDao();

        this.centerLocation = locationDao.getLocationByName(SpotAlertAppContext.CENTER_POINT_STRING);

        editLocationNameEditText = view.findViewById(R.id.editLocationName);
        editLocationSpotEditText = view.findViewById(R.id.editLocationSpot);

        Bundle bundle = getActivity().getIntent().getExtras();
        Long locationId = bundle.getLong("locationId");

        this.editLocation = locationDao.getLocation(locationId);

        locationImage = view.findViewById(R.id.pointImage);

        if (this.editLocation.getImageId() != null) {
            this.imageEntity = imageEntityDao.getImageEntity(this.editLocation.getImageId());
            if (this.imageEntity != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageEntity.getImageData(), 0, imageEntity.getImageData().length);
                locationImage.setImageBitmap(bitmap);
            }
        } else {
            this.imageEntity = new ImageEntity();
        }

        editLocationNameEditText.setText(this.editLocation.getName());
        editLocationSpotEditText.setText("(" + GeoUtils.getFormattedPoint(editLocation.latitude) + "," + GeoUtils.getFormattedPoint(editLocation.longitude) + ")");

        this.locationTimeRangeList = mapTimeRangeList(this.locationTimeRangeDao.getLocationTimeRangesByLocationId(this.editLocation.getId()));

        editLocationNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText editText = (EditText) v;

                    editLocation.setName(editText.getText().toString());
                    editLocation.setLabel(editText.getText().toString());

                    if (newLocationMarker != null) {
                        newLocationMarker.setTitle(editLocation.getLabel());
                    }

                    validateLocationName();
                }
            }
        });

        deleteListener = new ClickListener() {
            @Override
            public void click(Object obj) {
                if (obj instanceof LocationTimeRange) {

                    LocationTimeRange locationTimeRange = (LocationTimeRange) obj;

                    locationTimeRangeList.remove(locationTimeRange);

                    if (locationTimeRange.getId() != null) {
                        deletedTimeRangeList.add(locationTimeRange);
                    }

                    timeRangeAdapter.setDataChanged(locationTimeRangeList);

                    Toast.makeText(getActivity(), "הגדרת שעה נמחקה בהצלחה", Toast.LENGTH_LONG).show();
                }
            }
        };

        Button editLocationApproval = view.findViewById(R.id.editLocationApproval);
        Button editLocationCancel = view.findViewById(R.id.editLocationCancel);

        ImageButton locationItemButton = view.findViewById(R.id.locationItemButton);

        locationItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLocation();
            }
        });

        editLocationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity()).setMessage("האם אתה מעוניין לצאת ללא שמירת הנתונים?")
                        .setCancelable(true).setPositiveButton(
                                "כן",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ((MainActivity) getActivity()).moveLocation();
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
        });

        editLocationApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editLocation.setName(editLocationNameEditText.getText().toString());
                editLocation.setLabel(editLocationNameEditText.getText().toString());

                boolean validateLocation = validateLocationName().isValidate();
                boolean validateLocationPoint = validateLocationPoint().isValidate();
                boolean validateLocationTimeRange = validateLocationTimeRange().isValidate();

                if (!validateLocation || !validateLocationPoint || !validateLocationTimeRange) {
                    Toast toast = Toast.makeText(getActivity(), "נתוני המיקום אינם תקינים", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } else {


                    if (imageEntity.getImageData() != null) {
                        Long imageId = imageEntityDao.insertImageEntity(imageEntity);
                        editLocation.setImageId(imageId);
                    }

                    locationDao.updateLocation(editLocation);

                    for (ITimeRange timeRange : locationTimeRangeList) {
                        LocationTimeRange locationTimeRange = (LocationTimeRange) timeRange;
                        locationTimeRange.setLocationId(editLocation.getId());
                        AppDataBase.databaseWriteExecutor.submit(() -> locationTimeRangeDao.insertLocationTimeRange(locationTimeRange));
                    }

                    for (ITimeRange deletedLocationTimeRange : deletedTimeRangeList) {

                        AppDataBase.databaseWriteExecutor.submit(() -> locationTimeRangeDao.deleteLocationTimeRange((LocationTimeRange) deletedLocationTimeRange));
                    }

                    Toast toast = Toast.makeText(getActivity(), "הנקודה נשמרה בהצלחה", Toast.LENGTH_SHORT);
                    toast.show();
                    ((MainActivity) getActivity()).moveLocation();
                }
            }
        });

        ActivityCompat.requestPermissions(
                getActivity(), new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION
                                , Manifest.permission.ACCESS_COARSE_LOCATION
                        }, PackageManager.PERMISSION_GRANTED);

        recyclerView
                = (RecyclerView) view.findViewById(
                R.id.recyclerView);
        timeRangeAdapter = new TimeRangeAdapter(getActivity(), deleteListener, null);
        recyclerView.setAdapter(timeRangeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton addTimeRangeFB = (FloatingActionButton) view.findViewById(
                R.id.addTimeRageFB);

        addTimeRangeFB.setOnClickListener(new View.OnClickListener() {
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
        fm.beginTransaction().

                replace(R.id.map, supportMapFragment).

                commit();
        supportMapFragment.getMapAsync(this);

        timeRangeAdapter.setDataChanged(locationTimeRangeList);

        locationImage.setOnClickListener(this.cameraOnClickListenerHandler);
    }

    private List<ITimeRange> mapTimeRangeList(List<LocationTimeRange> locationTimeRangeList) {

        List<ITimeRange> timeRangeList = new ArrayList<>();

        for (LocationTimeRange locationTimeRange : locationTimeRangeList) {
            timeRangeList.add(locationTimeRange);
        }

        return timeRangeList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraOnClickListenerHandler.CAMERA_REQUEST_CODE) {
            CameraOnClickListenerHandler.CameraImage cameraImage = cameraOnClickListenerHandler.onActivityResultGetCameraImage();
            if (cameraImage != null) {
                locationImage.setImageBitmap(cameraImage.getBitmap());
                imageEntity.setImageData(cameraImage.getImageData());
            }
        }
    }

    private ValidateResponse validateLocationTimeRange() {
        ValidateResponse validateResponse = TimeRangeValidation.validateTimeRange(locationTimeRangeList);

        if (!validateResponse.isValidate()) {
            Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_LONG).show();
        }

        return validateResponse;
    }

    private ValidateResponse validateLocationName() {


        ValidateResponse validateResponse = LocationValidation.validateName(editLocation);

        if (!validateResponse.isValidate()) {
            editLocationNameEditText.setError(validateResponse.getMsg());
        } else {

            Location locationByName = locationDao.getLocationByName(editLocation.getName());

            if (locationByName != null && !locationByName.getId().equals(editLocation.getId())) {

                validateResponse = new ValidateResponse(false, "השם של המקום קיים במערכתת בחר שם חדש");

                editLocationNameEditText.setError(validateResponse.getMsg());
                return validateResponse;
            } else {
                editLocationNameEditText.setError(null);
            }
        }

        return validateResponse;
    }

    private ValidateResponse validateLocationPoint() {

        ValidateResponse validateResponse = LocationValidation.validateLocation(editLocation);

        if (!validateResponse.isValidate()) {
            editLocationSpotEditText.setError(validateResponse.getMsg());
        } else {
            editLocationSpotEditText.setError(null);
        }

        return validateResponse;
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

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        if (centerLocation != null) {
            updateCenterLocationOnMap();
        }

        if (editLocation != null) {
            updateNewLocationOnMap();
        }
    }

    private void updateNewLocation(LatLng latLng) {

        editLocation.setLatitude(latLng.latitude);
        editLocation.setLongitude(latLng.longitude);

        editLocationSpotEditText.setText(GeoUtils.getFormattedLatLng(latLng));

        validateLocationPoint();

        updateNewLocationOnMap();
    }

    private void updateCenterLocationOnMap() {

        LatLng latLng = new LatLng(centerLocation.getLatitude(), centerLocation.getLongitude());

        if (this.centerLocationMarker != null) {
            this.centerLocationMarker.setPosition(latLng);
        } else {
            this.centerLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(centerLocation.getLabel()));
        }
        this.centerLocationMarker.showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, centerLocation.getZoom().floatValue());
        mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);
    }

    private void updateNewLocationOnMap() {

        LatLng latLng = new LatLng(editLocation.getLatitude(), editLocation.getLongitude());

        if (this.newLocationMarker != null) {
            this.newLocationMarker.setPosition(latLng);
        } else {
            this.newLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(editLocation.getLabel()));
        }
        this.centerLocationMarker.showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);
    }
}