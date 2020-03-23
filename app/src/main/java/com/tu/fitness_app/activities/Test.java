package com.tu.fitness_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tu.fitness_app.R;

public class Test extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        button = findViewById(R.id.button2);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(Test.this, Calendar.class);
            startActivity(intent);
        });
    }
}
