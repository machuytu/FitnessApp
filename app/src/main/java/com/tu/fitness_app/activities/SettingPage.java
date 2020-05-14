package com.tu.fitness_app.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tu.fitness_app.Database.Fitness;
import com.tu.fitness_app.Model.Setting;
import com.tu.fitness_app.Model.User;
import com.tu.fitness_app.R;

import java.util.Date;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class SettingPage extends AppCompatActivity {

    Button btnSave;
    RadioButton rdiEasy, rdiMedium, rdiHard;
    RadioGroup rdiGroup;
    Fitness fitness;
    Setting setting;
    SwitchCompat switchAlarm;
    TimePicker timePicker;
    TextView tvAlarm;

    EditText editText1;
    EditText editText2;

    ProgressBar progressBar;
    private DrawerLayout drawerLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private int CaloriesHolder;
    private int StepHolder;

    @SuppressLint({"WrongViewCast", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnSave = findViewById(R.id.btnSave);

        rdiEasy = findViewById(R.id.rdiEasy);
        rdiMedium = findViewById(R.id.rdiMedium);
        rdiHard = findViewById(R.id.rdiHard);

        rdiGroup = findViewById(R.id.rdiGroup);

        switchAlarm = findViewById(R.id.switchAlarm);

        timePicker = findViewById(R.id.timePicker);

        editText1 = findViewById(R.id.et4);
        editText2 = findViewById(R.id.et5);

        tvAlarm = findViewById(R.id.tvAlarm);
        switchAlarm = findViewById(R.id.switchAlarm);

//        fitness = new Fitness(this);
//        int mode = fitness.getSettingMode();
//        setRadioButton(mode);

        int mode1 = (int) LoginActivity.mode;
        setRadioButton(mode1);

        progressBar = (MaterialProgressBar)findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                    }
                };
        actionBarDrawerToggle.syncState();


        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId())
            {
                case R.id.item1:
                    Intent intent = new Intent(SettingPage.this, ListExercises.class);
                    startActivity(intent);
                    break;
                case R.id.item2:
                    intent = new Intent(SettingPage.this, Daily_Training.class);
                    startActivity(intent);
                    break;
                case R.id.item3:
                    intent = new Intent(SettingPage.this, Calendar.class);
                    startActivity(intent);
                    break;
                case R.id.item4:
                    intent = new Intent(SettingPage.this, SettingPage.class);
                    startActivity(intent);
                    break;
                case R.id.item5:
                    intent = new Intent(SettingPage.this, StepCountDaily.class);
                    startActivity(intent);
                    break;
                case R.id.item6:
                    AuthUI.getInstance().signOut(SettingPage.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SettingPage.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent myIntent = new Intent(SettingPage.this, LoginActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                    startActivity(myIntent);
                    finish();
                default:
                    break;
                case R.id.item7:
                    intent = new Intent(SettingPage.this, OverviewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item8:
                    intent = new Intent(SettingPage.this, HistoryActivity.class);
                    startActivity(intent);
                    break;
            }
            drawerLayout.closeDrawers();
            return false;
        });

        // Show data
        float seriesCalo = LoginActivity.mSeries2;
        editText1.setText(Integer.toString((int) seriesCalo));

        float seriesStep = LoginActivity.mSeries1;
        editText2.setText(Integer.toString((int) seriesStep));

        //Event
        btnSave.setOnClickListener(v -> {
            // Get data from text
            CaloriesHolder = Integer.parseInt(editText1.getText().toString().trim());
            StepHolder = Integer.parseInt(editText2.getText().toString().trim());

            User user = new User();
            user.SetCalorieGoal(CaloriesHolder);
            user.SetStepGoal(StepHolder);

            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDatabase.child("Users").child(userId).setValue(user);

            saveAlarm(switchAlarm.isChecked());
            saveWorkoutState();
            Toast.makeText(SettingPage.this, "SAVE!!!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveAlarm(boolean checked) {
        if(checked) {
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent;
            PendingIntent pendingIntent;

            intent = new Intent(SettingPage.this, AlarmNotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            java.util.Calendar calendar = java.util.Calendar.getInstance();
            Date toDate = java.util.Calendar.getInstance().getTime();
            calendar.set(toDate.getYear(), toDate.getMonth(), toDate.getDay(), timePicker.getHour(), timePicker.getMinute());

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        } else {
            //cancel alarm
            Intent intent = new Intent(SettingPage.this, AlarmNotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
        switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tvAlarm.setText("Alarm is " +
                        (switchAlarm.isChecked() ? "On" : "Off"));
            }
        });
    }

    private void saveWorkoutState() {
//        int selectID = rdiGroup.getCheckedRadioButtonId();
//        if(selectID == rdiEasy.getId()) {
//            fitness.saveSettingMode(0);
//        } else if(selectID == rdiMedium.getId()) {
//            fitness.saveSettingMode(1);
//        } else  if(selectID == rdiHard.getId()) {
//            fitness.saveSettingMode(2);
//        }

        int selectID = rdiGroup.getCheckedRadioButtonId();
        Setting setting = new Setting();
        if(selectID == rdiEasy.getId()) {
            setting.SetSetting(0);
        } else if(selectID == rdiMedium.getId()) {
            setting.SetSetting(1);
        } else  if(selectID == rdiHard.getId()) {
            setting.SetSetting(2);
        }
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("Users").child(userId).setValue(setting);
    }

    private void setRadioButton(int mode) {
        if(mode == 0) {
            rdiGroup.check(R.id.rdiEasy);
        } else if(mode == 1) {
            rdiGroup.check(R.id.rdiMedium);
        } else if(mode == 2) {
            rdiGroup.check(R.id.rdiHard);
        }
    }
}
