package com.spot.alert;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class CreateLocationFragment extends Fragment implements OnMapReadyCallback {

    public static final String DEFAULT_NAME = "נקודה_1";
    private LocationDao locationDao;

    private LocationTimeRangeDao locationTimeRangeDao;

    private ImageEntityDao imageEntityDao;
    private GoogleMap mMap;
    private Location centerLocation;
    private Location newLocation;
    private ImageEntity imageEntity;
    private Marker centerLocationMarker;
    private Marker newLocationMarker;
    private TimeRangeAdapter timeRangeAdapter;
    private RecyclerView recyclerView;
    private ClickListener deleteListener;
    private EditText createLocationNameEditText;
    private EditText createLocationSpotEditText;
    private List<ITimeRange> locationTimeRangeList = new ArrayList<>();
    private ImageView locationImage;
    private CameraOnClickListenerHandler cameraOnClickListenerHandler;

    private ActivityResultLauncher<Intent> startCamera;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_location_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CameraOnClickListenerHandler.CAMERA_REQUEST_CODE);
        }

        ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {

                        CameraOnClickListenerHandler.CameraImage cameraImage = cameraOnClickListenerHandler.onActivityResultGetCameraImage();
                        if (cameraImage != null) {
                            locationImage.setImageBitmap(cameraImage.getBitmap());
                            imageEntity.setImageData(cameraImage.getImageData());
                        }

                    }
                }
        );

        this.cameraOnClickListenerHandler = new CameraOnClickListenerHandler(this.getActivity(), this, startCamera);

        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao();
        this.locationTimeRangeDao = AppDataBase.getDatabase(getActivity()).locationTimeRangeDao();
        this.imageEntityDao = AppDataBase.getDatabase(getActivity()).imageEntityDao();

        this.centerLocation = locationDao.getLocationByName(SpotAlertAppContext.CENTER_POINT_STRING);

        createLocationNameEditText = view.findViewById(R.id.createLocationName);
        createLocationSpotEditText = view.findViewById(R.id.createLocationSpot);

        this.imageEntity = new ImageEntity();

        this.newLocation = new Location();
        this.newLocation.setLabel(DEFAULT_NAME);
        this.newLocation.setName(DEFAULT_NAME);
        createLocationNameEditText.setText(DEFAULT_NAME);
        createLocationNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText editText = (EditText) v;

                    newLocation.setName(editText.getText().toString());
                    newLocation.setLabel(editText.getText().toString());

                    if (newLocationMarker != null) {
                        newLocationMarker.setTitle(newLocation.getLabel());
                    }

                    validateLocationName();
                }
            }
        });

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

        createLocationApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newLocation.setName(createLocationNameEditText.getText().toString());
                newLocation.setLabel(createLocationNameEditText.getText().toString());

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
                        newLocation.setImageId(imageId);
                    }

                    long locationId = locationDao.insertLocation(newLocation);

                    for (ITimeRange timeRange : locationTimeRangeList) {
                        LocationTimeRange locationTimeRange = (LocationTimeRange) timeRange;

                        locationTimeRange.setLocationId(locationId);
                        AppDataBase.databaseWriteExecutor.submit(() -> locationTimeRangeDao.insertLocationTimeRange(locationTimeRange));
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
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                                , android.Manifest.permission.ACCESS_COARSE_LOCATION
                        }, PackageManager.PERMISSION_GRANTED);

        recyclerView
                = (RecyclerView) view.findViewById(
                R.id.recyclerView);
        timeRangeAdapter = new

                TimeRangeAdapter(getActivity(), deleteListener);
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


        locationImage = view.findViewById(R.id.pointImage);

        locationImage.setOnClickListener(cameraOnClickListenerHandler);
    }

    private ValidateResponse validateLocationTimeRange() {
        ValidateResponse validateResponse = TimeRangeValidation.validateTimeRange(locationTimeRangeList);

        if (!validateResponse.isValidate()) {
            Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_LONG).show();
        }

        return validateResponse;
    }

    private ValidateResponse validateLocationName() {

        ValidateResponse validateResponse = LocationValidation.validateName(newLocation);

        if (!validateResponse.isValidate()) {
            createLocationNameEditText.setError(validateResponse.getMsg());
        } else {
            Location locationByName = locationDao.getLocationByName(newLocation.getName());

            if (locationByName != null) {
                validateResponse = new ValidateResponse(false, "השם של המקום קיים במערכתת בחר שם חדש");

                createLocationNameEditText.setError(validateResponse.getMsg());
                return validateResponse;
            } else {
                createLocationNameEditText.setError(null);
            }
        }

        return validateResponse;
    }

    private ValidateResponse validateLocationPoint() {

        ValidateResponse validateResponse = LocationValidation.validateLocation(newLocation);

        if (!validateResponse.isValidate()) {
            createLocationSpotEditText.setError(validateResponse.getMsg());
        } else {
            createLocationSpotEditText.setError(null);
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
    }

    private void updateNewLocation(LatLng latLng) {

        newLocation.setLatitude(latLng.latitude);
        newLocation.setLongitude(latLng.longitude);

        createLocationSpotEditText.setText("(" + GeoUtils.getFormattedPoint(latLng.latitude) + "," + GeoUtils.getFormattedPoint(latLng.longitude) + ")");

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

        LatLng latLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

        if (this.newLocationMarker != null) {
            this.newLocationMarker.setPosition(latLng);
        } else {
            this.newLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(newLocation.getLabel()));
        }
        this.centerLocationMarker.showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}