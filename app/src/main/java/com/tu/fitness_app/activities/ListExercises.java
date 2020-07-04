package com.tu.fitness_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    private DrawerLayout drawerLayout;

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exercises);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initData();

        recyclerView = findViewById(R.id.listEx);
        adapter = new RecycleViewAdapter(exerciseList,getBaseContext());
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("List Excercise");
        setSupportActionBar(toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
                        intent = new Intent(ListExercises.this, Calendar.class);
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
                    default:
                        break;
                    case R.id.item7:
                        intent = new Intent(ListExercises.this, OverviewActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item8:
                        intent = new Intent(ListExercises.this, HistoryActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item9:
                        intent = new Intent(ListExercises.this, BarcodeScanner.class);
                        startActivity(intent);
                        break;
                    case R.id.item11:
                        intent = new Intent(ListExercises.this, RunMode.class);
                        startActivity(intent);
                        break;
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });

    }

    private void initData() {
        exerciseList.add(new Exercise(R.drawable.flow,"Flow Yoga","Chân trước khụy gối,chân sau duỗi thẳng,tay dơ lên nghiêng sang trái và ngược lại"));
        exerciseList.add(new Exercise(R.drawable.squatgif,"Split Squat","Chân dang rộng bằng vai, khụy xuống và thẳng lưng"));
        exerciseList.add(new Exercise(R.drawable.plankgif,"Plank","Nằm chống đẩy, đi chuyển mỗi tay vuông góc với mặt đất"));
        exerciseList.add(new Exercise(R.drawable.mountaingif,"Mountain Climber","Tay chống thẳng, chân co duỗi san kẻ"));
        exerciseList.add(new Exercise(R.drawable.jumpgif,"Jump Ropes","Chân đan chéo dang ra san kẻ"));
        exerciseList.add(new Exercise(R.drawable.runhighgif,"Run High Knees","Chạy bộ nâng cao đùi"));
        exerciseList.add(new Exercise(R.drawable.lunggif,"Lung Shoulder Press  ","Chân khụy gối tay nâng tạ lên và ngược lại"));
        exerciseList.add(new Exercise(R.drawable.pullgif,"Leg Pull-In","Tay chống sau lưng chân đưa lên cao và ngược lại"));
        exerciseList.add(new Exercise(R.drawable.vgif,"V-Up","Nằm thẳng lưng, tay và chân đụng nhau"));
        exerciseList.add(new Exercise(R.drawable.glutegif,"Glute Kickback","Tay chống chân dơ cao"));
        exerciseList.add(new Exercise(R.drawable.pushgif,"Push Up","Tay chống đẩy, chân duỗi thẳng"));
        exerciseList.add(new Exercise(R.drawable.jumplungegif,"Jump Lunge","Chân vuông góc xen kẻ và nhảy"));
        exerciseList.add(new Exercise(R.drawable.backgif,"Back Extension","Nằm xấp, lưng thẳng đầu ngẩng lên"));
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
