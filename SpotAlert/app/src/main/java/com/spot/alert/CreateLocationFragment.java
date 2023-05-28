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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import com.spot.alert.database.ImageEntityDao;
import com.spot.alert.database.LocationDao;
import com.spot.alert.database.LocationTimeRangeDao;
import com.spot.alert.dataobjects.ImageEntity;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.utils.BitMapUtils;
import com.spot.alert.utils.GeoUtils;
import com.spot.alert.validators.LocationValidation;
import com.spot.alert.validators.ValidateResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateLocationFragment extends Fragment implements OnMapReadyCallback, OnRequestPermissionsResultCallback {

    public static final String DEFAULT_NAME = "נקודה_1";
    private LocationDao locationDao;

    private LocationTimeRangeDao locationTimeRangeDao;

    private ImageEntityDao imageEntityDao;
    private GoogleMap mMap;
    private Location centerlocation;
    private Location newlocation;

    private ImageEntity imageEntity;
    private Marker centerLocationMarker;

    private Marker newLocationMarker;
    private TimeRangeAdapter timeRangeAdapter;
    private RecyclerView recyclerView;
    private ClickListener deleteListener;

    private EditText createLocationNameEditText;

    private EditText createLocationSpotEditText;

    private List<LocationTimeRange> locationTimeRangeList = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#.#####");
    private String imagePath;
    private static int CAMERA_REQUEST_CODE = 1111111222;

    ImageView spotImage;

    final long maxBytes = 1024 * 1024;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_location_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }

        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao();
        this.locationTimeRangeDao = AppDataBase.getDatabase(getActivity()).locationTimeRangeDao();
        this.imageEntityDao = AppDataBase.getDatabase(getActivity()).imageEntityDao();

        this.centerlocation = locationDao.getLocationByName(SpotAlertAppContext.CENTER_POINT_STRING);

        createLocationNameEditText = view.findViewById(R.id.createLocationName);
        createLocationSpotEditText = view.findViewById(R.id.createLocationSpot);

        this.imageEntity = new ImageEntity();

        this.newlocation = new Location();
        this.newlocation.setLabel(DEFAULT_NAME);
        this.newlocation.setName(DEFAULT_NAME);
        createLocationNameEditText.setText(DEFAULT_NAME);
        createLocationNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText editText = (EditText) v;

                    newlocation.setName(editText.getText().toString());
                    newlocation.setLabel(editText.getText().toString());

                    if (newLocationMarker != null) {
                        newLocationMarker.setTitle(newlocation.getLabel());
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
                newlocation.setName(createLocationNameEditText.getText().toString());
                newlocation.setLabel(createLocationNameEditText.getText().toString());

                boolean validateLocation = validateLocationName().isValidate();
                boolean validateLocationPoint = validateLocationPoint().isValidate();
                boolean validateLocationTimeRange = validateLocationTimeRange().isValidate();

                if (!validateLocation || !validateLocationPoint || !validateLocationTimeRange) {
                    Toast toast = Toast.makeText(getActivity(), "נתוני המיקום אינם תקינים", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } else {

                    long locationId = locationDao.insertLocation(newlocation);

                    if(imageEntity.getImageData()!=null) {
                        Long imageId = imageEntityDao.insertImageEntity(imageEntity);
                        newlocation.setImageId(imageId);
                    }



                    for (LocationTimeRange locationTimeRange : locationTimeRangeList) {
                        locationTimeRange.setLocationId(locationId);
                        AppDataBase.databaseWriteExecutor.submit(() -> locationTimeRangeDao.insertLocation(locationTimeRange));
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
        fm.beginTransaction().
                replace(R.id.map, supportMapFragment).
                commit();
        supportMapFragment.getMapAsync(this);


        spotImage = view.findViewById(R.id.pointImage);
        spotImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create a file to save the image
                    File imageFile = createImageFile();
                    if (imageFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(getActivity(), "com.spot.alert.fileprovider", imageFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    }
                }

            }

            private File createImageFile() {
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File imageFile = null;
                try {
                    imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
                    imagePath = imageFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return imageFile;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            // Image captured successfully, you can now retrieve the image using the imagePath
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            Bitmap scaledBitmap = BitMapUtils.scaleBitmap(bitmap, maxBytes);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            byte[] imageData = outputStream.toByteArray();

            spotImage.setImageBitmap(scaledBitmap);
            imageEntity.setImageData(imageData);
        }
    }

    private ValidateResponse validateLocationTimeRange() {
        ValidateResponse validateResponse = LocationValidation.validateLocationTimeRange(locationTimeRangeList);

        if (!validateResponse.isValidate()) {
            Toast.makeText(getActivity(), validateResponse.getMsg(), Toast.LENGTH_LONG).show();
        }

        return validateResponse;
    }

    private ValidateResponse validateLocationName() {

        ValidateResponse validateResponse = LocationValidation.validateName(newlocation);

        if (!validateResponse.isValidate()) {
            createLocationNameEditText.setError(validateResponse.getMsg());
        } else {
            Location locationByName = locationDao.getLocationByName(newlocation.getName());

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

        ValidateResponse validateResponse = LocationValidation.validateLocation(newlocation);

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

        if (centerlocation != null) {
            updateCenterLocationOnMap();
        }
    }

    private void updateNewLocation(LatLng latLng) {

        newlocation.setLatitude(latLng.latitude);
        newlocation.setLongitude(latLng.longitude);

        createLocationSpotEditText.setText("(" + df.format(latLng.latitude) + "," + df.format(latLng.longitude) + ")");

        validateLocationPoint();

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, requestCode);
            } else {
                // Permission denied, handle accordingly (e.g., display an error message)
            }
        }
    }
}