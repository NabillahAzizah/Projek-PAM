package com.example.bookproject;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.bookproject.model.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookedActivity extends AppCompatActivity {

    private LinearLayout emptyLayout;
    private CardView bookedLayout;
    private Button cancelButton;
    private ImageView ivBack;
    private Button statusButton;
    private TextView deviceName, userEmail, bookingDate, sessionTime;
    private TextView inUseText;
    private TextView timerText;
    private TextView statusText;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Booking currentBooking;
    private ValueEventListener bookingListener;

    // Timer
    private Handler handler;
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Initialize Handler for timer
        handler = new Handler(Looper.getMainLooper());

        initViews();
        setupClickListeners();
        loadCurrentBooking();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        bookedLayout = findViewById(R.id.booked_layout);
        emptyLayout = findViewById(R.id.empty_layout);
        cancelButton = findViewById(R.id.cancelButton);
        statusButton = findViewById(R.id.statusButton);
        deviceName = findViewById(R.id.device_name);
        userEmail = findViewById(R.id.user_email);
        bookingDate = findViewById(R.id.booking_date);
        sessionTime = findViewById(R.id.session_time);
        inUseText = findViewById(R.id.inUseText);
        timerText = findViewById(R.id.timerText);
        statusText = findViewById(R.id.statusText);
    }

    private void loadCurrentBooking() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showEmptyState();
            return;
        }

        // Listen for current user's active booking
        bookingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Booking activeBooking = null;

                // Find the most recent active booking
                for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                    Booking booking = bookingSnapshot.getValue(Booking.class);
                    if (booking != null && !booking.getStatus().equals("CANCELLED")) {
                        // Check if this booking is still valid (not expired)
                        if (booking.isExpired() && booking.getStatus().equals("BOOKED")) {
                            // Auto-expire the booking
                            expireBooking(booking.getBookingId());
                        } else if (!booking.getStatus().equals("EXPIRED")) {
                            activeBooking = booking;
                            break; // Use the first active booking found
                        }
                    }
                }

                currentBooking = activeBooking;
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BookedActivity.this, "Failed to load booking data", Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        };

        dbRef.child("bookings").child(currentUser.getUid())
                .addValueEventListener(bookingListener);
    }

    private void updateUI() {
        if (currentBooking != null) {
            showBookingCard();
            updateBookingDisplay();
            setupStatusButton();
            startTimer();
        } else {
            showEmptyState();
        }
    }

    private void showBookingCard() {
        bookedLayout.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        cancelButton.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        bookedLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.GONE);
        stopTimer();
    }

    private void updateBookingDisplay() {
        if (currentBooking == null) return;

        deviceName.setText(currentBooking.getPlaystationName());
        userEmail.setText(currentBooking.getUserEmail());
        bookingDate.setText(currentBooking.getBookingDate());

        String sessionText = "Session " + currentBooking.getSessionNumber() + "\n" + currentBooking.getTimeRange();
        sessionTime.setText(sessionText);
    }

    private void setupStatusButton() {
        if (currentBooking == null) return;

        String status = currentBooking.getStatus();

        switch (status) {
            case "BOOKED":
                statusButton.setVisibility(View.VISIBLE);
                statusButton.setText("Use");
                statusButton.setEnabled(true);
                statusButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green_500));
                statusButton.setOnClickListener(v -> usePlayStation());

                inUseText.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                break;

            case "IN_USE":
                statusButton.setVisibility(View.GONE);
                inUseText.setVisibility(View.VISIBLE);
                inUseText.setText("In Use");
                inUseText.setTextColor(ContextCompat.getColor(this, R.color.green_500));
                inUseText.setTypeface(null, Typeface.BOLD);

                statusText.setVisibility(View.GONE);
                timerText.setVisibility(View.GONE);
                break;

            case "CANCELLED":
                statusButton.setVisibility(View.GONE);
                inUseText.setVisibility(View.GONE);
                statusText.setVisibility(View.VISIBLE);
                statusText.setText("Cancelled");
                statusText.setTextColor(ContextCompat.getColor(this, R.color.red_500));
                statusText.setTypeface(null, Typeface.ITALIC);

                timerText.setVisibility(View.GONE);
                break;

            case "EXPIRED":
                statusButton.setVisibility(View.GONE);
                inUseText.setVisibility(View.GONE);
                statusText.setVisibility(View.VISIBLE);
                statusText.setText("Expired");
                statusText.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
                statusText.setTypeface(null, Typeface.ITALIC);

                timerText.setVisibility(View.GONE);
                break;
        }
    }

    private void startTimer() {
        if (currentBooking == null || !currentBooking.getStatus().equals("BOOKED")) {
            timerText.setVisibility(View.GONE);
            return;
        }

        timerText.setVisibility(View.VISIBLE);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentBooking != null && currentBooking.getStatus().equals("BOOKED")) {
                    long remainingMinutes = currentBooking.getRemainingMinutes();

                    if (remainingMinutes <= 0) {
                        // Auto-expire
                        expireBooking(currentBooking.getBookingId());
                        return;
                    }

                    long seconds = ((currentBooking.getExpiresAt() - System.currentTimeMillis()) / 1000) % 60;
                    timerText.setText(String.format(Locale.getDefault(), "Auto-cancel in: %d:%02d", remainingMinutes, seconds));

                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.post(timerRunnable);
    }

    private void stopTimer() {
        if (timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
        if (timerText != null) {
            timerText.setVisibility(View.GONE);
        }
    }

    private void usePlayStation() {
        if (currentBooking == null) return;

        // Update booking status to IN_USE
        dbRef.child("bookings")
                .child(currentBooking.getUserId())
                .child(currentBooking.getBookingId())
                .child("status")
                .setValue("IN_USE")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "PlayStation is now in use", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }

    private void expireBooking(String bookingId) {
        if (currentBooking == null) return;

        // Update booking status to EXPIRED
        dbRef.child("bookings")
                .child(currentBooking.getUserId())
                .child(bookingId)
                .child("status")
                .setValue("EXPIRED")
                .addOnSuccessListener(aVoid -> {
                    // Update PlayStation status back to Available
                    updatePlayStationStatus(currentBooking.getPlaystationId(), "Available");
                });
    }

    private void setupClickListeners() {
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        if (cancelButton != null) {
            cancelButton.setOnClickListener(v -> showCancelConfirmationDialog());
        }
    }

    private void showCancelConfirmationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_cancel_confirmation);

        Button btnNo = dialog.findViewById(R.id.btnNo);
        Button btnYes = dialog.findViewById(R.id.btnYes);

        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnYes.setOnClickListener(v -> {
            cancelBooking();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void cancelBooking() {
        if (currentBooking == null) return;

        // Update booking status to CANCELLED
        dbRef.child("bookings")
                .child(currentBooking.getUserId())
                .child(currentBooking.getBookingId())
                .child("status")
                .setValue("CANCELLED")
                .addOnSuccessListener(aVoid -> {
                    // Update PlayStation status back to Available
                    updatePlayStationStatus(currentBooking.getPlaystationId(), "Available");
                    Toast.makeText(this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePlayStationStatus(int playstationId, String status) {
        dbRef.child("playstation_status").child(String.valueOf(playstationId))
                .child("status").setValue(status);

        if ("Available".equals(status)) {
            // Remove occupied_until when becoming available
            dbRef.child("playstation_status").child(String.valueOf(playstationId))
                    .child("occupied_until").removeValue();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove listener
        if (bookingListener != null && mAuth.getCurrentUser() != null) {
            dbRef.child("bookings").child(mAuth.getCurrentUser().getUid())
                    .removeEventListener(bookingListener);
        }

        // Stop timer
        stopTimer();
    }
}