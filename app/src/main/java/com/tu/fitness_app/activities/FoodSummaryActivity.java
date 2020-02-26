package com.tu.fitness_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;

import com.natasa.progressviews.CircleProgressBar;
import com.tu.fitness_app.Adapter.Food_MyRecycleAdapter;
import com.tu.fitness_app.R;

public class FoodSummaryActivity extends AppCompatActivity {
    float food_fat;
    float food_carbs;
    float food_protein;
    float max_fat;
    float max_carbs;
    float max_protein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_summary);

        final CircleProgressBar fats = findViewById(R.id.fats_progress);
        final CircleProgressBar carbs = findViewById(R.id.carbs_progress);
        final CircleProgressBar protein = findViewById(R.id.protein_progress);

        food_fat = Food_MyRecycleAdapter.totalFat;
        food_carbs = Food_MyRecycleAdapter.totalCarbs;
        food_protein = Food_MyRecycleAdapter.totalProtein;

        max_fat = OverviewActivity.caloriesMax * 35 / 100;
        max_carbs = OverviewActivity.caloriesMax * 35 / 100;
        max_protein = OverviewActivity.caloriesMax * 40 / 100;

        Log.d("max fat", String.valueOf(max_fat));
        Log.d("max carb", String.valueOf(max_carbs));
        Log.d("max protein", String.valueOf(max_protein));
        Log.d("food fat", String.valueOf(food_fat));
        Log.d("food carb", String.valueOf(food_carbs));
        Log.d("food protein", String.valueOf(food_protein));

        // Animation
        TranslateAnimation translation_fat;
        translation_fat = new TranslateAnimation(0f, 0F, 0f, 180);
        translation_fat.setStartOffset(100);
        translation_fat.setDuration(2000);
        translation_fat.setFillAfter(true);
        translation_fat.setInterpolator(new BounceInterpolator());

        TranslateAnimation translation_carbs;
        translation_carbs = new TranslateAnimation(0f, 0F, 0f, 180);
        translation_carbs.setStartOffset(100);
        translation_carbs.setDuration(2000);
        translation_carbs.setFillAfter(true);
        translation_carbs.setInterpolator(new BounceInterpolator());

        TranslateAnimation translation_protein;
        translation_protein = new TranslateAnimation(0f, 0F, 0f, 370);
        translation_protein.setStartOffset(100);
        translation_protein.setDuration(2000);
        translation_protein.setFillAfter(true);
        translation_protein.setInterpolator(new BounceInterpolator());

        // Fats Progress Bar
        if (food_fat > 0) {
            fats.setProgress((100 * (food_fat)) / max_fat);
        } else {
            fats.setProgress((100 * (LoginActivity.user_fat)) / max_fat);
            fats.setWidthProgressBackground(25);
            fats.setWidthProgressBarLine(25);
        }
        if (food_fat > 0) {
            fats.setText(String.valueOf(food_fat));
        } else {
            fats.setText(String.valueOf(LoginActivity.user_fat));
        }
        fats.setTextSize(35);
        fats.setBackgroundColor(Color.LTGRAY);
        fats.setRoundEdgeProgress(true);
        fats.startAnimation(translation_fat);

        // Carbs Progress Bar
        if (food_carbs > 0) {
            carbs.setProgress((100 * (food_carbs)) / max_carbs);
        } else {
            carbs.setProgress((100 * (LoginActivity.user_carbs)) / max_carbs);
            carbs.setWidthProgressBackground(25);
            carbs.setWidthProgressBarLine(25);
        }
        if (food_carbs > 0) {
            carbs.setText(String.valueOf(food_carbs));
        } else {
            carbs.setText(String.valueOf(LoginActivity.user_carbs));
        }
        carbs.setTextSize(35);
        carbs.setBackgroundColor(Color.LTGRAY);
        carbs.setRoundEdgeProgress(true);
        carbs.startAnimation(translation_carbs);

        // Protein Progress Bar
        if (food_protein > 0) {
            protein.setProgress((100 * (food_protein)) / max_protein);
        } else {
            protein.setProgress((100 * (LoginActivity.user_protein)) / max_protein);
            protein.setWidthProgressBackground(25);
            protein.setWidthProgressBarLine(25);
        }
        if (food_protein > 0) {
            protein.setText(String.valueOf(food_protein));
        } else {
            protein.setText(String.valueOf(LoginActivity.user_protein));
        }
        protein.setTextSize(35);
        protein.setBackgroundColor(Color.LTGRAY);
        protein.setRoundEdgeProgress(true);
        protein.setAnimation(translation_protein);
    }

    private int getDisplayHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
