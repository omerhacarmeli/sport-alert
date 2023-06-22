package com.spot.alert;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.spot.alert.adapter.location.LocationAdapter;
import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.ImageEntityDao;
import com.spot.alert.database.LocationDao;
import com.spot.alert.dataobjects.ImageEntity;
import com.spot.alert.utils.GeoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocationFragment extends Fragment implements LocationReceiver.OnLocationStateListener, LocationListener, OnMapReadyCallback {

    private LocationDao locationDao;
    private ImageEntityDao imageEntityDao;
    private LocationReceiver locationReceiver;
    private double latitude, longitude;
    private GoogleMap mMap;
    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private LocationAdapter adapter;
    private RecyclerView recyclerView;
    private ClickListener deleteListener;
    private ClickListener editListener;
    private ClickListener clickListener;
    private ClickListener testLocationListener;
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

        this.locationReceiver = new LocationReceiver(this);// making a objrct of LocationReceiver

        this.locationDao = AppDataBase.getDatabase(getActivity()).locationDao();//bringing the data base
        this.imageEntityDao = AppDataBase.getDatabase(getActivity()).imageEntityDao();//bringing the image from he the data base
        // in here I'm asking for premission to use the loction on the phone
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                , android.Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);//using the location manager

        // Request location updates
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//check if i have acsses to the GPS

            Log.i("About GPS", "GPS is Enabled in your device");
        } else {
            GeoUtils.alertDialogEnableLocation(getActivity());
        }
