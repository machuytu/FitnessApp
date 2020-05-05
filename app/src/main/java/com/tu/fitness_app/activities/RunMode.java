package com.tu.fitness_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.tu.fitness_app.R;

import java.util.Date;
import java.util.Locale;

public class RunMode extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private static final String TAG = "RunCountDown";
    private static final long milisStart = 30000;

    private TextView tvStep;
    private TextView tvTimer;
    private Button btnStartStop;
    private Button btnReset;
    private CountDownTimer timer;
    private boolean isRunning;
    private long millisTime;
    private int step;
    private int stepInSensor;
    private int stepAtReset;
    private int stepOnPause;
    private int stepBD;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private DatabaseReference getStepsRef() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        Date today = new Date();
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
        return mDatabase.child("RunMode").child(userId).child(date).child("totalsteps");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_mode);
        tvStep = findViewById(R.id.tv_step);
        tvTimer = findViewById(R.id.tv_timer);
        btnStartStop = findViewById(R.id.btn_start_stop);
        btnReset = findViewById(R.id.btn_reset);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

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
                        Intent intent = new Intent(RunMode.this, ListExercises.class);
                        startActivity(intent);
                        break;
                    case R.id.item2:
                        intent = new Intent(RunMode.this, Daily_Training.class);
                        startActivity(intent);
                        break;
                    case R.id.item3:
                        intent = new Intent(RunMode.this, Calendar.class);
                        startActivity(intent);
                        break;
                    case R.id.item4:
                        intent = new Intent(RunMode.this, SettingPage.class);
                        startActivity(intent);
                        break;
                    case R.id.item5:
                        intent = new Intent(RunMode.this, StepCountDaily.class);
                        startActivity(intent);
                        break;
                    case R.id.item6:
                        AuthUI.getInstance().signOut(RunMode.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(RunMode.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent myIntent = new Intent(RunMode.this, LoginActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                        startActivity(myIntent);
                        finish();
                    case R.id.item7:
                        intent = new Intent(RunMode.this, OverviewActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item8:
                        intent = new Intent(RunMode.this, HistoryActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item9:
                        intent = new Intent(RunMode.this, BarcodeScanner.class);
                        startActivity(intent);
                        break;
                    case R.id.item10:
                        intent = new Intent(RunMode.this, RunMode.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        millisTime = milisStart;
        isRunning = false;
        tvStep.setText(String.valueOf(0));
        setTVTimer();
        btnReset.setVisibility(View.INVISIBLE);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reset();
            }
        });

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    Stop();
                } else {
                    Start();
                }
            }
        });
    }

    private void Start() {
        timer = new CountDownTimer(millisTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisTime = millisUntilFinished;
                setTVTimer();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                getStepsRef().setValue(step + stepBD);
                step = 0;
                stepOnPause = step;
                stepAtReset = stepInSensor;
                btnStartStop.setText("Start");
                btnStartStop.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                setTVTimer();
            }
        }.start();

        isRunning = true;
        btnReset.setVisibility(View.INVISIBLE);
        btnStartStop.setText("Stop");

        Toast.makeText(this, "Start!",Toast.LENGTH_LONG).show();
    }

    private void Stop() {
        timer.cancel();

        isRunning = false;
        btnReset.setVisibility(View.VISIBLE);
        btnStartStop.setText("Start");
        stepOnPause = step;
        stepAtReset = stepInSensor;
        Toast.makeText(this, "Stop!",Toast.LENGTH_LONG).show();
    }

    private void Reset() {
        isRunning = false;
        millisTime = milisStart;
        btnReset.setVisibility(View.INVISIBLE);
        setTVTimer();

        stepOnPause = 0;
        stepAtReset = stepInSensor;
        tvStep.setText(String.valueOf(0));
        Toast.makeText(this, "Restart!",Toast.LENGTH_LONG).show();
    }

    private void setTVTimer () {
        int minutes = (int) (millisTime / 1000) / 60;
        int seconds = (int) (millisTime / 1000) % 60;
        String timeFormat = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        tvTimer.setText(timeFormat);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStepsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    stepBD = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                }
                else {
                    stepBD = 0;
                }
                Log.i(TAG, "stepDB = " + stepBD);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Sensor mSensorStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (mSensorStepCounter != null) {
            mSensorManager.registerListener(this, mSensorStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            Log.i(TAG, "Sensor step counter not found");
            Toast.makeText(this,"Sensor step counter not found!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (isRunning) {
                stepInSensor = (int) event.values[0];
                if (stepAtReset == 0) {
                    stepAtReset = stepInSensor;
                }
                step = stepInSensor - stepAtReset + stepOnPause;
                tvStep.setText(String.valueOf(step));
                Log.i(TAG, "Step: " + step);
            }
            else {
                stepAtReset = (int) event.values[0];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
