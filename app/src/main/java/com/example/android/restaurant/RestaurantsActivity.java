package com.example.android.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.restaurant.model.Restaurant;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class RestaurantsActivity extends AppCompatActivity implements RestaurantAdapter.OnItemClicked {

    private String randomResultantRestaurant, randomResultantRestaurantId, token, jsonString, myUrl, nextPageUrl;
    private double randomRestaurantLat, randomRestaurantLng;
    private boolean isUrlLoaded = false;
    private boolean alreadyExecuted = false;
    private Button randomizeButton;
    private JSONObject jsonObject;
    private List<Restaurant> listOfRestaurant;
    private RecyclerView mRecyclerView;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    static final String RESTAURANT_ID_KEY = "restaurant_id_key";
    static final String RESTAURANT_NAME_KEY = "restaurant_name_key";
    static final String RESTAURANT_LAT = "latitude";
    static final String RESTAURANT_LNG = "longitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        mRecyclerView = (RecyclerView) findViewById(R.id.restaurant_recycler_view);
        contentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.finding_restaurants);
        randomizeButton = (Button) findViewById(R.id.button_random);
        listOfRestaurant = new ArrayList<>();

        //Getting myUrl string from previous activity
        Intent intent = getIntent();
        if (intent.hasExtra(MapsActivity.EXTRA_URL_KEY)) {
            myUrl = intent.getStringExtra(MapsActivity.EXTRA_URL_KEY);
            setLoadingScreen();
            getJson(myUrl);

            Log.i("Restaurants Activity", myUrl);

        } else {
            Log.i("Restaurants Activity", "None Found");
        }

        //create layout manager for recyclerview
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        //ensure recycler view has fixed size regardless of content
        mRecyclerView.setHasFixedSize(true);
        //pass in listOfRestaurants from JSON file as argument
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(listOfRestaurant);
        mRecyclerView.setAdapter(restaurantAdapter);

        //loads more data once scrolled finish
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                    if(dy > 0){
                        //build url with token
                        //parse new json file and get new results
                        //add to the list of restaurants
                       if(!isUrlLoaded){
                           nextPageUrlBuilder();
                           isUrlLoaded = true;
                       }
                       else{
                           //thread and parse the json file
                           //add it to the current list of restaurants
                           //remember to reset url once finished.
                           if(!alreadyExecuted) {
                               getAdditionalJson(nextPageUrl); //how to make this call once
                               alreadyExecuted = true;
                           }
                       }
                    }
            }
        });

        //Set ClickListener for each view in Recyclerview
        restaurantAdapter.setOnClick(RestaurantsActivity.this);
        randomizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomiseMyMealPlease();
                    ResultsFragment resultsFragment = new ResultsFragment();
                    //passes in the restaurant_Place_ID and the restaurant_Name to the fragment.
                    //make a method that gets your current location in the recyclerview listener,
                    //then pass it to the fragment
                    Bundle args = new Bundle();
                    args.putString(RESTAURANT_ID_KEY,randomResultantRestaurantId);
                    args.putString(RESTAURANT_NAME_KEY,randomResultantRestaurant);
                    args.putDouble(RESTAURANT_LAT, randomRestaurantLat);
                    args.putDouble(RESTAURANT_LNG, randomRestaurantLng);
                    resultsFragment.setArguments(args);
                    resultsFragment.show(getSupportFragmentManager(),"My Fragment");
            }
        });
    }

        @Override
        public void onItemClick(int position) {
            String myUrl = "google.navigation:q="+listOfRestaurant.get(position).getLat()+","+listOfRestaurant.get(position).getLng()+"&mode=w";
            Uri gmmIntentUri = Uri.parse(myUrl);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
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

                        try {

                            jsonObject = new JSONObject(jsonString);

                            //collect next_page_token
                            token = jsonObject.getString("next_page_token");

                            //parsing JSON data
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

                                String restaurantId = "";
                                if(res.has("place_id")){
                                    restaurantId = res.getString("place_id");
                                }

                                double restaurantLat = 0;
                                if(res.has("geometry")){
                                    restaurantLat = res.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                }

                                double restaurantLng = 0;
                                if(res.has("geometry")){
                                    restaurantLng = res.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                }

                                Restaurant restaurant = new Restaurant(restaurantName, restaurantRating,
                                        restaurantUserRatings, restaurantPrice, restaurantId, restaurantLat , restaurantLng);
                                listOfRestaurant.add(restaurant);
                            }

                            Log.i("Restaurant Success1", listOfRestaurant.toString());


                            //Log success, then open recyclerview
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    setmRecyclerView();
                                    mRecyclerView.getAdapter().notifyItemRangeInserted(0, listOfRestaurant.size()-1);
                                }
                            });

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

        //while getting http request and parsing JSON, show loading bar
        public void setLoadingScreen(){
            contentLoadingProgressBar.show();
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        //when JSON is parsed and ready, load back to recyclerview. this prevents the myRestaurants list from becoming null
        public void setmRecyclerView(){
            contentLoadingProgressBar.hide();
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        //gets a random int and passes it into the listOfRestaurant. Producing a toast of the restaurant
        public void randomiseMyMealPlease(){
            Random random = new Random();

            int max = listOfRestaurant.size() - 1;

            int randomRestaurantId = random.nextInt(max);
            randomResultantRestaurant = listOfRestaurant.get(randomRestaurantId).getName();
            randomResultantRestaurantId = listOfRestaurant.get(randomRestaurantId).getRestaurantId();
            randomRestaurantLat = listOfRestaurant.get(randomRestaurantId).getLat();
            randomRestaurantLng = listOfRestaurant.get(randomRestaurantId).getLng();

            Log.i("RestaurantFrag", randomResultantRestaurantId.toString() + randomResultantRestaurant);
        }

        private void nextPageUrlBuilder(){

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("maps.googleapis.com")
                    .appendPath("maps")
                    .appendPath("api")
                    .appendPath("place")
                    .appendPath("nearbysearch")
                    .appendPath("json")
                    .appendQueryParameter("pagetoken",token)
                    .appendQueryParameter("key",getResources().getString(R.string.google_maps_key));

            nextPageUrl = builder.build().toString();
            Log.i("Loading Next Page...", nextPageUrl);
        }

        private void getAdditionalJson(String url){
            com.example.android.todolist.AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    HttpsURLConnection con = null;
                    try {
                        URL u = new URL(url);
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

                        try {

                            jsonObject = new JSONObject(jsonString);

                            //collect next_page_token
                            //token = jsonObject.getString("next_page_token");

                            //parsing JSON data
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

                                String restaurantId = "";
                                if(res.has("place_id")){
                                    restaurantId = res.getString("place_id");
                                }

                                double restaurantLat = 0;
                                if(res.has("geometry")){
                                    restaurantLat = res.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                }

                                double restaurantLng = 0;
                                if(res.has("geometry")){
                                    restaurantLng = res.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                }

                                Restaurant restaurant = new Restaurant(restaurantName, restaurantRating,
                                        restaurantUserRatings, restaurantPrice, restaurantId, restaurantLat , restaurantLng);
                                listOfRestaurant.add(restaurant);
                            }
                            Log.i("Restaurant List Size", String.valueOf(listOfRestaurant.size()));
                            Log.i("Restaurant List Size", listOfRestaurant.toString());

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
