package com.tu.fitness_app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tu.fitness_app.Model.History;
import com.tu.fitness_app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseReference ref_history;
    private DatabaseReference ref_calories;
    private DatabaseReference ref_basicInfo;

    TextView textView;
    private ArrayList<History> historyArrayList;
    private ArrayList<String> historyArrayList1;
    ArrayAdapter<History> adapter;
    ArrayAdapter<String> adapter1;
    ListView historyListView;
    Date today = new Date();


    static float sumOfCalories;
    static float sumOfFat;
    static float sumOfCarbs;
    static float sumOfProtein;

    private Toolbar mToolbar;
    private ActionBar mActionBar;

    String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        textView = findViewById(R.id.selectedDate);

        sumOfCalories = LoginActivity.calRef;
        sumOfFat = LoginActivity.user_fat;
        sumOfCarbs = LoginActivity.user_carbs;
        sumOfProtein = LoginActivity.user_protein;

        historyArrayList =new ArrayList<>();
        historyArrayList1 =new ArrayList<>();
        // Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref_history = database.getReference("history");
        ref_calories = database.getReference("Calories");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // View
        historyListView = findViewById(R.id.historyListView);
        final String date_today = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();

        // Read from database
        readFromDatabase(date_today);

        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.recycle_toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        
    }

    //      https://github.com/Applandeo/Material-Calendar-View
    public void chooseDate(View view) throws OutOfDateRangeException {

        // calender library
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_history_calendar,null);
        builder.setView(dialogView);

        //adding images
        List<EventDay> events = new ArrayList<>();
        final java.util.Calendar calendar = java.util.Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.img_success));

        final CalendarView calendarView = dialogView.findViewById(R.id.calendarView);
        calendarView.setEvents(events);


//        calendar icon



        //setting current date

        calendarView.setDate(calendar);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        calendarView.setOnDayClickListener(eventDay -> {

            Calendar clickedDayCalendar = eventDay.getCalendar();
            String selectedDate = String.valueOf(clickedDayCalendar.get(Calendar.YEAR))
                    +'-'+ (clickedDayCalendar.get(Calendar.MONTH) + 1)
                    +'-'+ clickedDayCalendar.get(Calendar.DATE);
            textView.setText(selectedDate);

            alertDialog.dismiss();

            // Read from the database
            readFromDatabase(selectedDate);
        });

    }

    private DatabaseReference getCaloriesRef(String ref) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
        return mDatabase.child("Calories").child(userId).child(ref);
    }

    public void readFromDatabase(String date) {

        final String date_today = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();

        ref_history.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historyArrayList.clear();
                historyArrayList1.clear();

                for (DataSnapshot historySnapShot: dataSnapshot.child(UserId).child(date).getChildren()) {
                    History history = historySnapShot.getValue(History.class);
                    historyArrayList1.add(history.getItem());
                    historyArrayList.add(history);
                }
                adapter1 = new ArrayAdapter<>(HistoryActivity.this, android.R.layout.simple_list_item_1,historyArrayList1);
//                historyListView.setAdapter(adapter);
                historyListView.setAdapter(adapter1);

                // onClick
                if (date.equals(date_today)) {
                    historyListView.setOnItemClickListener((parent, view, position, idd) -> {
                        // Dialog -> Delete
                        History history = historyArrayList.get(position);
                        final String id = history.getId();

                        // Build dialog with custom layout
                        // Inflate custom view -> Set it on builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_history_delete, null);
                        builder.setView(dialogView);

                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        Button delete_btn = dialogView.findViewById(R.id.dialog_delete);
                        delete_btn.setOnClickListener(v -> {
//
                            sumOfCalories = sumOfCalories - Float.parseFloat(history.getTotalCalories());
                            sumOfFat = sumOfFat - history.getTotalFat();
                            sumOfCarbs = sumOfCarbs - history.getTotalCarbs();
                            sumOfProtein = sumOfProtein - history.getTotalProtein();

                            if (sumOfCalories < 0.5) {
                                sumOfCalories = 0;
                            }
                            if (sumOfFat < 0.5) {
                                sumOfFat = 0;
                            }
                            if (sumOfCarbs < 0.5) {
                                sumOfCarbs = 0;
                            }
                            if (sumOfProtein < 0.5) {
                                sumOfProtein = 0;
                            }

                            getCaloriesRef(date).child("totalcalories").setValue(sumOfCalories);
                            getCaloriesRef(date).child("totalfat").setValue(sumOfFat);
                            getCaloriesRef(date).child("totalcarbs").setValue(sumOfCarbs);
                            getCaloriesRef(date).child("totalprotein").setValue(sumOfProtein);

                            LoginActivity.calRef =  sumOfCalories;
                            LoginActivity.user_protein =  sumOfProtein;
                            LoginActivity.user_carbs =  sumOfCarbs;
                            LoginActivity.user_fat =  sumOfFat;

                            ref_history.child(UserId).child(date).child(id).removeValue();
                            alertDialog.dismiss();
                        });
                        Button cancel_btn = dialogView.findViewById(R.id.dialog_cancel);
                        cancel_btn.setOnClickListener(v -> alertDialog.dismiss());
                    });
                }

                textView.setText(date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                if(databaseError != null) {
                    Toast.makeText(HistoryActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
