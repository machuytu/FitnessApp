package com.tu.fitness_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.tu.fitness_app.Custom.WorkoutDoneDecorator;
import com.tu.fitness_app.Database.Fitness;
import com.tu.fitness_app.Model.WorkoutDays;
import com.tu.fitness_app.R;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class Calendar extends AppCompatActivity {

    MaterialCalendarView materialCalendarView;
    HashSet<CalendarDay> list = new HashSet<>();

    Fitness fitness;

    ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private WorkoutDays workoutDays;
    private DatabaseReference ref_LisDay;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fitness = new Fitness(this);
        workoutDays = new WorkoutDays();

        materialCalendarView = findViewById(R.id.calendar);

//        List<String> workoutDay = fitness.getWorkoutDays();
        List<String> workoutDay = LoginActivity.day;
        Log.d("calendarday", String.valueOf(workoutDay));

        progressBar = (MaterialProgressBar)findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Calendar");
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //nav_header
        View headView = navigationView.getHeaderView(0);
        TextView name = headView.findViewById(R.id.nameHeaderBar);
        TextView email = headView.findViewById(R.id.headeremail);
        // nav_header
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        Log.d("user email", userEmail);
        email.setText(userEmail);

        String nameString = LoginActivity.USER_NAME;
        name.setText(nameString);

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
                    Intent intent = new Intent(Calendar.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item1:
                    intent = new Intent(Calendar.this, ListExercises.class);
                    startActivity(intent);
                    break;
                case R.id.item2:
                    intent = new Intent(Calendar.this, Daily_Training.class);
                    startActivity(intent);
                    break;
                case R.id.item3:
                    intent = new Intent(Calendar.this, Calendar.class);
                    startActivity(intent);
                    break;
                case R.id.item4:
                    intent = new Intent(Calendar.this, SettingPage.class);
                    startActivity(intent);
                    break;
                case R.id.item5:
                    intent = new Intent(Calendar.this, StepCountDaily.class);
                    startActivity(intent);
                    break;
                case R.id.item6:
                    AuthUI.getInstance().signOut(Calendar.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Calendar.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent myIntent = new Intent(Calendar.this, LoginActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                    startActivity(myIntent);
                    finish();
                default:
                    break;
                case R.id.item7:
                    intent = new Intent(Calendar.this, OverviewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item8:
                    intent = new Intent(Calendar.this, HistoryActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item9:
                    intent = new Intent(Calendar.this, BarcodeScanner.class);
                    startActivity(intent);
                    break;
                case R.id.item11:
                    intent = new Intent(Calendar.this, RunMode.class);
                    startActivity(intent);
                    break;
            }
            drawerLayout.closeDrawers();
            return false;
        });

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        ref_LisDay = database.getReference("WorkoutDays");

        //save workout done to db
//        fitness.saveDay(""+ Calendar.getInstance().getTimeInMillis());
//        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        ref_LisDay.child(userId).setValue(workoutDays);

        HashSet<CalendarDay> convertedList = new HashSet<>();
        for (String value:workoutDay) {
            convertedList.add(CalendarDay.from(new Date(Long.parseLong(value))));
        }
        materialCalendarView.addDecorator(new WorkoutDoneDecorator(convertedList));
    }
}
