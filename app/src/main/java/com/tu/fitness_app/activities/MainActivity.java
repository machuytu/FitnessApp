package com.tu.fitness_app.activities;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.firebase.ui.auth.AuthUI;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.tu.fitness_app.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private BarChart barChart;
    private DatabaseReference myref;
    private ArcProgress progressBarSteps;
    private ArcProgress progressBarBurns;
    private ArcProgress progressBarCalo;
    private NumberProgressBar progressBarCarbs;
    private NumberProgressBar progressBarProtein;
    private NumberProgressBar progressBarFat;
    private int pStatus = 50;
    private int pBurn = 50;
    private int pcalo = 50;
//    Double carbs;
//    Double fat;
//    Double protein;
    private Handler handler = new Handler();
    private  TextView step;
    private  TextView burn;
    private Button btnTrain;
    private TextView tvcaloTotal;
    private Boolean done =false;
    BarDataSet barDataSet;
    ArrayList<IBarDataSet> iBarDataSets = new ArrayList<>();
    BarData barData;
    Button btnMonthYear;
    TextView txtMonthYear;
    String stringOfDate;
    ArrayList listkey;
    ArrayList listvalue;
    int dem;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private AppBarConfiguration mAppBarConfiguration;
    ImageView btnTraining;

    public static float mSeriesMax = 0f;
    // Sensor data
    private TextView textView;
    private SensorManager msensorManager;
    private SensorManager sensorManager;
    private ImageView imgDone;
    private TextView tvDone;
    private ImageView imgNotDone;
    private TextView tvNotDone;
    private TextView tvWeightNum;
    private TextView tvHeightNum;
    private TextView tvSex;
    private TextView tvAge;
    private TextView tvStep;
    private TextView tvRun;
    private TextView tvQuangDuong;
    private TextView tvBurnCalo;
    private Spinner spinner;
    private String names[] = {"Run", "Calories"};
    ArrayAdapter <String> adapter;
    static String record = "Calories";
//Run
    float dailytotalsteps;
    float runmodetotalsteps;
    static float dailytotalcalories;
    static float runmodetotalcalories;
    static float totalCaloriessum;
    static float dailytotaldistances;
    static float runmodetotaldistances;
    static float totaldistancessum;
//Calories
    static float mytotalcalories;
    static float mytotalcaloriessum;
    static float mytotalcarbs;
    float mytotalfat;
    float mytotalprotein;
    XAxis xAxis;
    MaterialCalendarView materialCalendarView;
    Date today = new Date();
    String todaystring = today.getYear() + 1900 + "-" + (1 + today.getMonth());
    private ImageView imgRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //default
        mytotalcalories = 0f;
        dailytotalcalories = 0f;
        runmodetotalcalories = 0f;
        dailytotaldistances = 0f;
        runmodetotaldistances = 0f;
        //getDay format
        Date date = java.util.Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(date);


        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");

        //Activity
        tvStep = findViewById(R.id.tvStep);
        tvRun = findViewById(R.id.tvRun);
        tvQuangDuong = findViewById(R.id.tvQuangDuong);
        tvBurnCalo = findViewById(R.id.tvBurnCalo);
        //Infor
        tvWeightNum = findViewById(R.id.tvWeightNum);
        tvHeightNum = findViewById(R.id.tvHeightNum);
        tvSex = findViewById(R.id.tvSex);
        tvAge = findViewById(R.id.tvAge);
        //Trainning
        btnTrain = findViewById(R.id.btnTrainning);
        imgDone = findViewById(R.id.imgDone);
        tvDone = findViewById(R.id.tvDone);
        imgNotDone = findViewById(R.id.imgNotDone);
        tvNotDone = findViewById(R.id.tvNotDone);
        imgRight = findViewById(R.id.imageView4);
        //Line chart
        spinner = (Spinner)findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);

        listkey = new ArrayList<String>();
        listvalue = new ArrayList<String>();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtMonthYear = findViewById(R.id.txtMonthYear);
        txtMonthYear.setText(todaystring);
        SimpleDateFormat sdfchart = new SimpleDateFormat("yyyy-MM");
        stringOfDate = sdfchart.format(new Date());
        btnMonthYear = findViewById(R.id.btnMonthYear);
        barChart = findViewById(R.id.chart);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.getDescription().setEnabled(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextSize(15f);
//        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisLineWidth(1.5f);
//        leftAxis.setAxisMaximum(3000);
//        barData.setValueTextSize((float) 0.5);


        xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);

        xAxis.setTextSize(15f);
//        xAxis.setAxisLineColor(Color.RED);
//        xAxis.setGranularity(1f);
        xAxis.setAxisLineWidth(1.5f);
