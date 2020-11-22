package com.example.android.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

public class ResultsFragment extends DialogFragment {

    private TextView mRestaurantResult;
    private String randomedRestaurant;
    private Button letsGo,reRoll;

    public ResultsFragment(String result){
        randomedRestaurant = result;
    }

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
    }
}
