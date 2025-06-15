package com.example.garnerapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class BookedActivity extends AppCompatActivity {

    private LinearLayout emptyLayout;
    private CardView bookedLayout;
    private Button cancelButton;
    private Button statusButton;
    private TextView deviceName, userEmail, bookingDate, sessionTime;
    private TextView inUseText; // Changed from inUseTextView to match layout
    private boolean isBooked = false;
    private boolean isUsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);

        // Initialize views
        bookedLayout = findViewById(R.id.booked_layout);
        emptyLayout = findViewById(R.id.empty_layout);
        cancelButton = findViewById(R.id.cancelButton);
        statusButton = findViewById(R.id.statusButton);
        deviceName = findViewById(R.id.device_name);
        userEmail = findViewById(R.id.user_email);
        bookingDate = findViewById(R.id.booking_date);
        sessionTime = findViewById(R.id.session_time);
        inUseText = findViewById(R.id.inUseText);

        isBooked = checkBookingStatus();

        updateUI();

        statusButton.setOnClickListener(v -> {
            if (!isUsed) {
                isUsed = true;
                saveUsageStatus(true);
                updateUI();
                Toast.makeText(this, "Device is now in use", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> showCancelConfirmationDialog());
    }

    private boolean checkBookingStatus() {
        // In real app, check database
        return true;
    }

    private void updateUI() {
        if (isBooked) {
            bookedLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            cancelButton.setVisibility(View.VISIBLE);

            deviceName.setText("Play Station 1");
            userEmail.setText("Khemswahl23@student.ub.ac.id");
            bookingDate.setText("Monday, 26/05/2025");
            sessionTime.setText("08:45 - 09:45 am");

            if (isUsed) {
                statusButton.setVisibility(View.GONE);
                inUseText.setVisibility(View.VISIBLE);
                inUseText.setText("In Use");
                inUseText.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
                inUseText.setTypeface(null, Typeface.ITALIC);
            } else {
                statusButton.setVisibility(View.VISIBLE);
                inUseText.setVisibility(View.GONE);
                statusButton.setText("Use");
                statusButton.setEnabled(true);
                statusButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green_500));
            }
        } else {
            bookedLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.GONE);
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
        isBooked = false;
        isUsed = false;
        updateUI();
        Toast.makeText(this, "Booking berhasil dibatalkan", Toast.LENGTH_SHORT).show();
    }

    private void saveUsageStatus(boolean isUsed) {

    }
}