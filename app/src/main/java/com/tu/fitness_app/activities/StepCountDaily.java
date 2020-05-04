package com.tu.fitness_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.tu.fitness_app.Model.Steps;
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

    public static int mSeriesMax = 0;

    /**
     * Data series index used for controlling animation of {@link DecoView}. These are set when
     * the data series is created then used in {@link #createEvents} to specify what series to
     * apply a given event to
     */
    private int mBackIndex;
    private int mSeriesIndex;

    private SensorManager sensorManager;

    public static int evsteps;
    private int stepAtStart;
    private int stepInDB;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    TextView tvPercentage;
    TextView tvRemaining;
    TextView tvRun;

    // Firebase database init
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private DatabaseReference getStepsRef() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        Date today = new Date();
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
        return mDatabase.child("Steps").child(userId).child(date).child("totalsteps");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count_daily);

        tvRemaining = findViewById(R.id.textRemaining);
        tvPercentage = findViewById(R.id.textPercentage);
        tvRun = findViewById(R.id.textRun);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDecoView = findViewById(R.id.dynamicArcView);
        mSeriesMax = SetGoalActivity.mSeries;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

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
                        Intent intent = new Intent(StepCountDaily.this, ListExercises.class);
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
                    case R.id.item10:
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

        if (mSeriesMax == 0) {
            mSeriesMax = LoginActivity.mSeries1;
        }
        if(mSeriesMax > 0) {
            // Create required data series on the DecoView
            createDataSeries();

            // Setup events to be fired on a schedule
            createEvents();
        }
    }

    private void createDataSeries() {
        mBackIndex = mDecoView.addSeries(new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mSeriesMax, mSeriesMax)
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
                    tvRemaining.setText(String.format("%d Steps to goal", (int) (seriesItem.getMaxValue() - currentPosition)));

                    float percentFilled = ((currentPosition - seriesItem.getMinValue())
                            / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                    tvPercentage.setText(String.format("%.0f%%", percentFilled * 100));

                }
                else{
                    tvRemaining.setText("Completed");
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
//                .setDuration(3000)
//                .setDelay(30)
                .build());

        getStepsRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    stepInDB = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                }
                else {
                    stepInDB = 0;
                }
                Log.i("TAG", "stepDB = " + stepInDB);
                evsteps = stepInDB;

                mDecoView.addEvent(new DecoEvent.Builder(evsteps)
                        .setIndex(mSeriesIndex)
                        .build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor sensorStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (sensorStepCounter != null) {
            sensorManager.registerListener(this, sensorStepCounter, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Step count sensor not available!", Toast.LENGTH_LONG).show();
            Log.i("log_err", "Step count sensor not available!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getStepsRef().setValue(evsteps);
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
            Log.i("TAG", "total steps = " + evsteps);

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
