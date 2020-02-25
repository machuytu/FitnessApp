package com.tu.fitness_app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.tu.fitness_app.Database.Fitness;
import com.tu.fitness_app.R;
import com.tu.fitness_app.Utils.Common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class Daily_Training extends AppCompatActivity {

    Button btnStart, btnBack;
    ImageView ex_image;
    TextView txtGetReady, txtCountdown, txtTimer, ex_name;
    ProgressBar progressBar;
    LinearLayout layoutGetReady;

    private DrawerLayout drawerLayout;

    int ex_id = 0, limit_time=0;

    Fitness fitness;

    List<com.tu.fitness_app.Model.Exercise> list = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily__training);

        initData();

        btnStart = findViewById(R.id.btnStart);
        btnBack = findViewById(R.id.btnBack);

        ex_image = findViewById((R.id.detail_image));
        ex_name = findViewById(R.id.title);

        txtCountdown = findViewById(R.id.txtCountdown);
        txtGetReady = findViewById(R.id.txtGetReady);
        txtTimer = findViewById(R.id.timer);

        layoutGetReady = findViewById(R.id.layout_get_ready);

        progressBar = (MaterialProgressBar)findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
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
                case R.id.item0:
                    Intent intent = new Intent(Daily_Training.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item1:
                    intent = new Intent(Daily_Training.this, ListExercises.class);
                    startActivity(intent);
                    break;
                case R.id.item2:
                    intent = new Intent(Daily_Training.this, Daily_Training.class);
                    startActivity(intent);
                    break;
                case R.id.item3:
                    intent = new Intent(Daily_Training.this, CalendarActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item4:
                    intent = new Intent(Daily_Training.this, SettingPage.class);
                    startActivity(intent);
                    break;
                case R.id.item5:
                    intent = new Intent(Daily_Training.this, StepCountDaily.class);
                    startActivity(intent);
                    break;
                case R.id.item6:
                    AuthUI.getInstance().signOut(Daily_Training.this)
                            .addOnCompleteListener(task ->
                                    Toast.makeText(Daily_Training.this,
                                            "Signed out successfully", Toast.LENGTH_SHORT).show());
                    Intent myIntent = new Intent(Daily_Training.this, LoginActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                    startActivity(myIntent);
                    finish();
                default:
                    break;
                case R.id.item7:
                    intent = new Intent(Daily_Training.this, OverviewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item8:
                    intent = new Intent(Daily_Training.this, HistoryActivity.class);
                    startActivity(intent);
                    break;
            }
            drawerLayout.closeDrawers();
            return false;
        });

        fitness = new Fitness(this);
        if(fitness.getSettingMode() == 0) {
            limit_time = Common.TIME_LIMIT_EASY;
        } else if(fitness.getSettingMode() == 1) {
            limit_time = Common.TIME_LIMIT_MEDIUM;
        } else if(fitness.getSettingMode() == 2) {
            limit_time = Common.TIME_LIMIT_HARD;
        }

        //Set Data
        progressBar.setMax(list.size());

        setExerciseInformation(ex_id);

        btnStart.setOnClickListener(v -> {
            if(btnStart.getText().toString().toLowerCase().equals("Start")) {
                showGetReady();
                btnStart.setText("Done");
            } else if(btnStart.getText().toString().toLowerCase().equals("Done")) {
                if(fitness.getSettingMode() == 0) {
                    exercisesEasyModeCountDown.cancel();
                } else if(fitness.getSettingMode() == 1) {
                    exercisesMediumModeCountDown.cancel();
                } else if(fitness.getSettingMode() == 2) {
                    exercisesHardModeCountDown.cancel();
                }

                restCountDown.cancel();

                if(ex_id < list.size() - 1) {
                    ShowRestTime();

                    ex_id++;

                    progressBar.setProgress(ex_id);

                    txtTimer.setText("");
                } else {
                    showFinished();
                }

            } else {
                if (fitness.getSettingMode() == 0) {
                    exercisesEasyModeCountDown.cancel();
                } else if (fitness.getSettingMode() == 1) {
                    exercisesMediumModeCountDown.cancel();
                } else if (fitness.getSettingMode() == 2) {
                    exercisesHardModeCountDown.cancel();
                }

                restCountDown.cancel();

                if (ex_id < list.size()) {
                    setExerciseInformation(ex_id);
                } else {
                    showFinished();
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(Daily_Training.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("SetTextI18n")
    private void ShowRestTime() {
        ex_image.setVisibility(View.INVISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        txtTimer.setVisibility(View.INVISIBLE);
        btnStart.setText("Skip");
        layoutGetReady.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.INVISIBLE);

        restCountDown.start();

        txtGetReady.setText("REST TIME");
    }

    @SuppressLint("SetTextI18n")
    private void showFinished() {
        ex_image.setVisibility(View.INVISIBLE);
        btnStart.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);

        txtTimer.setVisibility(View.INVISIBLE);
        layoutGetReady.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        ex_name.setVisibility(View.INVISIBLE);

        txtGetReady.setText("FINISH !!!");
        txtCountdown.setText("Congratulation !! \nYou're do today exercises");
        txtCountdown.setTextSize(20);

        //save workout done to db
        fitness.saveDay(""+ Calendar.getInstance().getTimeInMillis());
    }

    @SuppressLint("SetTextI18n")
    private void showGetReady() {
        ex_image.setVisibility(View.INVISIBLE);
        btnStart.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.INVISIBLE);

        txtTimer.setVisibility(View.VISIBLE);
        layoutGetReady.setVisibility(View.VISIBLE);

        txtGetReady.setText("GET READY");

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtCountdown.setText(""+(millisUntilFinished - 1000)/1000);
            }

            @Override
            public void onFinish() {
                showExercises();
            }
        }.start();
    }

    private void showExercises() {
        if(ex_id < list.size()) //list size contains all
        {
            ex_image.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.INVISIBLE);

            layoutGetReady.setVisibility(View.INVISIBLE);

            if(fitness.getSettingMode() == 0) {
                exercisesEasyModeCountDown.start();
            } else if(fitness.getSettingMode() == 1) {
                exercisesMediumModeCountDown.start();
            } else if(fitness.getSettingMode() == 2) {
                exercisesHardModeCountDown.start();
            }

            //set data
            ex_image.setImageResource(list.get(ex_id).getImage_id());
            ex_name.setText(list.get(ex_id).getName());
        } else {
            showFinished();
        }
    }

    // Rest
    CountDownTimer restCountDown = new CountDownTimer(10000, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            txtCountdown.setText("" + millisUntilFinished/1000);
        }

        @Override
        public void onFinish() {
            if (fitness.getSettingMode() == 0) {
                exercisesEasyModeCountDown.cancel();
            } else if (fitness.getSettingMode() == 1) {
                exercisesMediumModeCountDown.cancel();
            } else if (fitness.getSettingMode() == 2) {
                exercisesHardModeCountDown.cancel();
            }

            restCountDown.cancel();

            if (ex_id < list.size()) {
                setExerciseInformation(ex_id);
            } else {
                showFinished();
            }
        }
    };

    // Countdown
    CountDownTimer exercisesEasyModeCountDown = new CountDownTimer(Common.TIME_LIMIT_EASY, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            txtTimer.setText("" + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            if(ex_id < list.size()) {
                nextToStep();
            } else {
                showFinished();
            }
        }
    };

    CountDownTimer exercisesMediumModeCountDown = new CountDownTimer(Common.TIME_LIMIT_MEDIUM, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            txtTimer.setText("" + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            if(ex_id < list.size()) {
                nextToStep();
            } else {
                showFinished();
            }
        }
    };

    CountDownTimer exercisesHardModeCountDown = new CountDownTimer(Common.TIME_LIMIT_HARD, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            txtTimer.setText("" + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            if(ex_id < list.size()) {
                nextToStep();
            } else {
                showFinished();
            }
        }
    };

    private void nextToStep() {
        if(fitness.getSettingMode() == 0) {
            exercisesEasyModeCountDown.cancel();
        } else if(fitness.getSettingMode() == 1) {
            exercisesMediumModeCountDown.cancel();
        } else if(fitness.getSettingMode() == 2) {
            exercisesHardModeCountDown.cancel();
        }

        restCountDown.cancel();

        if(ex_id < list.size() - 1) {
            ShowRestTime();

            ex_id++;

            progressBar.setProgress(ex_id);

            txtTimer.setText("");
        } else {
            showFinished();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setExerciseInformation(int ex_id) {
        ex_image.setImageResource(list.get(ex_id).getImage_id());
        ex_name.setText(list.get(ex_id).getName());

        btnStart.setText("Start");

        ex_image.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        txtTimer.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.INVISIBLE);

        layoutGetReady.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.artistic_gymnastics,"Artistic Gymnastics"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.buddhist_meditation,"Buddhist Meditation"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.exercise_stretching,"Exercise Stretching"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.fitness_centre,"Fitness Centre"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.jump_ropes_jumping_exercise,"Jump Ropes"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.olympic_weightlifting,"Olympic Weightlifting"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.fitness_centre_physical,"Fitness Centre Physical"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.physical_exercise_weight_trainin,"Weight Training"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.physical_fitness,"Physical Fitness"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.rowing_indoor,"Rowing Indoor"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.sit_up_physical_exercise_crunch,"Sit Up Physical"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.street_workout,"Street Workout"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.yoga_physical_exercise,"Yoga Physical Exercise"));
    }

}
