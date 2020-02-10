package com.tu.fitness_app.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

class Food_RecyclerView_Main extends Fragment {
    private static final String ARG_MOVIE = "R.id.mdf_main_replace";
//    FoodDataJson foodData;


    public static Food_RecyclerView_Main newInstance() {
        Food_RecyclerView_Main fragment = new Food_RecyclerView_Main();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE, "R.id.rcmain");
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
//        foodData = new FoodDataJson();
    }
}