//        xAxis.setAvoidFirstLastClipping(true);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    int index = (int) value;
                    String s1 = (String) listkey.get(index);
                    StringTokenizer s2 = new StringTokenizer(s1, "-");
                    String s3 = s2.nextToken();
                    String s4 = s2.nextToken();
                    String s5 = s2.nextToken();
                    Log.d("s5",s5);
                    return s5;
                } catch (Exception e) {
                    return "";
                }
            }
        });


        Log.d("stringOfDate",stringOfDate);
        retrieveDataCalo(stringOfDate);
        btnMonthYear.setOnClickListener(this::onClick);
        //set adapter to spinner
        spinner.setAdapter(adapter);
        //set spinner method
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        record = "Run";
                        barChart.invalidate();
                        barChart.clear();
                        retrieveDataRun(todaystring);
                        txtMonthYear.setText(todaystring);

                        break;
                    case 0:
                        record = "Calories";
                        barChart.invalidate();
                        barChart.clear();
                        retrieveDataCalo(todaystring);
                        txtMonthYear.setText(todaystring);

                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Calo progress
        getDataCalories();
        int a = (int) mytotalcalories;
        progressBarCalo = (ArcProgress) findViewById(R.id.progressCalo);
        progressBarCalo.setProgress(Integer.parseInt(String.valueOf(a)));
        progressBarCalo.setBottomText("Calo");
        ObjectAnimator animation3 = ObjectAnimator.ofInt(progressBarCalo, "progress", 100,Integer.parseInt(String.valueOf(a))); // see this max value coming back here, we animate towards that value
        animation3.setDuration(5000); // in milliseconds
        animation3.setInterpolator(new DecelerateInterpolator());
        animation3.start();
        progressBarCalo.clearAnimation();

        //Protein progress
        progressBarProtein = (NumberProgressBar) findViewById(R.id.progressProtein);
        progressBarProtein.setProgress(pcalo);
        //Carb progress
        progressBarCarbs = (NumberProgressBar) findViewById(R.id.progressCarbs);
        progressBarCarbs.setProgress(pcalo);
        //Fat progress
        progressBarFat = (NumberProgressBar) findViewById(R.id.progressFat);
        progressBarFat.setProgress(pcalo);
        progressBarCalo = findViewById(R.id.progressCalo);
        tvcaloTotal = findViewById(R.id.tvCaloTotal);
//        progressBarCalo.setProgress(pCalo);
        progressBarCarbs = findViewById(R.id.progressCarbs);
        progressBarCarbs.getProgress();

        // Navigation Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headView = navigationView.getHeaderView(0);
        TextView name = headView.findViewById(R.id.nameHeaderBar);
        TextView email = headView.findViewById(R.id.headeremail);

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

// Infor
        FirebaseUser user = mAuth.getCurrentUser();
