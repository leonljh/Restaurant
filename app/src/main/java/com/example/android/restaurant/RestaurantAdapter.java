package com.example.android.restaurant;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.restaurant.model.Restaurant;

import org.w3c.dom.Text;

import java.util.List;


public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> myRestaurants;

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        public TextView restaurantName;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurant_id_num);
        }
    }

    public RestaurantAdapter(List<Restaurant> restaurants){
        this.myRestaurants = restaurants;
    }

    @NonNull
    @Override
    public RestaurantAdapter.RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForRestaurantItem = R.layout.restaurant_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForRestaurantItem, parent, shouldAttachToParentImmediately);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.RestaurantViewHolder holder, int position) {

            String restaurant = myRestaurants.get(position).getName();
            holder.restaurantName.setText(restaurant);

            holder.restaurantName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked.onItemClick(position);
                }
            });

    }

    @Override
    public int getItemCount() {
        return myRestaurants.size();
    }

    @Override
    public int getItemViewType(int position) {
        //this adds button to the first position of the recycler view
        return R.layout.restaurant_list_item;
    }

    //set restaurant method
    private OnItemClicked onItemClicked;

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public void setOnClick(OnItemClicked onClick){
        this.onItemClicked = onClick;
    }
}
