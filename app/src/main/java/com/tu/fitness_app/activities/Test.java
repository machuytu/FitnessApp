package com.tu.fitness_app.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tu.fitness_app.R;

public class Test extends AppCompatActivity {
    Button button;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference ref_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        ref_history = FirebaseDatabase.getInstance().getReference("history").child(userId);

        Query query = ref_history.orderByKey().startAt("2020-2-19").endAt("2020-2-19\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            private StringBuilder tv = new StringBuilder();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        tv.append(ds.getKey() + "\n");
                        Log.d("them", "them: " + tv);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err", "onCancelled: " + databaseError.toString());
            }
        });
    }
}
