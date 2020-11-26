package com.example.android.restaurant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class ResultsFragment extends DialogFragment {

    private TextView mRestaurantResult;
    private String randomedRestaurant, randomedRestaurantPlaceId, myUrl;
    private Button letsGo, reRoll;
    private double randomedRestaurantLat, randomedRestaurantLng;

    public ResultsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.results_layout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRestaurantResult = (TextView) getView().findViewById(R.id.results_restaurant);
        letsGo = (Button) getView().findViewById(R.id.lets_go_button);
        reRoll = (Button) getView().findViewById(R.id.nah_man_no_thanks);

        mRestaurantResult.setText("Random Restaurant: " + randomedRestaurant);

        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open google maps and set destination.
                String url = getUrlForJSON();
                Uri gmmIntentUri = Uri.parse(url);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        randomedRestaurantPlaceId = getArguments().getString(RestaurantsActivity.RESTAURANT_ID_KEY);
        randomedRestaurant = getArguments().getString(RestaurantsActivity.RESTAURANT_NAME_KEY);
        randomedRestaurantLat = getArguments().getDouble(RestaurantsActivity.RESTAURANT_LAT);
        randomedRestaurantLng = getArguments().getDouble(RestaurantsActivity.RESTAURANT_LNG);

        Log.i("Within Fragment", randomedRestaurant + " " + randomedRestaurantPlaceId + " " + randomedRestaurantLat + " " + randomedRestaurantLng );
    }


    //We have to get the current location from fusedLocationProviderClient as you never know, the user
    //is probably on the move while searching for restaurant.
//    private void getCurrentLocationFragment() {
//        fusedLocationProviderClient = LocationServices
//                .getFusedLocationProviderClient(getActivity());
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        Task<Location> locationResult =  fusedLocationProviderClient.getLastLocation();
//        locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
//
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if(task.isSuccessful()) {
//                    currentLocation = task.getResult();
//                    if(currentLocation != null){
//                        LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                        Log.i("Fragment Location Chk", location.toString());
//
//                        Geocoder geocoder = new Geocoder(getActivity());
//                        try {
//                            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
//                            currentLocationPostalCode = addresses.get(0).getPostalCode();
//                            Log.i("AutocompleteFrag Test", currentLocationPostalCode);
//                            getUrlForJSON();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
//        });
//    }

    private String getUrlForJSON(){
        //google.navigation:q=latitude,longitude
        myUrl = "google.navigation:q="+randomedRestaurantLat+","+randomedRestaurantLng+"&mode=w";
        Log.i("Fragment URL", myUrl);
        return myUrl;
    }
}
