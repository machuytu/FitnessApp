package com.tu.fitness_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tu.fitness_app.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Chart extends AppCompatActivity implements View.OnClickListener {
    TextView txtMonthYear;
    Button btnMonthYear;
    LineChart lineChart;
    private FirebaseAuth mAuth;
    private DatabaseReference myref;
    LineDataSet lineDataSet = new LineDataSet(null, null);
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;
    String stringOfDate;
    ArrayList listkey;
    ArrayList listvalue;
    int dem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        listkey = new ArrayList<String>();
        listvalue = new ArrayList<String>();
        mAuth = FirebaseAuth.getInstance();
        txtMonthYear = findViewById(R.id.txtMonthYear);
        txtMonthYear.setText(stringOfDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        stringOfDate = sdf.format(new Date());
        btnMonthYear = findViewById(R.id.btnMonthYear);
        lineChart = findViewById(R.id.chart1);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    int index = (int) value;
                    return (String) listkey.get(index);
                } catch (Exception e) {
                    return "";
                }
            }
        });

        retrieveData(stringOfDate);
        btnMonthYear.setOnClickListener(this);

    }
    @Override
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
                        Log.d("text month", (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        ((ViewGroup) datePickerDialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        datePickerDialog.show();

    }
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            //Use the current date as the default date in the date picker

            final java.util.Calendar c = java.util.Calendar.getInstance();
            int year = c.get(java.util.Calendar.YEAR);
            int month = c.get(java.util.Calendar.MONTH);
            int day = c.get(java.util.Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_DARK,this,year, month, day){
                //DatePickerDialog dpd = new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT,this,year, month, day){
                // DatePickerDialog dpd = new DatePickerDialog(getActivity(), AlertDialog.THEME_TRADITIONAL,this,year, month, day){
                @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    int day = getContext().getResources().getIdentifier("android:id/day", null, null);
                    if(day != 0){
                        View dayPicker = findViewById(day);
                        if(dayPicker != null){
                            //Set Day view visibility Off/Gone
                            dayPicker.setVisibility(View.GONE);
                        }
                    }

                }
            };
            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            //Set the Month & Year to TextView which chosen by the user
            TextView tv = (TextView) getActivity().findViewById(R.id.txtMonthYear);
            stringOfDate = month + "-" + year;
            tv.setText(stringOfDate);
            retrieveData(stringOfDate);
        }
    }
    private void retrieveData(String stringOfDate) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        myref = FirebaseDatabase.getInstance().getReference("Calories").child(userId);

        Query query = myref.orderByKey().startAt(stringOfDate).endAt(stringOfDate + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Entry> dataVals = new ArrayList<Entry>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                dem = 1;
                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();

                    float y =  Float.parseFloat(String.valueOf(next.child("totalcalories").getValue()));
                    listkey.add(next.getKey());

                    listvalue.add(next.child("totalcalories").getValue());
                    dataVals.add(new Entry(dem,y));
                    dem++;
                }
                Log.d("list:", String.valueOf(listvalue));
                if (dataVals == null) {
                    lineChart.clear();
                    lineChart.invalidate();
                }
                else {
                    showChart(dataVals);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err", "err: " + databaseError.toString());
            }
        });
    }
    private void showChart(ArrayList<Entry> dataVals) {
        lineDataSet.setValues(dataVals);
        lineDataSet.setLabel("DataSet 1");
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);
        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
