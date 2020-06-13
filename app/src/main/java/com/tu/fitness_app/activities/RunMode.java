package com.tu.fitness_app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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


//import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.natasa.progressviews.CircleProgressBar;
import com.tu.fitness_app.Model.StepCalculate;
import com.tu.fitness_app.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class RunMode extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "RunCountDown";
//    private CircularProgressBar circleProgress;
    private SensorManager mSensorManager;
    private CountDownTimer timer;
    private CircleProgressBar runprogress;
    private boolean isRunning;
    private long timeStart;
    private long timeLeft;
    private int step;
    private int stepInSensor;
    private int stepAtReset;
    private int stepOnPause;
    private int stepBD;

    private TextView tvStep;
    private TextView tvTimer;
    private Button btnStart;
    private Button btnStop;
    private Button btnReset;
    private Spinner spinnerTime;

    private ArrayList<String> listString;
    private ArrayList<Long> listTime;

    private DatabaseReference mDatabase;
//    private FirebaseAuth mAuth;
    private String userId;

    Date today = new Date();
    final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();

    private DatabaseReference getRef() {
        return mDatabase.child("RunMode").child(userId).child(date);
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
                        Intent intent = new Intent(RunMode.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item1:
                        intent = new Intent(RunMode.this, ListExercises.class);
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
                    case R.id.item11:
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_mode);
//        tvStep = findViewById(R.id.tv_step);
//        tvTimer = findViewById(R.id.tv_timer);
//        btnStart = findViewById(R.id.btn_start);
//        btnStop = findViewById(R.id.btn_stop);
//        btnReset = findViewById(R.id.btn_restart);
//        btnStop.setVisibility(View.INVISIBLE);
//        btnReset.setVisibility(View.INVISIBLE);



//        circleProgress
            runprogress = findViewById(R.id.run_progress);
        runprogress.setWidth(600);
        runprogress.setRoundEdgeProgress(true);
        runprogress.setLinearGradientProgress(false);

        runprogress.setStartPositionInDegrees(90);
        runprogress.setWidthProgressBarLine(40);


        runprogress.setBackgroundColor(Color.LTGRAY);
        runprogress.setRoundEdgeProgress(true);

        TranslateAnimation translation_run;
        translation_run = new TranslateAnimation(0f, 0F, 0f, 220);
        translation_run.setStartOffset(100);
        translation_run.setDuration(2000);
        translation_run.setFillAfter(true);
        translation_run.setInterpolator(new BounceInterpolator());
        runprogress.startAnimation(translation_run);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        StepCalculate.mode = 1; // run

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = LoginActivity.USER_ID;
//        if (userId == null)
//            mAuth.getCurrentUser().getUid();

        createNavBar();

//        createSpinner();

        getRef().addValueEventListener(new ValueEventListener() {
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
                stepBD = stepCalculate.getTotalsteps();
                Log.i(TAG, "step in db = " + stepBD);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Start();
//                Toast.makeText(RunMode.this, "Start!",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Stop();
//                Toast.makeText(RunMode.this, "Stop!",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Restart();
//                Toast.makeText(RunMode.this, "Restart!",Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void listAdd(long time) {
        listString.add(getStringTime(time));
        listTime.add(time);
    }

    private void createSpinner() {
//        spinnerTime = findViewById(R.id.spinner_time);

        listString = new ArrayList<>();
        listTime = new ArrayList<>();

        listAdd(60000L); // for test fast!!!
        listAdd(300000L);
        listAdd(600000L);
        listAdd(900000L);
        listAdd(1200000L);
        listAdd(1800000L);

//        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listString);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerTime.setAdapter(adapter);
//
//        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                timeStart = listTime.get(position);
//                Restart();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        mDatabase.child("Users").child(userId).child("mode").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int index = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                spinnerTime.setSelection(index + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Start() {
        isRunning = true;
        spinnerTime.setEnabled(false);
        btnStart.setVisibility(View.INVISIBLE);
        btnStop.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);

        timer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                tvTimer.setText(getStringTime(timeLeft));
            }

            @Override
            public void onFinish() {
                getRef().setValue(new StepCalculate(step + stepBD));
                step = 0;
                Stop();
                btnStart.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    private void Stop() {
        btnStart.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.INVISIBLE);
        btnReset.setVisibility(View.VISIBLE);
        isRunning = false;
        spinnerTime.setEnabled(true);
        timer.cancel();

        stepOnPause = step;
        stepAtReset = stepInSensor;
    }

    private void Restart() {
        btnStart.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);
        timeLeft = timeStart;
        tvTimer.setText(getStringTime(timeLeft));
        step = 0;
        stepOnPause = 0;
        tvStep.setText(String.valueOf(step));
    }

    private String getStringTime (long time) {
        int minutes = (int) (time / 1000 / 60);
        int seconds = (int) (time / 1000 % 60);
        return String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
