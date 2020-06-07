package com.tu.fitness_app.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.tu.fitness_app.Model.StepCalculate;
import com.tu.fitness_app.R;

import java.util.Date;

public class StepCountDaily extends AppCompatActivity implements SensorEventListener {
    /**
     * DecoView animated arc based chart
     */
    private DecoView mDecoView;

    /**
     * Maximum value for each data series in the {@link DecoView}. This can be different for each
     * data series, in this example we are applying the same all data series
     */

    private int mSeriesMax = 0;

    /**
     * Data series index used for controlling animation of {@link DecoView}. These are set when
     * the data series is created then used in {@link #createEvents} to specify what series to
     * apply a given event to
     */
    private int mBackIndex;
    private int mSeriesIndex;

    private static final String TAG = "Step count daily";
    private SensorManager sensorManager;

    public static int evsteps;
    private int stepAtStart;
    private int stepInDB;

    private TextView tvPercentage;
    private TextView tvRun;

    // Firebase database init
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;

    Date today = new Date();
    final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();

    private DatabaseReference getRef() {
        return mDatabase.child("DailyWalk").child(userId).child(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count_daily);
        mDecoView = findViewById(R.id.dynamicArcView);
        tvPercentage = findViewById(R.id.textPercentage);
//        tvRun = findViewById(R.id.textRun);

        createNavBar();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        StepCalculate.mode = 0; //walk

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = LoginActivity.USER_ID;
        Log.i(TAG, "uid" + userId);
        if (userId.equals(""))
            userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(userId).child("stepgoal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSeriesMax = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                createDataSeries();
                createEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StepCalculate stepCalculate;
                if (dataSnapshot.exists()) {
                    stepCalculate = dataSnapshot.getValue(StepCalculate.class);
                }
                else {
                    stepCalculate = new StepCalculate();
                    getRef().setValue(stepCalculate);
                }
                stepInDB = stepCalculate.getTotalsteps();
                evsteps = stepInDB;
                mDecoView.addEvent(new DecoEvent.Builder(evsteps)
                        .setIndex(mSeriesIndex)
                        .setDuration(1000)
                        .build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "get step in DB Error:", databaseError.toException());
            }
        });


    }

    private void createNavBar() {
        // Navigation Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
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

        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawerLayout)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.item0:
                        Intent intent = new Intent(StepCountDaily.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item1:
                        intent = new Intent(StepCountDaily.this, ListExercises.class);
                        startActivity(intent);
                        break;
                    case R.id.item2:
                        intent = new Intent(StepCountDaily.this, Daily_Training.class);
                        startActivity(intent);
                        break;
                    case R.id.item3:
                        intent = new Intent(StepCountDaily.this, Calendar.class);
                        startActivity(intent);
                        break;
                    case R.id.item4:
                        intent = new Intent(StepCountDaily.this, SettingPage.class);
                        startActivity(intent);
                        break;
                    case R.id.item5:
                        intent = new Intent(StepCountDaily.this, StepCountDaily.class);
                        startActivity(intent);
                        break;
                    case R.id.item6:
                        AuthUI.getInstance().signOut(StepCountDaily.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(StepCountDaily.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent myIntent = new Intent(StepCountDaily.this, LoginActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                        startActivity(myIntent);
                        finish();
                    case R.id.item7:
                        intent = new Intent(StepCountDaily.this, OverviewActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item8:
                        intent = new Intent(StepCountDaily.this, HistoryActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item9:
                        intent = new Intent(StepCountDaily.this, BarcodeScanner.class);
                        startActivity(intent);
                        break;
                    case R.id.item11:
                        intent = new Intent(StepCountDaily.this, RunMode.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });
    }
    private void createDataSeries() {

        mBackIndex = mDecoView.addSeries(new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build());

        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                tvRun.setText(String.format("%d Steps", (int) currentPosition));

                if (seriesItem.getMaxValue() > currentPosition){
//                    tvRemaining.setText(String.format("%d Steps to goal", (int) (seriesItem.getMaxValue() - currentPosition)));

                    float percentFilled = ((currentPosition - seriesItem.getMinValue())
                            / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                    tvPercentage.setText(String.format("%.0f%%", percentFilled * 100));

                }
                else{
//                    tvRemaining.setText("Completed");
                    tvPercentage.setText("100%");
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) { }
        });

        mSeriesIndex = mDecoView.addSeries(seriesItem);
    }

    private void createEvents() {
        mDecoView.executeReset();

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
                .setIndex(mSeriesIndex)
                .setDelay(0)
                .setDuration(1000)
                .setDisplayText("")
                .setListener(new DecoEvent.ExecuteEventListener() {
                    @Override
                    public void onEventStart(DecoEvent event) {
                    }

                    @Override
                    public void onEventEnd(DecoEvent event) {
                    }
                }).build());

        mDecoView.addEvent(new DecoEvent.Builder(mSeriesMax)
                .setIndex(mBackIndex)
                .setDuration(1000)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(evsteps)
                .setIndex(mSeriesIndex)
                .setDuration(1000)
                .build());

    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor sensorStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (sensorStepCounter != null) {
            sensorManager.registerListener(this, sensorStepCounter, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Step count sensor not available!", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Step count sensor not available!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getRef().setValue(new StepCalculate(evsteps));
        sensorManager.unregisterListener(this);
    }

    // Step Counter
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int stepInSensor = (int) event.values[0];
            if (stepAtStart == 0)
                stepAtStart = stepInSensor;
            evsteps = stepInSensor - stepAtStart + stepInDB;
            Log.i(TAG, "total steps = " + evsteps);

            // Draw
            mDecoView.addEvent(new DecoEvent.Builder(evsteps)
                    .setIndex(mSeriesIndex)
                    .setDuration(3250)
                    .build());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
