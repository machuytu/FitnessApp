package com.tu.fitness_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.tu.fitness_app.Adapter.RecycleViewAdapter;
import com.tu.fitness_app.Model.Exercise;
import com.tu.fitness_app.R;

import java.util.ArrayList;
import java.util.List;

public class ListExercises extends AppCompatActivity {

    List<com.tu.fitness_app.Model.Exercise> exerciseList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    com.tu.fitness_app.Adapter.RecycleViewAdapter adapter;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exercises);

        initData();

        recyclerView = findViewById(R.id.listEx);
        adapter = new RecycleViewAdapter(exerciseList,getBaseContext());
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

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

//        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
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
                        Intent intent = new Intent(ListExercises.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item1:
                        intent = new Intent(ListExercises.this, ListExercises.class);
                        startActivity(intent);
                        break;
                    case R.id.item2:
                        intent = new Intent(ListExercises.this, Daily_Training.class);
                        startActivity(intent);
                        break;
                    case R.id.item3:
                        intent = new Intent(ListExercises.this, CalendarActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item4:
                        intent = new Intent(ListExercises.this, SettingPage.class);
                        startActivity(intent);
                        break;
                    case R.id.item5:
                        intent = new Intent(ListExercises.this, StepCountDaily.class);
                        startActivity(intent);
                        break;
                    case R.id.item6:
                        AuthUI.getInstance().signOut(ListExercises.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ListExercises.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent myIntent = new Intent(ListExercises.this, LoginActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                        startActivity(myIntent);
                        finish();
                    case R.id.item7:
                        intent = new Intent(ListExercises.this, OverviewActivity.class);
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

    private void initData() {
        exerciseList.add(new Exercise(R.drawable.artistic_gymnastics,"Artistic Gymnastics"));
        exerciseList.add(new Exercise(R.drawable.buddhist_meditation,"Buddhist Meditation"));
        exerciseList.add(new Exercise(R.drawable.exercise_stretching,"Exercise Stretching"));
        exerciseList.add(new Exercise(R.drawable.fitness_centre,"Fitness Centre"));
        exerciseList.add(new Exercise(R.drawable.jump_ropes_jumping_exercise,"Jump Ropes"));
        exerciseList.add(new Exercise(R.drawable.olympic_weightlifting,"Olympic Weightlifting"));
        exerciseList.add(new Exercise(R.drawable.fitness_centre_physical,"Fitness Centre Physical"));
        exerciseList.add(new Exercise(R.drawable.physical_exercise_weight_trainin,"Weight Training"));
        exerciseList.add(new Exercise(R.drawable.physical_fitness,"Physical Fitness"));
        exerciseList.add(new Exercise(R.drawable.rowing_indoor,"Rowing Indoor"));
        exerciseList.add(new Exercise(R.drawable.sit_up_physical_exercise_crunch,"Sit Up Physical"));
        exerciseList.add(new Exercise(R.drawable.street_workout,"Street Workout"));
        exerciseList.add(new Exercise(R.drawable.yoga_physical_exercise,"Yoga Physical Exercise"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
