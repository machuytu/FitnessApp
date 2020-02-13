package com.tu.fitness_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.natasa.progressviews.CircleProgressBar;
import com.natasa.progressviews.utils.OnProgressViewListener;
import com.tu.fitness_app.Adapter.Food_MyRecycleAdapter;
import com.tu.fitness_app.R;

public class OverviewActivity extends AppCompatActivity {

    private float food_calories;
    private static float stepMax = 0f;
    private static float caloriesMax = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // Count Calories
        food_calories = Food_MyRecycleAdapter.caloriesCount;
        Log.d("Calories for Overview", String.valueOf(Food_RecyclerFrag_Main.calRef1));

        // Setting Steps and Calories
        stepMax = SetGoalActivity.mSeries;
        if (stepMax == 0) {
            stepMax = LoginActivity.mSeries1;
        }
        
        caloriesMax = SetGoalActivity.mSeries1;
        if (caloriesMax == 0) {
            caloriesMax = LoginActivity.mSeries2;
        }

        final CircleProgressBar steps = findViewById(R.id.step_progress);
        final CircleProgressBar food = findViewById(R.id.food_progress);

        // Animation
        TranslateAnimation translation_steps;
        translation_steps = new TranslateAnimation(0f,0F,0f,180);
        translation_steps.setStartOffset(100);
        translation_steps.setDuration(2000);
        translation_steps.setFillAfter(true);
        translation_steps.setInterpolator(new BounceInterpolator());

        TranslateAnimation translation_food;
        translation_food = new TranslateAnimation(0f, 0F, 0f, 220);
        translation_food.setStartOffset(100);
        translation_food.setDuration(2000);
        translation_food.setFillAfter(true);
        translation_food.setInterpolator(new BounceInterpolator());

        // Steps Progress Bar
        steps.setProgress(100 * (StepCountDaily.evsteps) / stepMax);
        steps.setWidth(280);
        steps.setWidthProgressBackground(25);
        steps.setWidthProgressBarLine(20);
        steps.setText(StepCountDaily.evsteps + "/" + stepMax);
        steps.setTextSize(40);
        steps.setBackgroundColor(Color.LTGRAY);
        steps.setRoundEdgeProgress(true);
        steps.startAnimation(translation_steps);

        // Food Progress Bar
        if (food_calories > 0) {
            food.setProgress((100 * (food_calories)) / caloriesMax);
            food.setText(food_calories + "/ " + caloriesMax);
        } else {
            food.setProgress((100 * (LoginActivity.calRef)) / caloriesMax);
            food.setText(LoginActivity.calRef + "/ " + caloriesMax);
        }
        food.setWidth(200);
        food.setWidthProgressBackground(25);
        food.setWidthProgressBarLine(40);
        food.setTextSize(70);
        food.setBackgroundColor(Color.LTGRAY);
        food.setRoundEdgeProgress(true);
        food.startAnimation(translation_food);

        // Listeners
        steps.setOnProgressViewListener(new OnProgressViewListener() {
            float progress = 0;

            @Override
            public void onFinish() {
                // Do Something On Progress Finish
                steps.setText("done!");
                // circleProgressBar.resetProgressBar();
            }

            @Override
            public void onProgressUpdate(float progress) {
                steps.setText("" + (int) progress);
                setProgress(progress);
            }

            @Override
            public void setProgress(float prog) {
                progress = prog;
            }

            @Override
            public int getprogress() {
                return (int) progress;
            }
        });

        food.setOnProgressViewListener(new OnProgressViewListener() {
            float progress = 0;

            @Override
            public void onFinish() {
                //do something on progress finish
                food.setText("full calories");
            }

            @Override
            public void onProgressUpdate(float progress) {
                food.setText("" + (int) progress);
                setProgress(progress);
            }

            @Override
            public void setProgress(float prog) {
                progress = prog;
            }

            @Override
            public int getprogress() {
                return (int) progress;
            }
        });

        // On Click Listeners For Activities
        final ImageView food_summary = findViewById(R.id.food_summary);
        food_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OverviewActivity.this, FoodSummaryActivity.class);
                startActivity(intent);
            }
        });

        final ImageView share_a_run = findViewById(R.id.share_a_run);
        share_a_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(OverviewActivity.this, AskLocationActivity.class);
//                startActivity(intent);
            }
        });

        // Add calories
        ImageView addCal = findViewById(R.id.addcalories);
        addCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OverviewActivity.this, Food_RecyclerFrag_Main.class);
                startActivity(intent);
            }
        });
    }
}
