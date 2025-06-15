package com.example.bookproject;

import android.app.Dialog;
import android.os.AsyncTask;
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

import com.example.bookproject.database.AppDatabase;
import com.example.bookproject.database.Booking;
import com.example.bookproject.database.BookingDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookedActivity extends AppCompatActivity {

    private LinearLayout emptyLayout;
    private CardView bookedLayout;
    private Button cancelButton;
    private Button statusButton;
    private TextView deviceName, userEmail, bookingDate, sessionTime;
    private TextView inUseText;
    private TextView cancelledText;

    private boolean isUsed = false;
    private boolean isCancelled = false;

    private BookingDao bookingDao;
    private Booking currentBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);

        // Init UI
        bookedLayout = findViewById(R.id.booked_layout);
        emptyLayout = findViewById(R.id.empty_layout);
        cancelButton = findViewById(R.id.cancelButton);
        statusButton = findViewById(R.id.statusButton);
        deviceName = findViewById(R.id.device_name);
        userEmail = findViewById(R.id.user_email);
        bookingDate = findViewById(R.id.booking_date);
        sessionTime = findViewById(R.id.session_time);
        inUseText = findViewById(R.id.inUseText);
        cancelledText = findViewById(R.id.cancelledText);

        AppDatabase db = AppDatabase.getDatabase(this);
        bookingDao = db.bookingDao();

        checkCurrentBooking("user123");

        statusButton.setOnClickListener(v -> {
            if (!isUsed) {
                isUsed = true;
                updateBookingStatus(currentBooking.getId(), "DIGUNAKAN");
                updateUI();
                Toast.makeText(this, "Device is now in use", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> showCancelConfirmationDialog());
    }

    private void checkCurrentBooking(String userId) {
        AsyncTask.execute(() -> {
            Booking booking = bookingDao.getCurrentBooking(userId, new Date());

            runOnUiThread(() -> {
                if (booking != null) {
                    currentBooking = booking;
                    isUsed = "DIGUNAKAN".equals(booking.getStatus());
                    isCancelled = "DIBATALKAN".equals(booking.getStatus());
                    updateUI();
                } else {
                    currentBooking = null;
                    updateUI(); // No booking
                }
            });
        });
    }

    private void updateBookingStatus(long bookingId, String newStatus) {
        AsyncTask.execute(() -> {
            bookingDao.updateBookingStatus(bookingId, newStatus);
        });
    }

    private void updateUI() {
        if (currentBooking != null) {
            bookedLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            cancelButton.setVisibility(View.VISIBLE);

            deviceName.setText("Play Station 1");
            userEmail.setText("Khemswahl23@student.ub.ac.id");
            bookingDate.setText("Monday, 26/05/2025");
            sessionTime.setText("08:45 - 09:45 am");

            if (isCancelled) {
                statusButton.setVisibility(View.GONE);
                inUseText.setVisibility(View.GONE);
                cancelledText.setVisibility(View.VISIBLE);
                cancelledText.setText("Cancelled");
                cancelledText.setTextColor(ContextCompat.getColor(this, R.color.red_500));
                cancelledText.setTypeface(null, Typeface.ITALIC);
            } else if (isUsed) {
                statusButton.setVisibility(View.GONE);
                inUseText.setVisibility(View.VISIBLE);
                cancelledText.setVisibility(View.GONE);
                inUseText.setText("In Use");
                inUseText.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
                inUseText.setTypeface(null, Typeface.ITALIC);
            } else {
                statusButton.setVisibility(View.VISIBLE);
                inUseText.setVisibility(View.GONE);
                cancelledText.setVisibility(View.GONE);
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
        isCancelled = true;
        updateUI();
        if (currentBooking != null) {
            updateBookingStatus(currentBooking.getId(), "DIBATALKAN");
        }

        Toast.makeText(this, "Booking berhasil dibatalkan", Toast.LENGTH_SHORT).show();
    }
}
