package com.example.bookproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    ImageButton startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satu);

        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SecondActivity.class);
            startActivity(intent);
        });
    }
}