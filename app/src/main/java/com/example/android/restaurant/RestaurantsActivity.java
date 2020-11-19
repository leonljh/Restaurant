package com.example.android.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.android.restaurant.model.Restaurant;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class RestaurantsActivity extends AppCompatActivity {

    private String myUrl;
    private String jsonString;
    private JSONObject jsonObject;
    private List<Restaurant> listOfRestaurant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        //Getting myUrl string from previous activity
        Intent intent = getIntent();
        if (intent.hasExtra(MapsActivity.EXTRA_URL_KEY)) {
            myUrl = intent.getStringExtra(MapsActivity.EXTRA_URL_KEY);
            Log.i("URL", myUrl);
            getJson(myUrl);

            Log.i("Restaurants Activity", myUrl);
        } else {
            Log.i("Restaurants Activity", "None Found");
        }
    }

    private void getJson(String urlString) {
        //get JSON file from URL for parsing

        com.example.android.todolist.AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection con = null;
                try {
                    URL u = new URL(urlString);
                    con = (HttpsURLConnection) u.openConnection();

                    con.connect();


                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    jsonString = sb.toString();
                    listOfRestaurant = new ArrayList<>();

                    try {
                        jsonObject = new JSONObject(jsonString);

                        JSONArray arr = jsonObject.getJSONArray("results");

                        for (int i=0; i < arr.length(); i++) {
                            JSONObject res = arr.getJSONObject(i);

                            String restaurantName = res.getString("name");

                            double restaurantRating = -3;
                            if(res.has("rating")){
                                restaurantRating = res.getDouble("rating");
                            }

                            double restaurantUserRatings = -1;
                            if(res.has("user_ratings_total")) {
                                 restaurantUserRatings = res.getDouble("user_ratings_total");
                            }

                            int restaurantPrice = -1;
                            if(res.has("price_level")) {
                                restaurantPrice = res.getInt("price_level");
                            }

                            Restaurant restaurant = new Restaurant(restaurantName, restaurantRating,
                                    restaurantUserRatings, restaurantPrice);
                            listOfRestaurant.add(restaurant);
                        }

                        Log.i("Restaurant", listOfRestaurant.toString());

                    } catch (JSONException j){
                        j.printStackTrace();
                        Log.e("JSON", "JSON Creation Error");
                    }
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    if (con != null) {
                        try {
                            con.disconnect();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                return;
                }
            });
        }

    }
