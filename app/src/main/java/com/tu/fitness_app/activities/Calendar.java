package com.tu.fitness_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.tu.fitness_app.Database.Fitness;
import com.tu.fitness_app.R;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

class CalendarActivity extends AppCompatActivity {

    MaterialCalendarView materialCalendarView;
    HashSet<CalendarDay> list = new HashSet<>();

    Fitness fitness;

    ProgressBar progressBar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        fitness = new Fitness(this);

        materialCalendarView = findViewById(R.id.calendar);

        List<String> workoutDay = fitness.getWorkoutDays();

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
                    Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item1:
                    intent = new Intent(CalendarActivity.this, ListExercises.class);
                    startActivity(intent);
                    break;
                case R.id.item2:
                    intent = new Intent(CalendarActivity.this, Daily_Training.class);
                    startActivity(intent);
                    break;
                case R.id.item3:
                    intent = new Intent(CalendarActivity.this, CalendarActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item4:
                    intent = new Intent(CalendarActivity.this, SettingPage.class);
                    startActivity(intent);
                    break;
                case R.id.item5:
                    intent = new Intent(CalendarActivity.this, StepCountDaily.class);
                    startActivity(intent);
                    break;
                case R.id.item6:
                    AuthUI.getInstance().signOut(CalendarActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(CalendarActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent myIntent = new Intent(CalendarActivity.this, LoginActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                    startActivity(myIntent);
                    finish();
                default:
                    break;
                case R.id.item7:
                    intent = new Intent(CalendarActivity.this, OverviewActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item8:
                    intent = new Intent(CalendarActivity.this, HistoryActivity.class);
                    startActivity(intent);
                    break;
            }
            drawerLayout.closeDrawers();
            return false;
        });


        for (String value:workoutDay) {
        }
    }
}
