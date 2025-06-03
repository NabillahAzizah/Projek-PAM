package com.example.bookproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PreviewZoneActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvPlayStationName;
    private TextView tvEmail;
    private TextView tvBookingDate;
    private TextView tvDuration;
    private TextView tvSessionTime;
    private CardView btnBookingNow;

    private String playstationName;
    private int playstationId;
    private int sessionNumber;
    private String timeRange;
    private String duration;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_zone);

        getIntentData();
        initViews();
        loadBookingData();
        setupClickListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        playstationId = intent.getIntExtra("playstation_id", 1);
        playstationName = intent.getStringExtra("playstation_name");
        sessionNumber = intent.getIntExtra("session_number", 1);
        timeRange = intent.getStringExtra("time_range");
        duration = intent.getStringExtra("duration");
        selectedDate = intent.getStringExtra("selected_date");
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvPlayStationName = findViewById(R.id.tv_playstation_name);
        tvEmail = findViewById(R.id.tv_email);
        tvBookingDate = findViewById(R.id.tv_booking_date);
        tvDuration = findViewById(R.id.tv_duration);
        tvSessionTime = findViewById(R.id.tv_session_time);
        btnBookingNow = findViewById(R.id.btn_booking_now);
    }

    private void loadBookingData() {
        // Set PlayStation name
        tvPlayStationName.setText(playstationName != null ? playstationName : "Play Station 1");

        // Set default email (this would typically come from user session/login)
        tvEmail.setText("Khernewah123@student.ub.ac.id");

        // Set booking date
        tvBookingDate.setText(selectedDate != null ? selectedDate : "Monday, 26/05/2025");

        // Set duration
        tvDuration.setText(duration != null ? duration : "60 Minutes");

        // Set session time with session number
        String sessionText = "Session " + sessionNumber + "\n" + (timeRange != null ? timeRange : "08:45 - 09:45 am");
        tvSessionTime.setText(sessionText);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnBookingNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processBooking();
            }
        });
    }

    private void processBooking() {
        // Here you would typically:
        // 1. Send booking data to server
        // 2. Generate booking ticket
        // 3. Navigate to success screen or show confirmation

        // Show success message
        Toast.makeText(this, "Booking confirmed successfully!", Toast.LENGTH_LONG).show();

        // Navigate back to home (MainActivity) and clear the activity stack
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}