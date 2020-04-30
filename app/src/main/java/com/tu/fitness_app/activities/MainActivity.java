package com.tu.fitness_app.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.firebase.ui.auth.AuthUI;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.tu.fitness_app.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private PieChart pieChart;
    private ArcProgress progressBarSteps;
    private ArcProgress progressBarBurns;
    private ArcProgress progressBarCalo;
    private NumberProgressBar progressBarCarbs;
    private NumberProgressBar progressBarProtein;
    private NumberProgressBar progressBarFat;
    private int pStatus = 50;
    private int pBurn = 50;
    private int pCalo = 50;
    private Handler handler = new Handler();
    private  TextView step;
    private  TextView burn;
    private Button btnTrain;
    private TextView tvcaloTotal;
    private Boolean done =false;

    private AppBarConfiguration mAppBarConfiguration;

    Button btnExercises, btnSetting, btnCalendar, btnStepCount, btnOverview, btnHistory, btnBarcode;
    ImageView btnTraining;

    public static float mSeriesMax = 0f;
    // Sensor data
    private TextView textView;
    private SensorManager msensorManager;
    private SensorManager sensorManager;
    private ImageView imgDone;
    private TextView tvDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTrain = findViewById(R.id.btnTrainning);
        imgDone = findViewById(R.id.imgDone);
        tvDone = findViewById(R.id.tvDone);
        //Pie chart
        pieChart = (PieChart) findViewById(R.id.piechart);
        PieDataSet pieDataSet = new PieDataSet(getData(),"Overview");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.animateXY(1000, 1000);
        pieChart.invalidate();

        //----------------------------------------------------Progress bar---------------------------------------------------------
        //progressBarSteps = (ProgressBar) findViewById(R.id.progressBar2);

        //Steps progress

//        progressBarSteps.setProgress(pStatus);
//        progressBarSteps.setBottomText("Steps");
//        ObjectAnimator animation = ObjectAnimator.ofInt(progressBarSteps, "progress", 0, 50); // see this max value coming back here, we animate towards that value
//        animation.setDuration(5000); // in milliseconds
//        animation.setInterpolator(new DecelerateInterpolator());
//        animation.start();
//        progressBarSteps.clearAnimation();

        //Burn progress

//        progressBarBurns.setProgress((pBurn));
//        progressBarBurns.setBottomText("Burn");
//        ObjectAnimator animation2 = ObjectAnimator.ofInt(progressBarBurns, "progress", 0, 50); // see this max value coming back here, we animate towards that value
//        animation2.setDuration(5000); // in milliseconds
//        animation2.setInterpolator(new DecelerateInterpolator());
//        animation2.start();
//        progressBarBurns.clearAnimation();

        //Calo progress
        progressBarCalo = (ArcProgress) findViewById(R.id.progressCalo);
        progressBarCalo.setProgress(pCalo);
        progressBarCalo.setBottomText("Calo");
        ObjectAnimator animation3 = ObjectAnimator.ofInt(progressBarCalo, "progress", 100, 50); // see this max value coming back here, we animate towards that value
        animation3.setDuration(5000); // in milliseconds
        animation3.setInterpolator(new DecelerateInterpolator());
        animation3.start();
        progressBarCalo.clearAnimation();

        //Protein progress
        progressBarProtein = (NumberProgressBar) findViewById(R.id.progressProtein);

        progressBarProtein.setProgress(pCalo);


        //Carb progress
        progressBarCarbs = (NumberProgressBar) findViewById(R.id.progressCarbs);
        progressBarCarbs.setProgress(pCalo);


        //Fat progress
        progressBarFat = (NumberProgressBar) findViewById(R.id.progressFat);
        progressBarFat.setProgress(pCalo);



//        progressBarBurns = (ArcProgress) findViewById(R.id.progressBar3);
//        burn = (TextView)findViewById(R.id.tvstepBurn);
//
//        progressBarBurns.setProgress(pBurn);
//        burn.setText(pBurn + " %");

        progressBarCalo = findViewById(R.id.progressCalo);
        tvcaloTotal = findViewById(R.id.tvCaloTotal);

//        progressBarCalo.setProgress(pCalo);

        progressBarCarbs = findViewById(R.id.progressCarbs);
        progressBarCarbs.getProgress();
        progressBarCarbs.getMax();



        //Set enable and disable training button
        if (done == true)
        {
            btnTrain.setEnabled(false);
        }else{
            btnTrain.setEnabled(true);
        }
        if(tvDone.getText()=="Done")
        {
            btnTrain.setEnabled(false);
        }else{
            btnTrain.setEnabled(true);
        }


        // Navigation Bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle =
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

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mSeriesMax = SetGoalActivity.mSeries;
        Log.d("SetGoal mseries", String.valueOf(SetGoalActivity.mSeries));
        if (mSeriesMax == 0) {
            mSeriesMax = LoginActivity.mSeries1;
        }
        final String cap1;
        final float[] m = new float[1];
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.item1:
                        Intent intent = new Intent(MainActivity.this, ListExercises.class);
                        startActivity(intent);
                        break;
                    case R.id.item2:
                        intent = new Intent(MainActivity.this, Daily_Training.class);
                        startActivity(intent);
                        break;
                    case R.id.item3:
                        intent = new Intent(MainActivity.this, Calendar.class);
                        startActivity(intent);
                        break;
                    case R.id.item4:
                        intent = new Intent(MainActivity.this, SettingPage.class);
                        startActivity(intent);
                        break;
                    case R.id.item5:
                        intent = new Intent(MainActivity.this, StepCountDaily.class);
                        startActivity(intent);
                        break;
                    case R.id.item6:
                        AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                        startActivity(myIntent);
                        finish();
                    default:
                        break;
                    case R.id.item7:
                        intent = new Intent(MainActivity.this, OverviewActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item8:
                        intent = new Intent(MainActivity.this, HistoryActivity.class);
                        startActivity(intent);
                        break;
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });

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

    private ArrayList getData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(945f, "Running"));
        entries.add(new PieEntry(1030f, "Walking"));
        entries.add(new PieEntry(1143f, "Trainning"));
        entries.add(new PieEntry(1250f, "Calo"));
        return entries;
    }



}