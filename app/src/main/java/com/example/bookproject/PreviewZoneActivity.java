package com.example.bookproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.bookproject.model.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_zone);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

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

        // Set user email from Firebase Auth
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
        } else {
            tvEmail.setText("user@student.ub.ac.id");
        }

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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SecondActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Disable button to prevent double booking
        btnBookingNow.setEnabled(false);

        // Create booking object
        Booking booking = new Booking(
                playstationId,
                playstationName,
                currentUser.getEmail(),
                currentUser.getUid(),
                selectedDate,
                timeRange,
                duration,
                sessionNumber
        );

        // Save to Firebase
        String bookingId = dbRef.child("bookings").push().getKey();
        if (bookingId != null) {
            booking.setBookingId(bookingId);

            // Save booking
            dbRef.child("bookings").child(currentUser.getUid()).child(bookingId)
                    .setValue(booking)
                    .addOnSuccessListener(aVoid -> {
                        // Update PlayStation status to occupied
                        updatePlayStationStatus(playstationId, "Occupied");

                        // Show success message
                        Toast.makeText(PreviewZoneActivity.this, "Booking confirmed successfully!", Toast.LENGTH_LONG).show();

                        // Navigate back to home
                        Intent intent = new Intent(PreviewZoneActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnBookingNow.setEnabled(true);
                        Toast.makeText(PreviewZoneActivity.this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            btnBookingNow.setEnabled(true);
            Toast.makeText(this, "Failed to generate booking ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePlayStationStatus(int playstationId, String status) {
        dbRef.child("playstation_status").child(String.valueOf(playstationId))
                .child("status").setValue(status);

        // Set occupied until time (for the session duration)
        if ("Occupied".equals(status)) {
            long occupiedUntil = System.currentTimeMillis() + (60 * 60 * 1000); // 1 hour default
            dbRef.child("playstation_status").child(String.valueOf(playstationId))
                    .child("occupied_until").setValue(occupiedUntil);
        }
    }
}