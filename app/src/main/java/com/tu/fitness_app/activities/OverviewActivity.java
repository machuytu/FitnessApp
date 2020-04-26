package com.tu.fitness_app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.natasa.progressviews.CircleProgressBar;
import com.natasa.progressviews.utils.OnProgressViewListener;
import com.tu.fitness_app.R;

public class OverviewActivity extends AppCompatActivity {

    public float food_calories = 0f;
    public static int stepMax = 0;
    public static float caloriesMax = 0f;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        food_calories = LoginActivity.calRef;

        // Setting Steps and Calories
        getUsersRef("stepgoal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stepMax = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (stepMax == 0) {
            stepMax = LoginActivity.mSeries1;
        }

        getUsersRef("caloriegoal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                caloriesMax = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (caloriesMax == 0) {
            caloriesMax = LoginActivity.mSeries2;
        }

        final CircleProgressBar steps = findViewById(R.id.step_progress);
        final CircleProgressBar food = findViewById(R.id.food_progress);

        // Navigation Bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.item1:
                        Intent intent = new Intent(OverviewActivity.this, ListExercises.class);
                        startActivity(intent);
                        break;
                    case R.id.item2:
                        intent = new Intent(OverviewActivity.this, Daily_Training.class);
                        startActivity(intent);
                        break;
                    case R.id.item3:
                        intent = new Intent(OverviewActivity.this, Calendar.class);
                        startActivity(intent);
                        break;
                    case R.id.item4:
                        intent = new Intent(OverviewActivity.this, SettingPage.class);
                        startActivity(intent);
                        break;
                    case R.id.item5:
                        intent = new Intent(OverviewActivity.this, StepCountDaily.class);
                        startActivity(intent);
                        break;
                    case R.id.item6:
                        AuthUI.getInstance().signOut(OverviewActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(OverviewActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent myIntent = new Intent(OverviewActivity.this, LoginActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                        startActivity(myIntent);
                        finish();
                    default:
                        break;
                    case R.id.item7:
                        intent = new Intent(OverviewActivity.this, OverviewActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item8:
                        intent = new Intent(OverviewActivity.this, HistoryActivity.class);
                        startActivity(intent);
                        break;
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });

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
            food.setProgress((100 * LoginActivity.calRef / caloriesMax));
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
        food_summary.setOnClickListener(v -> {
            Intent intent = new Intent(OverviewActivity.this, FoodSummaryActivity.class);
            startActivity(intent);
        });

        // Add calories
        ImageView addCal = findViewById(R.id.addcalories);
        addCal.setOnClickListener(v -> {
            Intent intent = new Intent(OverviewActivity.this, Food_RecyclerFrag_Main.class);
            startActivity(intent);
        });
    }

    private DatabaseReference getUsersRef(String ref) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        return mDatabase.child("Users").child(userId).child(ref);
    }
}