//        String
        String userId = user.getUid();
        myref = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String myage = dataSnapshot.child("age").getValue().toString();
                tvAge.setText(myage);
                String myweight = dataSnapshot.child("weight").getValue().toString();
                tvWeightNum.setText(myweight);
                String myheight = dataSnapshot.child("height").getValue().toString();
                tvHeightNum.setText(myheight);
                String mygender = dataSnapshot.child("gender").getValue().toString();
                tvSex.setText(mygender);
                String nameString = LoginActivity.USER_NAME;
                name.setText(nameString);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvAge.setText("Error found");
                tvHeightNum.setText("Error found");
                tvWeightNum.setText("Error found");
                tvSex.setText("Error found");
            }
        });
        //Run
        getDataStep();
        tvBurnCalo.setText(String.valueOf(totalCaloriessum));
        tvQuangDuong.setText(String.valueOf(totaldistancessum));

        //Set enable and disable training button
        List<String> workoutDay = LoginActivity.day;
        Log.d("workouDay", String.valueOf(workoutDay));
        List listday = new ArrayList();
        DateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        for (String value:workoutDay) {
            simple.format(Long.parseLong(value));
            listday.add(simple.format(Long.parseLong(value)));
            if(simple.format(Long.parseLong(value)).equals(strDate)){
                done=true;
            }
        }

        Log.d("listday", String.valueOf(listday));
        if (done == true)
        {
            imgDone.setVisibility(View.VISIBLE);
            tvDone.setVisibility(View.VISIBLE);
            btnTrain.setVisibility(View.INVISIBLE);
            imgRight.setVisibility(View.INVISIBLE);

            //btnTrain.setEnabled(true);
        }else{
            tvNotDone.setVisibility(View.VISIBLE);
            imgNotDone.setVisibility(View.VISIBLE);
            btnTrain.setVisibility(View.VISIBLE);
        }
        if(tvDone.getText()=="Done")
        {
            btnTrain.setEnabled(false);
        }else{
            btnTrain.setEnabled(true);
        }
        Log.d("user name:", LoginActivity.USER_NAME);


        getUsersRef("stepgoal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSeriesMax = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                    case R.id.item0:
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item1:
                        intent = new Intent(MainActivity.this, ListExercises.class);
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
                    case R.id.item9:
                        intent = new Intent(MainActivity.this, BarcodeScanner.class);
                        startActivity(intent);
                        break;
                    case R.id.item11:
                        intent = new Intent(MainActivity.this, RunMode.class);
                        startActivity(intent);
                        break;
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });

    }

    private void getDataCalories() {
        getCalories().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mytotalcalories = Float.parseFloat(String.valueOf(dataSnapshot.child("totalcalories").getValue()));
                    progressBarCalo.setProgress((int) mytotalcalories);
                    ObjectAnimator animation3 = ObjectAnimator.ofInt(progressBarCalo, "progress", 100,(int) mytotalcalories); // see this max value coming back here, we animate towards that value
                    animation3.setDuration(5000); // in milliseconds
                    animation3.setInterpolator(new DecelerateInterpolator());
                    animation3.start();
                    progressBarCalo.clearAnimation();
                    //Carb progress
//                    mytotalcarbs = Float.parseFloat(String.valueOf(dataSnapshot.child("totalcarbs").getValue()));
//                    progressBarCarbs = (NumberProgressBar) findViewById(R.id.progressCarbs);
//                    progressBarCarbs.setProgress((int) mytotalcarbs);
                    //Protein
                    mytotalprotein = Float.parseFloat(String.valueOf(dataSnapshot.child("totalprotein").getValue()));
                    progressBarProtein = (NumberProgressBar) findViewById(R.id.progressProtein);
                    progressBarProtein.setProgress((int) mytotalprotein);
                    //fat
                    mytotalfat = Float.parseFloat(String.valueOf(dataSnapshot.child("totalfat").getValue()));
                    progressBarFat = (NumberProgressBar) findViewById(R.id.progressFat);
                    progressBarFat.setProgress((int) mytotalfat);
                    Log.d("fat", String.valueOf(mytotalfat));
                }
                else {
                    mytotalcalories = 0f;
                    mytotalcarbs = 0f;
                    mytotalprotein = 0f;
                    mytotalfat = 0f;

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDataStep()  {
        getDailyWork().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //
                    dailytotalsteps = Float.parseFloat(String.valueOf(dataSnapshot.child("totalsteps").getValue()));
                    tvStep.setText(String.valueOf(dailytotalsteps));

                    dailytotalcalories = Float.parseFloat(String.valueOf(dataSnapshot.child("totalcalories").getValue()));
                    totalCaloriessum = runmodetotalcalories + dailytotalcalories;
                    tvBurnCalo.setText(String.valueOf(totalCaloriessum));

                    dailytotaldistances = Float.parseFloat(String.valueOf(dataSnapshot.child("totaldistances").getValue()));
                    totaldistancessum = dailytotaldistances + dailytotaldistances;
                    tvQuangDuong.setText(String.valueOf(totaldistancessum));
                    Log.d("test",String.valueOf(totaldistancessum));
                }
                else {
                    dailytotalsteps = 0f;
                    tvStep.setText(String.valueOf(dailytotalsteps));

                    dailytotalcalories = 0f;
                    totalCaloriessum += dailytotalcalories;

                    dailytotaldistances = 0f;
                    totaldistancessum += dailytotaldistances;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getRunMode().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    runmodetotalsteps = Float.parseFloat(String.valueOf(dataSnapshot.child("totalsteps").getValue()));
                    tvRun.setText(String.valueOf(runmodetotalsteps));

                    runmodetotalcalories = Float.parseFloat(String.valueOf(dataSnapshot.child("totalcalories").getValue()));
                    totalCaloriessum = runmodetotalcalories + dailytotalcalories;
                    tvBurnCalo.setText(String.valueOf(totalCaloriessum));

                    runmodetotaldistances = Float.parseFloat(String.valueOf(dataSnapshot.child("totaldistances").getValue()));
                    totaldistancessum = runmodetotaldistances + dailytotaldistances;
                    tvQuangDuong.setText(String.valueOf(totaldistancessum));
                }
                else {
                    runmodetotalsteps = 0f;
                    tvRun.setText(String.valueOf(runmodetotalsteps));

                    runmodetotalcalories = 0f;
                    totalCaloriessum += runmodetotalcalories;

                    runmodetotaldistances = 0f;
                    totaldistancessum += runmodetotaldistances;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private DatabaseReference getUsersRef(String ref) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        return mDatabase.child("Users").child(userId).child(ref);
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

    //Line Chart
    public void onClick(View view) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        int mYear = c.get(java.util.Calendar.YEAR);
        int mMonth = c.get(java.util.Calendar.MONTH);
        int mDay = c.get(java.util.Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        txtMonthYear.setText((monthOfYear + 1) + "-" + year);
                        stringOfDate = year + "-" + (monthOfYear + 1);
                            retrieveDataRun(stringOfDate);
                            retrieveDataCalo(stringOfDate);
                    }
                }, mYear, mMonth, mDay);
        ((ViewGroup) datePickerDialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        datePickerDialog.show();
    }
    private void retrieveDataCalo(String stringOfDate) {
        listkey = new ArrayList<String>();
        listvalue = new ArrayList<String>();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        myref = FirebaseDatabase.getInstance().getReference("Calories").child(userId);

        Query query = myref.orderByKey().startAt(stringOfDate).endAt(stringOfDate + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<BarEntry> dataVals = new ArrayList<BarEntry>();
                ArrayList<BarEntry> defaults = new ArrayList<BarEntry>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                dem = 0 ;
                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();

                    float y =  Float.parseFloat(String.valueOf(next.child("totalcalories").getValue()));
//                    String temp = next.getKey();
//                    StringTokenizer separated = new StringTokenizer(temp, "-");
//
//                    String year = separated.nextToken();
//                    String month = separated.nextToken();
//                    String day = separated.nextToken();
//                    Log.d("temp1", separated.nextToken());
                    listkey.add(next.getKey());

                    listvalue.add(next.child("totalcalories").getValue());
                    dataVals.add(new BarEntry(dem,y));
                    dem++;
                }
                Log.d("dataVals", String.valueOf(dataVals));
                if (dataVals.isEmpty()) {
                    barChart.clear();
                    barChart.invalidate();
                    showChartDefaults(defaults);
                }
                else {
                    barChart.invalidate();
                    barChart.clear();
                    showChart(dataVals);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err", "err: " + databaseError.toString());
            }
        });
        Log.d("listvalue", String.valueOf(listvalue));
    }
    private void retrieveDataRun(String stringOfDate) {
        listkey = new ArrayList<String>();
        listvalue = new ArrayList<String>();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        myref = FirebaseDatabase.getInstance().getReference("RunMode").child(userId);

        Query query = myref.orderByKey().startAt(stringOfDate).endAt(stringOfDate + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<BarEntry> dataVals = new ArrayList<BarEntry>();
                ArrayList<BarEntry> defaults = new ArrayList<BarEntry>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                dem = 0;
                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    float y =  Float.parseFloat(String.valueOf(next.child("totalsteps").getValue()));
                    listkey.add(next.getKey());
                    listvalue.add(next.child("totalsteps").getValue());
                    dataVals.add(new BarEntry(dem,y));
                    dem++;

                }
                if (dataVals.isEmpty()) {
                    barChart.clear();
                    barChart.invalidate();
                    showChartDefaults(defaults);
                }
                else {
                    barChart.invalidate();
                    barChart.clear();
                    showChart(dataVals);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err", "err: " + databaseError.toString());
            }
        });
    }
    private void showChart(ArrayList<BarEntry> dataVals) {
        barDataSet.clear();
        barDataSet = new BarDataSet(dataVals, null);
//        barDataSet.setValues(dataVals);
        barDataSet.setColor(Color.RED);
        barDataSet.setValueTextSize(8.5f);
        
        iBarDataSets.clear();
        iBarDataSets = new ArrayList<>();
        iBarDataSets.add(barDataSet);
        barData = new BarData(iBarDataSets);
        xAxis.setLabelCount(listkey.size());
        barChart.clear();
        barChart.setData(barData);
        barChart.invalidate();
    }
    private void showChartDefaults(ArrayList<BarEntry> defaults){
        defaults.add(new BarEntry(1,0));
        if (barDataSet != null) {
            barDataSet.clear();
        }

        barDataSet = new BarDataSet( defaults, null);
        barDataSet.setColor(Color.RED);
        barDataSet.setValueTextSize(8.5f);


        iBarDataSets.clear();
        iBarDataSets = new ArrayList<>();
        iBarDataSets.add(barDataSet);
        barData = new BarData(iBarDataSets);
        barChart.clear();
        barChart.setData(barData);
        barChart.invalidate();
    }
    //Run
    private DatabaseReference getDailyWork() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        Date today = new Date();
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
        Log.d("test", date);
        return mDatabase.child("DailyWalk").child(userId).child(date);
    }

    private DatabaseReference getRunMode() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        Date today = new Date();
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
        return mDatabase.child("RunMode").child(userId).child(date);
    }
    private DatabaseReference getCalories() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        Date today = new Date();
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
        return mDatabase.child("Calories").child(userId).child(date);
    }
}