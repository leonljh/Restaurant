package com.example.android.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private EditText mUserInputLocation;
    private Button mSearchButton, mGPSButton, mRestaurantSearchButton;
    private Location lastKnownLocation, currentLocation;
    private static final int DEFAULT_ZOOM = 15;
    private final LatLng defaultLocation = new LatLng(-50, 151.2106085);
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static final String EXTRA_URL_KEY = "extra_url_key";

    //TODO Implement if user searches for restaurant with edit text view empty
    //TODO Handle where user phone location is not on
    //TODO use restaurantactivity to parse JSON file and display in recycler view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        mUserInputLocation = (EditText) findViewById(R.id.user_input_location);
        mSearchButton = (Button) findViewById(R.id.search_button);
        mGPSButton = (Button) findViewById(R.id.current_locator);
        mRestaurantSearchButton = (Button) findViewById(R.id.search_restaurants);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocation(v);
            }
        });

        mGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        mRestaurantSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String myUrl = getUrl();
                    //pass myUrl thru intent to next activity
                    Intent restaurantsIntent = new Intent(getApplicationContext(), RestaurantsActivity.class);
                    restaurantsIntent.putExtra(EXTRA_URL_KEY,myUrl);
                    startActivity(restaurantsIntent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        updateLocationUI();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //Uses fusedLocationProviderClient to get current location of the device and zooms camera to current location
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            Log.i("Search Restaurant", lastKnownLocation.toString());
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                LatLng location = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(location).title("You are here!"));
                            }
                        } else {
                            Log.d("MapsActivity", "Current location is null. Using defaults.");
                            Log.e("MapsActivity", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
            Toast.makeText(this, "Please Enter A Location To Start!", Toast.LENGTH_SHORT).show();
        }
    }

    private LatLng getDeviceLocationForRestaurant() {
        String location = mUserInputLocation.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                Toast.makeText(this, "Where are you?", Toast.LENGTH_SHORT).show();
            }

            if (addressList != null &&addressList.size() > 0) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                Toast.makeText(getApplicationContext(), address.toString(), Toast.LENGTH_LONG).show();
                return latLng;
            } else {
                Toast.makeText(this, "Please Enter Valid Location", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        return null;
    }


    //Searches for location by getting user input from EditText and Geocoder once search button is pressed
    public void searchLocation(View view) {

        String location = mUserInputLocation.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                Toast.makeText(this, "Where are you?", Toast.LENGTH_SHORT).show();
            }

            if (addressList != null &&addressList.size() > 0) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Eating Around Here?"));
                int zoomLevel = 16;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
            } else {
                Toast.makeText(this, "Please Enter Valid Location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getUrl(){

            LatLng newLatLng = getDeviceLocationForRestaurant();
            //uri builder to build uri with just location, radius 1.2km radius
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("maps.googleapis.com")
                    .appendPath("maps")
                    .appendPath("api")
                    .appendPath("place")
                    .appendPath("nearbysearch")
                    .appendPath("json")
                    .appendQueryParameter("location", newLatLng.latitude + "," + newLatLng.longitude)
                    .appendQueryParameter("radius", "1500")
                    .appendQueryParameter("type", "restaurant")
                    .appendQueryParameter("key", getResources().getString(R.string.google_maps_key));

            String myUrl = builder.build().toString();
            return myUrl;
    }
}