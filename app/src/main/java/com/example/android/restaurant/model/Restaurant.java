package com.example.android.restaurant.model;

public class Restaurant {

    public Restaurant(String name, double rating, double userRatings, int price, String restaurantId, double lat, double lng) {
        this.name = name;
        this.rating = rating;
        this.userRatings = userRatings;
        this.price = price;
        this.restaurantId = restaurantId;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", rating=" + rating +
                ", userRatings=" + userRatings +
                ", price=" + price +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(double userRatings) {
        this.userRatings = userRatings;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getRestaurantId(){
        return this.restaurantId;
    }

    public void setRestaurantLat(String restaurantId){
        this.restaurantId = restaurantId;
    }

    public Double getLat() {return this.lat;}

    public Double getLng() {return this.lng;}

    private String name;
    private double userRatings, lat, lng, rating;
    private int price;
    private String restaurantId;


}
