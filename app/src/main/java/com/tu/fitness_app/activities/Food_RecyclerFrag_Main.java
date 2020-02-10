package com.tu.fitness_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tu.fitness_app.R;

public class Food_RecyclerFrag_Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food__recycler_frag__main);

//        ìf(savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.rcFrag_main, Food_RecyclerView_Main.newInstance()).commit())
//        }
    }
}
