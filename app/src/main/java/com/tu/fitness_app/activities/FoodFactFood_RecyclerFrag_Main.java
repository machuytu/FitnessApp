package com.tu.fitness_app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tu.fitness_app.R;

import java.util.Date;

public class FoodFactFood_RecyclerFrag_Main extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Toolbar mToolbar;
    private ActionBar mActionBar;

    public static float calRef1 = 0f;
    public static float user_fat1 = 0f;
    public static float user_carbs1 = 0f;
    public static float user_protein1 = 0f;

    Date today = new Date();

    private DatabaseReference getCaloriesRef(String ref) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
        return mDatabase.child("Calories").child(userId).child(date).child(ref);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food__recycler_frag__main);
        //Load common fragment
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rcFrag_main, FoodFactFood_RecyclerView_Main.newInstance()).commit();
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Toolbar
        mToolbar = findViewById(R.id.recycle_toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        getCaloriesRef("totalcalories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    calRef1 = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    calRef1 = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        getCaloriesRef("totalfat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_fat1 = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    user_fat1 = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        getCaloriesRef("totalcarbs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_carbs1 = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    user_carbs1 = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        getCaloriesRef("totalprotein").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_protein1 = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    user_protein1 = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(FoodFactFood_RecyclerFrag_Main.this, OverviewActivity.class);
        startActivity(intent);
        finish();
    }
}
