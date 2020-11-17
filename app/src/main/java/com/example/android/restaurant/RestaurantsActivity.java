package com.example.android.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RestaurantsActivity extends AppCompatActivity {

    String myUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        //Getting myUrl string from previous activity
        Intent intent = getIntent();
        if(intent.hasExtra(MapsActivity.EXTRA_URL_KEY)){
            myUrl = intent.getStringExtra(MapsActivity.EXTRA_URL_KEY);
            Log.i("Restaurants Activity", myUrl);
        }
        else{
            Log.i("Restaurants Activity", "None Found");
        }
    }
}