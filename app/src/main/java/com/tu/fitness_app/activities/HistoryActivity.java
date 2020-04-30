package com.tu.fitness_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tu.fitness_app.Model.Calories;
import com.tu.fitness_app.Model.History;
import com.tu.fitness_app.Model.User;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        textView = findViewById(R.id.selectedDate);

        historyArrayList =new ArrayList<>();
        historyArrayList1 =new ArrayList<>();
        // Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref_history = database.getReference("history");
        ref_calories = database.getReference("Calories");

        // View
        historyListView = findViewById(R.id.historyListView);
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();

        // Read from database
        readFromDatabase(date);

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


        //calendar icon
        ref_basicInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    // read data user (not done)
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

    public void readFromDatabase(final String date) {
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
                        ref_history.child(UserId).child(date).child(id).removeValue();
                        alertDialog.dismiss();
                    });
                    Button cancel_btn = dialogView.findViewById(R.id.dialog_cancel);
                    cancel_btn.setOnClickListener(v -> alertDialog.dismiss());
                });

                // Find the amount of calories and set text and send to overview
                sumOfCalories = 0f;
                sumOfFat = 0f;
                sumOfProtein = 0f;
                sumOfCarbs = 0f;
//
                for (int i = 0; i < historyArrayList.size(); i++) {
                    sumOfCalories += Float.valueOf(historyArrayList.get(i).getTotalCalories());
                    sumOfFat += historyArrayList.get(i).getTotalFat();
                    sumOfProtein += historyArrayList.get(i).getTotalProtein();
                    sumOfCarbs += historyArrayList.get(i).getTotalCarbs();
                }

                Log.d("sum ", String.valueOf(sumOfCalories + sumOfCarbs + sumOfProtein + sumOfFat));
                Calories calories = new Calories(sumOfCalories, sumOfFat, sumOfCarbs, sumOfProtein);
                ref_calories.child(UserId).child(date).setValue(calories);
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
