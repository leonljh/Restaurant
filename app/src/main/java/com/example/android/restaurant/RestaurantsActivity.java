package com.example.android.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class RestaurantsActivity extends AppCompatActivity {

    private String myUrl;
    private String jsonString;
    private String randomResultantRestaurant;
    private Button randomizeButton;
    private JSONObject jsonObject;
    private List<Restaurant> listOfRestaurant;
    private RecyclerView mRecyclerView;
    private ContentLoadingProgressBar contentLoadingProgressBar;


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

        randomizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomiseMyMealPlease();
                    ResultsFragment resultsFragment = new ResultsFragment(randomResultantRestaurant);
                    resultsFragment.show(getSupportFragmentManager(),"My Fragment");
            }
        });

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

                        Log.i("Restaurant List Success", listOfRestaurant.toString());


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
        private void randomiseMyMealPlease(){
            Random random = new Random();

            int max = listOfRestaurant.size() - 1;

            int randomRestaurantId = random.nextInt(max);
            randomResultantRestaurant = listOfRestaurant.get(randomRestaurantId).getName();
            //Toast.makeText(this, "Let's eat at: " + randomResultantRestaurant, Toast.LENGTH_LONG).show();
        }
    }
