package com.spot.alert;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_location_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // asking premission for using the camera
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
        this.cameraOnClickListenerHandler = new CameraOnClickListenerHandler(this.getActivity(), this, startCamera); //listener for the camera

        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao(); // taking the data of the locations
        this.locationTimeRangeDao = AppDataBase.getDatabase(getActivity()).locationTimeRangeDao();// taking the data of the locations time range
        this.imageEntityDao = AppDataBase.getDatabase(getActivity()).imageEntityDao();// taking the data of the images

        this.centerLocation = locationDao.getLocationByName(SpotAlertAppContext.CENTER_POINT_STRING);//in here im taking the cenetr point location

        createLocationNameEditText = view.findViewById(R.id.createLocationName);// name that was given to the location
        createLocationSpotEditText = view.findViewById(R.id.createLocationSpot); // coordinates of the location

        this.imageEntity = new ImageEntity();// creating a image object

        this.newLocation = new Location(); // creating a user object
        this.newLocation.setLabel(DEFAULT_NAME); // setting a default label to the new location
        this.newLocation.setName(DEFAULT_NAME);// setting a default name to the new location (נקודה_1)
        createLocationNameEditText.setText(DEFAULT_NAME); // adding the default name to the editText
        createLocationNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // listener to the new location creation
            @Override
            public void onFocusChange(View v, boolean hasFocus) { // updating when it on focus
                if (!hasFocus) {
                    EditText editText = (EditText) v;

                    newLocation.setName(editText.getText().toString());// setting a name to the new location
                    newLocation.setLabel(editText.getText().toString());// setting a label to the new location

                    if (newLocationMarker != null) { // if there is no marker on the location
                        newLocationMarker.setTitle(newLocation.getLabel()); // setting a title on the marker on the map
                    }

                    validateLocationName(); // checking if validate
                }
            }
        });

        deleteListener = new ClickListener() {// delete listener for deleting location time range
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.LocationTimeRange) { //checking if they are from the same kind

                    LocationTimeRange locationTimeRange = (LocationTimeRange) obj; //casting

                    locationTimeRangeList.remove(locationTimeRange);// removing from the list

                    timeRangeAdapter.setDataChanged(locationTimeRangeList); // sending the new list to the adapter and updating the recycle view

                    Toast.makeText(getActivity(), "הגדרת שעה נמחקה בהצלחה", Toast.LENGTH_LONG).show(); //toast message that the time has been deleted
                }
            }
        };

        Button createLocationApproval = view.findViewById(R.id.createLocationApproval); // approve button
        Button createLocationCancel = view.findViewById(R.id.createLocationCancel);// delete button
        ImageButton locationItemButton = view.findViewById(R.id.locationItemButton);// craeting button for taking the location you are in right now
        locationItemButton.setOnClickListener(new View.OnClickListener() {// in this listener we taking the location from the GPS
            @Override
            public void onClick(View v) {
                selectLocation();
            } // moving to select location
        });

        createLocationCancel.setOnClickListener(new View.OnClickListener() { // in this listener we cancel the creation on the location and then returning to locationFragment
            @Override
            public void onClick(View view) {
                //creating alertDialog
                new AlertDialog.Builder(getActivity()).setMessage("האם אתה מעוניין לצאת ללא שמירת הנתונים?") // asking if the user wants to exit
                        .setCancelable(true).setPositiveButton( // if yes
                                "כן",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ((MainActivity) getActivity()).moveLocation(); // moving back to locationFragment
                                    }
                                })
                        .setNegativeButton( // if not, nothing happen and alertDialog in cancel
                                " לא",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .create().show();
            }
        });

        createLocationApproval.setOnClickListener(new View.OnClickListener() {// creating listener to the approval button
            @Override
            public void onClick(View view) {
                newLocation.setName(createLocationNameEditText.getText().toString()); // setting a name to the new location
                newLocation.setLabel(createLocationNameEditText.getText().toString());// setting a label to the new location

                boolean validateLocation = validateLocationName().isValidate(); // checking If the name is validate
                boolean validateLocationPoint = validateLocationPoint().isValidate();// checking of the location point is validate
                boolean validateLocationTimeRange = validateLocationTimeRange().isValidate();// checking of the time range are validate

                if (!validateLocation || !validateLocationPoint || !validateLocationTimeRange) { // checking if one of them aren't validate
                    // toast message that theinformetion isn't validate
                    Toast toast = Toast.makeText(getActivity(), "נתוני המיקום אינם תקינים", Toast.LENGTH_SHORT);
                    toast.show();
                    return;// getting out of the listener
                } else { //if everything is validate
                    if (imageEntity.getImageData() != null) { //and image entity is not null
                        // im inserting the image to the data base and taking the id
                        Long imageId = imageEntityDao.insertImageEntity(imageEntity);
                        newLocation.setImageId(imageId); //combines between the location and image
                     }
                    // im inserting the newLocation to the data base and taking the id
                    long locationId = locationDao.insertLocation(newLocation);
                   // goes over the time range list
                    for (ITimeRange timeRange : locationTimeRangeList) {
                        LocationTimeRange locationTimeRange = (LocationTimeRange) timeRange; // casting

                        locationTimeRange.setLocationId(locationId); // combines between the location the time range
                        AppDataBase.databaseWriteExecutor.submit(() -> locationTimeRangeDao.insertLocationTimeRange(locationTimeRange));
                    }
                    // toast message that says that the point has been save
                    Toast toast = Toast.makeText(getActivity(), "הנקודה נשמרה בהצלחה", Toast.LENGTH_SHORT);
                    toast.show();
                    ((MainActivity) getActivity()).moveLocation(); // going back to locationFragment
                }
            }

        });

        ActivityCompat.requestPermissions(getActivity(), new String[]//asking for permission
                        {
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                                , android.Manifest.permission.ACCESS_COARSE_LOCATION
                        }, PackageManager.PERMISSION_GRANTED);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView); //referece for recycle view
        timeRangeAdapter = new TimeRangeAdapter(getActivity(), deleteListener); // creating the adapter
        recyclerView.setAdapter(timeRangeAdapter);// setting the adapter in the recycle view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton addTimeRangeFB = (FloatingActionButton) view.findViewById( // creating fab for adding time
                R.id.addTimeRageFB);

        addTimeRangeFB.setOnClickListener(new View.OnClickListener() { // creating listener for the fab
            @Override
            public void onClick(View v) {
                LocationTimeRange locationTimeRange = new LocationTimeRange(); // creating a location time range object
                locationTimeRange.setDayWeek(1); // setting the first day has 1  because the its starting from 0
                locationTimeRangeList.add(locationTimeRange);// adding the new time range to the list
                timeRangeAdapter.setDataChanged(locationTimeRangeList);// sending the list to the adapter
            }
        });
        // put the map on the fragment map
        FragmentManager fm = getActivity().getSupportFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
        locationImage = view.findViewById(R.id.pointImage);
        locationImage.setOnClickListener(cameraOnClickListenerHandler);
    }

    private ValidateResponse validateLocationTimeRange() {//LocationTimeRange is a function that checks that time ranges are validate
        // sending the time range to the validateTimeRange and gets in return boolean and msg-message
        ValidateResponse validateResponse = TimeRangeValidation.validateTimeRange(locationTimeRangeList);

        if (!validateResponse.isValidate()) { // if it's difference from true
            Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_LONG).show(); // shows the toast message
        }

        return validateResponse; // returning the boolean
    }

    private ValidateResponse validateLocationName() {//validateLocationName checks if the name of the location is validate
     // sending the time range to the validateName and gets in return boolean and msg-message
        ValidateResponse validateResponse = LocationValidation.validateName(newLocation);

        if (!validateResponse.isValidate()) {// if it's difference from true
            createLocationNameEditText.setError(validateResponse.getMsg()); //set error to the editText and message
        } else {
            //inserting the newLocation name to the locationByName
            Location locationByName = locationDao.getLocationByName(newLocation.getName());

            if (locationByName != null) { // if not null
                //toast message of location is exist
                validateResponse = new ValidateResponse(false, "השם של המקום קיים במערכתת בחר שם חדש");

                createLocationNameEditText.setError(validateResponse.getMsg()); // set error
                return validateResponse; // return the boolean
            } else {
                createLocationNameEditText.setError(null); // set error is null
            }
        }

        return validateResponse; // return the boolean
    }
    //validateLocationPoint checks if the location point is validate
    private ValidateResponse validateLocationPoint() {
       // sending the newLocation to the validateName and gets in return boolean and msg-message
        ValidateResponse validateResponse = LocationValidation.validateLocation(newLocation);

        if (!validateResponse.isValidate()) {// if it's difference from true
            createLocationSpotEditText.setError(validateResponse.getMsg());//set error to the editText and message
        } else { // if not
            createLocationSpotEditText.setError(null); // set error null
        }

        return validateResponse; //return the boolean
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

        if(centerLocation == null)
        {
            return;
        }

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