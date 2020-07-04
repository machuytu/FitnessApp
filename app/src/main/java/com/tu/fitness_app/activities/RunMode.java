package com.tu.fitness_app.activities;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tu.fitness_app.Model.StepCalculate;
import com.tu.fitness_app.R;

import java.util.Date;
import java.util.Locale;

public class RunMode extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "RunCountDown";

    private SensorManager mSensorManager;
    private CountDownTimer timer;

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
    private RadioGroup rdiGroup;
    private RadioButton rdiEasy;
    private RadioButton rdiMedium;
    private RadioButton rdiHard;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;

    Date today = new Date();
    final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();

    private DatabaseReference getRef() {
        return mDatabase.child("RunMode").child(userId).child(date);
    }

    private void createNavBar() {
        // Navigation Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Run mode");
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //nav_header
        View headView = navigationView.getHeaderView(0);
        TextView name = headView.findViewById(R.id.nameHeaderBar);
        TextView email = headView.findViewById(R.id.headeremail);
        // nav_header
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        Log.d("user email", userEmail);
        email.setText(userEmail);
        String nameString = LoginActivity.USER_NAME;
        name.setText(nameString);

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

        tvStep = findViewById(R.id.textStep);
        tvTimer = findViewById(R.id.textTime);

        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnReset = findViewById(R.id.btn_restart);
        btnStop.setVisibility(View.INVISIBLE);
        btnReset.setVisibility(View.INVISIBLE);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        StepCalculate.mode = 1; // run

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = LoginActivity.USER_ID;
//        if (userId == null)
//            mAuth.getCurrentUser().getUid();

        createNavBar();
        createRadio();

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

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start();
                Toast.makeText(RunMode.this, "Start!",Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stop();
                Toast.makeText(RunMode.this, "Stop!",Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Restart();
                Toast.makeText(RunMode.this, "Restart!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createRadio() {
        rdiGroup = findViewById(R.id.rdiGroup);
        rdiEasy = findViewById(R.id.rdiEasy);
        rdiMedium = findViewById(R.id.rdiMedium);
        rdiHard = findViewById(R.id.rdiHard);

        rdiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rdiEasy:
                        timeStart = 1200000;
                        break;
                    case R.id.rdiMedium:
                        timeStart = 2700000;
                        break;
                    case R.id.rdiHard:
                        timeStart = 3600000;
                        break;
                }
                timeLeft = timeStart;
                tvTimer.setText(getStringTime(timeLeft));
            }
        });

        mDatabase.child("Users").child(userId).child("mode").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int index = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                    switch (index)
                    {
                        case 0:
                            rdiEasy.setChecked(true);
                            break;
                        case 1:
                            rdiMedium.setChecked(true);
                            break;
                        case 2:
                            rdiHard.setChecked(true);
                            break;
                    }
                }
                else {
                    rdiEasy.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void Start() {
        isRunning = true;
        rdiEasy.setEnabled(false);
        rdiMedium.setEnabled(false);
        rdiHard.setEnabled(false);

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
        rdiEasy.setEnabled(true);
        rdiMedium.setEnabled(true);
        rdiHard.setEnabled(true);
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
