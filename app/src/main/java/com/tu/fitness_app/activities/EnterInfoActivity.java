package com.tu.fitness_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tu.fitness_app.R;

public class EnterInfoActivity extends AppCompatActivity {
    int position;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ImageView imgHigh;
    private ImageView imgAvg;
    private ImageView imgLow;
    private TextView tvHigh;
    private TextView tvAvg;
    private TextView tvLow;
    private TextView tvWelcome;
    private int Gender;
    private float totalCarolies = 0f;
    private float BMR = 0f;
    private float zLevel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basicinfo);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Button next = (Button) findViewById(R.id.nextbutton);


        final EditText nameET = (EditText) findViewById(R.id.nameInput);
        final EditText phoneET = (EditText)findViewById(R.id.phoneInput);
        final EditText ageET = (EditText) findViewById(R.id.ageInput);
        final EditText weightET = (EditText) findViewById(R.id.weightInput);
        final EditText heightET = (EditText) findViewById(R.id.heightInput);
        imgHigh =  (ImageView) findViewById(R.id.imgHigh);
        imgAvg =  (ImageView) findViewById(R.id.imgAvg);
        imgLow=  (ImageView) findViewById(R.id.imgLow);
        tvHigh = (TextView) findViewById(R.id.tvHigh);
        tvAvg = (TextView) findViewById(R.id.tvAvg);
        tvLow = (TextView) findViewById(R.id.tvLow);
//        tvWelcome = (TextView) findViewById(R.id.tvWelcome);



        final RadioGroup myRadioGroup = (RadioGroup) findViewById(R.id.genderGroup);
        myRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            position = myRadioGroup.indexOfChild(findViewById(checkedId));
            if (position == 0) {
                Log.d("Gender is ", "Male");
                getUsersRef("gender").setValue("Male");
                Gender = 0;
            } else {
                Log.d("Gender is ", "Female");
                getUsersRef("gender").setValue("Female");
                Gender = 1;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameET.getText().toString().length() == 0) {
                    nameET.setError("Name is required!");
                    return;
                }
                Intent myIntent = new Intent(EnterInfoActivity.this, MainActivity.class);
                startActivity(myIntent);
                getUsersRef("name").setValue(nameET.getText().toString());
                getUsersRef("phone").setValue(phoneET.getText().toString());
                getUsersRef("age").setValue(ageET.getText().toString());
                getUsersRef("height").setValue(heightET.getText().toString());
                getUsersRef("weight").setValue(weightET.getText().toString());
                
                if (Gender == 0) {
                    BMR = (float) (10 * Float.parseFloat(weightET.getText().toString())
                            + 6.25 * Float.parseFloat(heightET.getText().toString())
                                - 5 * Float.parseFloat(ageET.getText().toString()) + 5);
                } else {
                    BMR = (float) (10 * Float.parseFloat(weightET.getText().toString())
                            + 6.25 * Float.parseFloat(heightET.getText().toString())
                            - 5 * Float.parseFloat(ageET.getText().toString()) - 161);
                }

                totalCarolies = BMR * zLevel;
                Log.d("totalcalries", String.valueOf(totalCarolies));
                getUsersRef("caloriegoal").setValue(totalCarolies);

                if (zLevel == 1.2) {
                    getUsersRef("stepgoal").setValue(8000);
                } else if (zLevel == 1.55) {
                    getUsersRef("stepgoal").setValue(10000);
                } else {
                    getUsersRef("stepgoal").setValue(12000);
                }
            }
        });

        imgHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgAvg.setAlpha((float) 0.5);
                tvAvg.setAlpha((float)0.5);
                imgLow.setAlpha((float) 0.5);
                tvLow.setAlpha((float)0.5);
                imgHigh.setAlpha((float) 1);
                tvHigh.setAlpha((float)1);

                zLevel = (float) 1.9;
            }
        });
        imgAvg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgHigh.setAlpha((float) 0.5);
                tvHigh.setAlpha((float)0.5);
                imgLow.setAlpha((float) 0.5);
                tvLow.setAlpha((float)0.5);
                imgAvg.setAlpha((float) 1);
                tvAvg.setAlpha((float) 1);

                zLevel = (float) 1.55;
            }
        });
        imgLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgAvg.setAlpha((float) 0.5);
                tvAvg.setAlpha((float)0.5);
                imgHigh.setAlpha((float) 0.5);
                tvHigh.setAlpha((float)0.5);
                imgLow.setAlpha((float) 1);
                tvLow.setAlpha((float) 1);

                zLevel = (float) 1.2;
            }
        });
    }
    private DatabaseReference getUsersRef(String ref) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        return mDatabase.child("Users").child(userId).child(ref);
    }

}
