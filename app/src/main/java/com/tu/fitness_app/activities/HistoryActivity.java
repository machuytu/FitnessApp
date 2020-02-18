package com.tu.fitness_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
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
    private DatabaseReference ref_step;
    private DatabaseReference ref_calories;
    private DatabaseReference ref_basicInfo;

    TextView textView;
    private ArrayList<History> historyArrayList;
    ArrayAdapter<History> adapter;
    ListView historyListView;
    Date today = new Date();

    static int sumOfCalories;
    static int sumOfMoveCal;
    static int sumOfEatCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        textView = findViewById(R.id.selectedDate);

        historyArrayList =new ArrayList<>();
        // Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref_history = database.getReference("history");
        ref_calories = database.getReference("calories");
        ref_basicInfo = database.getReference("basicInfo");

        // View
        historyListView = findViewById(R.id.historyListView);
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();

        // Read from database
        readFromDatabase(date);
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
                GetStartModel getStartModel = dataSnapshot.getValue(GetStartModel.class);
                if (getStartModel != null) {
                    int currentWeight = getStartModel.getCurrentWeight();
                    int targetWeight = getStartModel.getTargetWeight();

                    if(currentWeight>targetWeight && sumOfCalories >=0){
                        //setting current date
                        calendar.set(2018, 6, 26);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //setting current date
        calendar.set(2018, 6, 20);

        calendarView.setDate(calendar);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                java.util.Calendar clickedDayCalendar = eventDay.getCalendar();
                String selectedDate = String.valueOf(clickedDayCalendar.get(java.util.Calendar.YEAR))
                        +'-'+   String.valueOf(clickedDayCalendar.get(java.util.Calendar.MONTH)+1)
                        +'-'+   String.valueOf(clickedDayCalendar.get(Calendar.DATE));
                textView.setText(selectedDate);
                alertDialog.dismiss();

                // Read from the database
                readFromTheDatabase(selectedDate);
            }
        });



    }

    public void readFromDatabase(final String date) {
        ref_history.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historyArrayList.clear();
                for (DataSnapshot historySnapShot: dataSnapshot.child(date).getChildren()) {
                    History history = historySnapShot.getValue(History.class);
                }
                adapter = new ArrayAdapter<>(HistoryActivity.this, android.R.layout.simple_list_item_1,historyArrayList);
                historyListView.setAdapter(adapter);

                // onClick
                historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long idd) {
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
                        delete_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ref_history.child(date).child(id).removeValue();
                                alertDialog.dismiss();
                            }
                        });
                        Button cancel_btn = dialogView.findViewById(R.id.dialog_cancel);
                        cancel_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                    }
                });

                // Find the amount of calories and set text and send to overview
                sumOfCalories = 0;
                sumOfMoveCal = 0;
                sumOfEatCal = 0;

                for (int i = 0; i < historyArrayList.size(); i++) {
                    sumOfCalories += Integer.valueOf(historyArrayList.get(i).getTotalCalories());
                    if(Integer.valueOf(historyArrayList.get(i).getTotalCalories())>0){
                        sumOfEatCal += Integer.valueOf(historyArrayList.get(i).getTotalCalories());
                    }else{
                        sumOfMoveCal += Integer.valueOf(historyArrayList.get(i).getTotalCalories());
                    }
                }
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


    public void chooseDate(View view) {
    }
}
