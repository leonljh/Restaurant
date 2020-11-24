package com.example.android.restaurant;

import android.location.Address;
import android.location.Geocoder;
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
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.List;

public class ResultsFragment extends DialogFragment {

    private TextView mRestaurantResult;
    private String randomedRestaurant, randomedRestaurantId;
    private Button letsGo,reRoll;

    public ResultsFragment(){ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

            super.onCreateView(inflater, container, savedInstanceState);
            return inflater.inflate(R.layout.results_layout_fragment, container,false);
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

            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        randomedRestaurantId = getArguments().getString(RestaurantsActivity.RESTAURANT_ID_KEY);
        randomedRestaurant = getArguments().getString(RestaurantsActivity.RESTAURANT_NAME_KEY);
        Log.i("Within Fragment", randomedRestaurant + " " + randomedRestaurantId);
    }

    private String getJsonForDirection(){
        //get geocoder and get current location using fusedlocationproviderclient
        //get latlng and pass into URL builder

        //uri builder to build uri with just location, radius 1.2km radius
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("nearbysearch")
                .appendPath("json")
                .appendQueryParameter("key", getResources().getString(R.string.google_maps_key));

        String myUrl = builder.build().toString();
        return myUrl;
    }

//    private void getCurrentLocationFragment() {
//        Geocoder geocoder = new Geocoder(getContext());
//        List<Address> locations = geocoder.getFromLocation()
//    }

}