//getting the map fragment
        FragmentManager fm = getActivity().getSupportFragmentManager();
        supportMapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);

        List<com.spot.alert.dataobjects.Location> list = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView); //referencing the recycleView
        deleteListener = new ClickListener() { //creating a delete listener
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.Location) { // checking if it is from location kind

                    com.spot.alert.dataobjects.Location location = (com.spot.alert.dataobjects.Location) obj;//casting giving the object to location

                    if (SpotAlertAppContext.CENTER_POINT_STRING.equals(location.getName())) { //checking if it is from center point
                        Toast.makeText(getActivity(), "לא ניתן למחוק את מוקד אבטחה", Toast.LENGTH_LONG).show(); //toast message of can't delete the point
                        return; // getting out if the delete listener
                    }
                    // removing the marker from the map
                    if (markerMap.get(location.getId()) != null) {

                        Marker removedMarker = markerMap.remove(location.getId()); // bringing the id
                        removedMarker.remove();// removing the marker from the map
                    }

                    locationDao.deleteLocation(location); // deleting the location from the map
                    // deleting the image of the place from the data base
                    if (location.getImageId() != null) { //check if it is difference from null
                        ImageEntity imageEntity = new ImageEntity(); // creating an object of imageEntity
                        imageEntity.setId(location.getImageId()); // setting the id of the deleting location
                        imageEntityDao.deleteImageEntity(imageEntity); // deleting the image of the place from the data base
                    }

                    Toast.makeText(getActivity(), location.getName() + " נמחק בהצלחה", Toast.LENGTH_LONG).show();//toast message of sucsseful deleting
                }
            }
        };

        editListener = new ClickListener() {// crating an edit listener of edit location
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.Location) {// checking if it is from location kind

                    com.spot.alert.dataobjects.Location location = (com.spot.alert.dataobjects.Location) obj; // giving the object to location

                    Bundle bundle = new Bundle();
                    bundle.putLong("locationId", location.getId());
                    getActivity().getIntent().putExtras(bundle);

                    ((MainActivity) getActivity()).moveEditLocation(location); //moving to moveEditLocation class
                }
            }
        };

        clickListener = new ClickListener() { // click listener of markerring the map
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.Location) {// checking if it is from location kind
                    com.spot.alert.dataobjects.Location location = (com.spot.alert.dataobjects.Location) obj;// giving the object to location
                    if (markerMap.get(location.getId()) != null) {// checking if the marker is difference from null
                        markerMap.get(location.getId()).showInfoWindow();// giving the name of the place on the map
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())); // updating the Longitude and Latitude on the camera
                        mMap.animateCamera(cameraUpdate); // update animation
                        mMap.moveCamera(cameraUpdate);// update camera
                    }
                }
            }
        };

        testLocationListener = new ClickListener() {// in this function we give to the user an informetion on how far he is from the spot
            @Override
            public void click(Object obj) {
                if (obj instanceof com.spot.alert.dataobjects.Location) {// checking if it is from location kind
                    com.spot.alert.dataobjects.Location location = (com.spot.alert.dataobjects.Location) obj;// giving the object to location

                    if (locationManager.isLocationEnabled()) {//return if location is on or off
                        //bring the location in kilometers
                        double distanceFromLatLonInKm = GeoUtils.getDistanceFromLatLonInKm(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(latitude, longitude));
                        //locations in meters
                        int distanceFromLatLonInMeter = (int) (distanceFromLatLonInKm * 1000);
                        new AlertDialog.Builder(getContext())//telling the user how far he is from the location
                                .setMessage("המרחק שלך מהנקודה " + distanceFromLatLonInMeter + " מטר")
                                .setCancelable(true)
                                .setPositiveButton(
                                        "סגור",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                dialog.cancel();
                                            }
                                        }).create()
                                .show();

                    } else {

                        GeoUtils.alertDialogEnableLocation(getActivity());
                    }
                }
            }
        };

        FloatingActionButton addLocationFB = (FloatingActionButton) view.findViewById(
                R.id.addLocationFB);//giving referance to the adding location fab

        addLocationFB.setOnClickListener(new View.OnClickListener() {//making listener to the fab
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).moveCreateLocation();//moving to CreatingLocation
            }
        });

        adapter = new LocationAdapter(getActivity(), deleteListener, editListener, clickListener, testLocationListener);//crating an adapter to the location
        recyclerView.setAdapter(adapter);//here we are doing set to the recycleView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) { //in here we are making that the addingLocationFab will not appear if we are in the buttom of the recylcer view

                if (dy > 0) {
                    addLocationFB.hide();// will not appear
                } else {
                    addLocationFB.show();//will appear
                }
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        loadLiveData();//here updating the the list
    }

    @Override
    public void onStart() {//starts when the fragment is starts working
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
    public void onMapReady(GoogleMap googleMap) {//in here when the map is ready googleMaps will gives us the map
        SpotAlertAppContext.googleMap = googleMap;
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);//for pressing on the map and moving
        mMap.getUiSettings().setZoomControlsEnabled(true);//zoom

        updateLocationsOnMap();
    }

    @Override
    public void onLocationChanged(Location location) {// in here we are giving the latitude and longitude the location
        // Get the latitude and longitude
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // Do something with the coordinates
        Log.d("MyApp", "Latitude: " + latitude + ", Longitude: " + longitude);
    }

    @Override
    public void onLocationStateChange() {// a function that checks if the location is Off or On

        if (locationManager.isLocationEnabled()) {

            Log.i("About GPS", "GPS is Enabled in your device");
            Toast toast = Toast.makeText(getActivity(), "המיקום שלך הופעל", Toast.LENGTH_SHORT);
            toast.show();

        } else {
            GeoUtils.alertDialogEnableLocation(getActivity());
        }
    }

    private void loadLiveData() {
        this.locationDao.getLocations().observe(getActivity(), new Observer<List<com.spot.alert.dataobjects.Location>>() {
            @Override
            public void onChanged(List<com.spot.alert.dataobjects.Location> locationList) { //sign to live data observe
                //מחזיר לייב דטה שיודע לטפל ברשימה של משתמשים
                //בכל פעם שיש עידכון על המשתמשים הלייב דטה יביא לי רשימה חדשה
                locations = locationList;

                adapter.setDataChanged(locations); //update the adapter witha new list

                updateLocationsOnMap(); //update the location on the map
            }
        });
    }

    private void updateLocationsOnMap() { // updating the locations on the map after updates
        if (mMap != null && locations != null && !locations.isEmpty()) { //if non of the object are empties

            com.spot.alert.dataobjects.Location center = getCenterPoint(locations);

            if (center == null || center.getLatitude() == null || center.getLongitude() == null) {
                return;
            }

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(center.getLatitude(), center.getLongitude()), center.getZoom().floatValue());

            mMap.animateCamera(cameraUpdate);
            mMap.moveCamera(cameraUpdate);//animetion for movement

            List<Marker> markers = new ArrayList<>();// take the list of the markers

            for (com.spot.alert.dataobjects.Location location : locations) { //goes over the list of location

                if (!markerMap.containsKey(location.getId())) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(location.getLabel()));
                    markerMap.put(location.getId(), marker);
                    markers.add(marker);
                } else {
                    markers.add(markerMap.get(location.getId()));
                }
            }

            mMap.animateCamera(cameraUpdate);
            mMap.moveCamera(cameraUpdate);

            markers.get(0).showInfoWindow();
        }
    }

    private com.spot.alert.dataobjects.Location getCenterPoint(List<com.spot.alert.dataobjects.Location> locations) {

        for (com.spot.alert.dataobjects.Location location : locations) {
            if (SpotAlertAppContext.CENTER_POINT_STRING.equals(location.getName()))
            {
                return location;
            }
        }

        return null;
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    /**
     * Called when the provider this listener is registered with becomes disabled. If a provider is
     * disabled when this listener is registered, this callback will be invoked immediately.
     *
     * <p class="note">Note that this method only has a default implementation on Android R and
     * above, and this method must still be overridden in order to run successfully on Android
     * versions below R. LocationListenerCompat from the compat libraries may be used to avoid the
     * need to override for older platforms.
     *
     * @param provider the name of the location provider
     */
    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
