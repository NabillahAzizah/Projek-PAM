package com.example.garnerapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailPsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ps);

        TextView tvDetail = findViewById(R.id.tv_detail);

        // Ambil data dari intent
        String psName = getIntent().getStringExtra("ps_name");
        tvDetail.setText("Detail untuk: " + psName);
    }
}
