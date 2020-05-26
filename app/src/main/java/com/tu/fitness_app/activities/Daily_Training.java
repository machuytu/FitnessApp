package com.tu.fitness_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tu.fitness_app.Database.Fitness;
import com.tu.fitness_app.Model.Setting;
import com.tu.fitness_app.Model.WorkoutDays;
import com.tu.fitness_app.R;
import com.tu.fitness_app.Utils.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class Daily_Training extends AppCompatActivity {

    Button btnStart, btnBack;
    ImageView ex_image;
    TextView txtGetReady, txtCountdown, txtTimer, ex_name,tvIntro;
    ProgressBar progressBar;
    LinearLayout layoutGetReady;
    private DrawerLayout drawerLayout;
    int ex_id = 0, limit_time=0;
    private AppBarConfiguration mAppBarConfiguration;
    Fitness fitness;
    Setting setting;

    private DatabaseReference mDatabase;
    private DatabaseReference ref_LisDay;
    List<com.tu.fitness_app.Model.Exercise> list = new ArrayList<>();
    private WorkoutDays workoutDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily__training);

        initData();

    

        drawerLayout = findViewById(R.id.drawer_layout);
        btnStart = (Button)findViewById(R.id.btnStart);
        btnBack = (Button)findViewById(R.id.btnBack);

        ex_image = (ImageView)findViewById((R.id.detail_image));
        ex_name = (TextView)findViewById(R.id.title);
        tvIntro= (TextView)findViewById(R.id.tvIntro);
        txtCountdown = (TextView)findViewById(R.id.txtCountdown);
        txtGetReady = (TextView)findViewById(R.id.txtGetReady);
        txtTimer = (TextView)findViewById(R.id.timer);

        layoutGetReady = (LinearLayout)findViewById(R.id.layout_get_ready);

        progressBar = (MaterialProgressBar)findViewById(R.id.progressBar);

//        fitness = new Fitness(this);
//        if(fitness.getSettingMode() == 0) {
//            limit_time = Common.TIME_LIMIT_EASY;
//        } else if(fitness.getSettingMode() == 1) {
//            limit_time = Common.TIME_LIMIT_MEDIUM;
//        } else if(fitness.getSettingMode() == 2) {
//            limit_time = Common.TIME_LIMIT_HARD;
//        }

        setting = new Setting();
        if(setting.GetSetting() == 0) {
            limit_time = Common.TIME_LIMIT_EASY;
        } else if(setting.GetSetting() == 1) {
            limit_time = Common.TIME_LIMIT_MEDIUM;
        } else if(setting.GetSetting() == 2) {
            limit_time = Common.TIME_LIMIT_HARD;
        }

        //Set Data
        progressBar.setMax(list.size());

        setExerciseInformation(ex_id);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnStart.getText().toString().toLowerCase().equals("start")) {
                    showGetReady();
                    btnStart.setText("done");
                } else if(btnStart.getText().toString().toLowerCase().equals("done")) {
                    if(setting.GetSetting() == 0) {
                        exercisesEasyModeCountDown.cancel();
                    } else if(setting.GetSetting() == 1) {
                        exercisesMediumModeCountDown.cancel();
                    } else if(setting.GetSetting() == 2) {
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
                    if (setting.GetSetting() == 0) {
                        exercisesEasyModeCountDown.cancel();
                    } else if (setting.GetSetting() == 1) {
                        exercisesMediumModeCountDown.cancel();
                    } else if (setting.GetSetting() == 2) {
                        exercisesHardModeCountDown.cancel();
                    }

                    restCountDown.cancel();

                    if (ex_id < list.size()) {
                        setExerciseInformation(ex_id);
                    } else {
                        showFinished();
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Daily_Training.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref_LisDay = database.getReference("WorkoutDays");
        String day = ""+ Calendar.getInstance().getTimeInMillis();
        Log.d("day", day);
        List<String> listday = Arrays.asList(day);
        workoutDays = new WorkoutDays(listday);

        //save workout done to db
//        fitness.saveDay(""+ Calendar.getInstance().getTimeInMillis());
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref_LisDay.child(userId).setValue(workoutDays);
//        workoutDays.saveDay(""+ Calendar.getInstance().getTimeInMillis());
    }

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

            if(setting.GetSetting() == 0) {
                exercisesEasyModeCountDown.start();
            } else if(setting.GetSetting() == 1) {
                exercisesMediumModeCountDown.start();
            } else if(setting.GetSetting() == 2) {
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
        @Override
        public void onTick(long millisUntilFinished) {
            txtCountdown.setText("" + millisUntilFinished/1000);
        }

        @Override
        public void onFinish() {
            if (setting.GetSetting() == 0) {
                exercisesEasyModeCountDown.cancel();
            } else if (setting.GetSetting() == 1) {
                exercisesMediumModeCountDown.cancel();
            } else if (setting.GetSetting() == 2) {
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
        if(setting.GetSetting() == 0) {
            exercisesEasyModeCountDown.cancel();
        } else if(setting.GetSetting() == 1) {
            exercisesMediumModeCountDown.cancel();
        } else if(setting.GetSetting() == 2) {
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

    private void setExerciseInformation(int ex_id) {
        ex_image.setImageResource(list.get(ex_id).getImage_id());
        ex_name.setText(list.get(ex_id).getName());
        tvIntro.setText(list.get(ex_id).getName1());

        btnStart.setText("Start");

        ex_image.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        txtTimer.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.INVISIBLE);

        layoutGetReady.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.flow,"Flow Yoga","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.squatgif,"Split Squat","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.plankgif,"Plank","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.mountaingif,"Mountain Climber","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.jumpgif,"Jump Ropes","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.runhighgif,"Run High Knees","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.lunggif,"Lung Shoulder Press  ","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.pullgif,"Leg Pull-In","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.vgif,"V-Up","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.glutegif,"Glute Kickback","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.pushgif,"Push Up","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.jumplungegif,"Jump Lunge","Chân khụy gối vuông góc"));
        list.add(new com.tu.fitness_app.Model.Exercise(R.drawable.backgif,"Back Extension","Chân khụy gối vuông góc"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.exercise_stretching,"Exercise Stretching"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.fitness_centre,"Fitness Centre"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.jump_ropes_jumping_exercise,"Jump Ropes"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.olympic_weightlifting,"Olympic Weightlifting"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.fitness_centre_physical,"Fitness Centre Physical"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.physical_exercise_weight_trainin,"Weight Training"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.physical_fitness,"Physical Fitness"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.rowing_indoor,"Rowing Indoor"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.sit_up_physical_exercise_crunch,"Sit Up Physical"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.street_workout,"Street Workout"));
//        list.add(new com.tu.fitness_app.model.Exercise(R.drawable.yoga_physical_exercise,"Yoga Physical Exercise"));
    }

}
