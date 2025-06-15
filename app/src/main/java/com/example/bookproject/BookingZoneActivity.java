//BookingZoneActivity.java - FIXED VERSION
package com.example.bookproject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingZoneActivity extends AppCompatActivity {

    private TextView tvSelectedDate;
    private RecyclerView rvTimeSlots;
    private View calendarOverlay;
    private CalendarView calendarView;
    private ImageView ivBack, ivPrevDate, ivNextDate;

    // PlayStation card elements
    private LinearLayout cardBackground;
    private TextView tvPlaystationCardName;
    private ImageView ivController;

    private TimeSlotAdapter timeSlotAdapter;
    private List<TimeSlot> timeSlotList;
    private Calendar selectedCalendar;
    private String playstationName;
    private int playstationId;
    private int cardType; // 0 = orange (bg_item), 1 = mint (bg_item2)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_zone);

        getIntentData();
        initViews();
        setupPlayStationCard();
        setupTimeSlots();
        setupClickListeners();
        // JANGAN panggil setupCalendar() di sini - akan crash!
    }

    private void getIntentData() {
        Intent intent = getIntent();
        playstationId = intent.getIntExtra("playstation_id", 1);
        playstationName = intent.getStringExtra("playstation_name");
        cardType = intent.getIntExtra("card_type", 0); // Default orange

        if (playstationName == null) {
            playstationName = "Play Station 1";
        }

        Log.d("BookingZone", "PlayStation: " + playstationName + ", CardType: " + cardType);
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        ivPrevDate = findViewById(R.id.iv_prev_date);
        ivNextDate = findViewById(R.id.iv_next_date);
        rvTimeSlots = findViewById(R.id.rv_time_slots);
        calendarOverlay = findViewById(R.id.calendar_overlay);

        // JANGAN INIT CALENDAR VIEW DI SINI - AKAN NULL!
        // calendarView = calendarOverlay.findViewById(R.id.calendar_view); // CRASH!

        // PlayStation card elements
        cardBackground = findViewById(R.id.card_background);
        tvPlaystationCardName = findViewById(R.id.tv_playstation_name);
        ivController = findViewById(R.id.iv_controller);

        selectedCalendar = Calendar.getInstance();

        // Set PlayStation name dengan null check
        if (tvPlaystationCardName != null) {
            tvPlaystationCardName.setText(playstationName);
        }

        // Update date display
        updateDateDisplay();
    }

    private void setupPlayStationCard() {
        // Set PlayStation name
        if (tvPlaystationCardName != null) {
            tvPlaystationCardName.setText(playstationName);
        }

        // Set background berdasarkan card type
        if (cardBackground != null) {
            if (cardType == 0) {
                // Orange card - bg_item.png
                cardBackground.setBackgroundResource(R.drawable.bg_item);
                Log.d("BookingZone", "Set orange background");
            } else {
                // Mint card - bg_item2.png
                cardBackground.setBackgroundResource(R.drawable.bg_item2);
                Log.d("BookingZone", "Set mint background");
            }
        }
    }

    // SETUP CALENDAR HANYA SAAT DIBUTUHKAN - TIDAK DI onCreate!
    private void setupCalendar() {
        if (calendarView == null) {
            Log.e("BookingZone", "calendarView is null in setupCalendar");
            return;
        }

        try {
            // Set calendar date range (today + 3 days)
            Calendar today = Calendar.getInstance();
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.DAY_OF_MONTH, 3);

            calendarView.setMinDate(today.getTimeInMillis());
            calendarView.setMaxDate(maxDate.getTimeInMillis());
            calendarView.setDate(selectedCalendar.getTimeInMillis());

            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                    Calendar clickedDate = Calendar.getInstance();
                    clickedDate.set(year, month, dayOfMonth);

                    // Only allow today + 3 days
                    Calendar today = Calendar.getInstance();
                    Calendar maxAllowed = Calendar.getInstance();
                    maxAllowed.add(Calendar.DAY_OF_MONTH, 3);

                    if (clickedDate.getTimeInMillis() >= today.getTimeInMillis() &&
                            clickedDate.getTimeInMillis() <= maxAllowed.getTimeInMillis()) {
                        selectedCalendar.set(year, month, dayOfMonth);
                        updateDateDisplay();
                        hideCalendar();
                        refreshTimeSlots();
                    } else {
                        Toast.makeText(BookingZoneActivity.this,
                                "Please select a date within the next 3 days",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Log.d("BookingZone", "Calendar setup completed successfully");
        } catch (Exception e) {
            Log.e("BookingZone", "Error setting up calendar: " + e.getMessage());
        }
    }

    private void setupTimeSlots() {
        timeSlotList = new ArrayList<>();
        timeSlotList.add(new TimeSlot(1, "08:45 - 09.45", "60 Minutes"));
        timeSlotList.add(new TimeSlot(2, "09:45 - 10.45", "60 Minutes"));
        timeSlotList.add(new TimeSlot(3, "10:45 - 11.30", "45 Minutes"));
        timeSlotList.add(new TimeSlot(4, "12:45 - 13.45", "60 Minutes"));
        timeSlotList.add(new TimeSlot(5, "13:45 - 14.45", "60 Minutes"));
        timeSlotList.add(new TimeSlot(6, "14:45 - 15.30", "45 Minutes"));

        timeSlotAdapter = new TimeSlotAdapter(timeSlotList, new TimeSlotAdapter.OnTimeSlotClickListener() {
            @Override
            public void onTimeSlotClick(TimeSlot timeSlot) {
                navigateToPreviewZone(timeSlot);
            }
        });

        rvTimeSlots.setLayoutManager(new LinearLayoutManager(this));
        rvTimeSlots.setAdapter(timeSlotAdapter);
    }

    private void setupClickListeners() {
        if (ivBack != null) {
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        if (tvSelectedDate != null) {
            tvSelectedDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCalendar();
                }
            });
        }

        if (ivPrevDate != null) {
            ivPrevDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar today = Calendar.getInstance();
                    Calendar newDate = (Calendar) selectedCalendar.clone();
                    newDate.add(Calendar.DAY_OF_MONTH, -1);

                    if (newDate.getTimeInMillis() >= today.getTimeInMillis()) {
                        selectedCalendar = newDate;
                        updateDateDisplay();
                        refreshTimeSlots();
                    } else {
                        Toast.makeText(BookingZoneActivity.this,
                                "Cannot select past dates",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (ivNextDate != null) {
            ivNextDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar maxDate = Calendar.getInstance();
                    maxDate.add(Calendar.DAY_OF_MONTH, 3);
                    Calendar newDate = (Calendar) selectedCalendar.clone();
                    newDate.add(Calendar.DAY_OF_MONTH, 1);

                    if (newDate.getTimeInMillis() <= maxDate.getTimeInMillis()) {
                        selectedCalendar = newDate;
                        updateDateDisplay();
                        refreshTimeSlots();
                    } else {
                        Toast.makeText(BookingZoneActivity.this,
                                "Cannot select dates beyond 3 days",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Hide calendar when clicking outside
        if (calendarOverlay != null) {
            calendarOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideCalendar();
                }
            });
        }
    }

    private void updateDateDisplay() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());

        String dayName = dayFormat.format(selectedCalendar.getTime());
        String dateString = dateFormat.format(selectedCalendar.getTime());

        if (tvSelectedDate != null) {
            tvSelectedDate.setText(dayName + ", " + dateString);
        }

        // Update calendar overlay date if it's visible
        if (calendarOverlay != null && calendarOverlay.getVisibility() == View.VISIBLE) {
            TextView overlayDate = calendarOverlay.findViewById(R.id.tv_selected_date);
            if (overlayDate != null) {
                overlayDate.setText(dayName + ", " + dateString);
            }
        }
    }

    private void showCalendar() {
        Log.d("BookingZone", "showCalendar called");

        if (calendarOverlay == null) {
            Log.e("BookingZone", "calendarOverlay is null");
            Toast.makeText(this, "Calendar not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // INIT CALENDAR VIEW SAAT PERTAMA KALI DIBUTUHKAN
        if (calendarView == null) {
            Log.d("BookingZone", "Initializing calendar view for first time");
            calendarView = calendarOverlay.findViewById(R.id.calendar_view);

            if (calendarView != null) {
                setupCalendar(); // Setup calendar listener

                // Setup click listener untuk calendar view
                calendarView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do nothing - prevent event bubbling
                    }
                });
            } else {
                Log.e("BookingZone", "Failed to find calendar_view in overlay");
                Toast.makeText(this, "Calendar view not found", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        calendarOverlay.setVisibility(View.VISIBLE);

        if (calendarView != null) {
            calendarView.setDate(selectedCalendar.getTimeInMillis());
        }

        // Update PlayStation name in overlay
        TextView overlayPlaystationName = calendarOverlay.findViewById(R.id.tv_playstation_name);
        if (overlayPlaystationName != null) {
            overlayPlaystationName.setText(playstationName);
        }

        // Update calendar overlay card background
        LinearLayout overlayCardBackground = calendarOverlay.findViewById(R.id.card_background);
        if (overlayCardBackground != null) {
            if (cardType == 0) {
                overlayCardBackground.setBackgroundResource(R.drawable.bg_item);
            } else {
                overlayCardBackground.setBackgroundResource(R.drawable.bg_item2);
            }
        }

        // Update date in overlay
        updateDateDisplay();

        Log.d("BookingZone", "Calendar overlay shown successfully");
    }

    private void hideCalendar() {
        if (calendarOverlay != null) {
            calendarOverlay.setVisibility(View.GONE);
        }
    }

    private void refreshTimeSlots() {
        // Clear any previous selection
        if (timeSlotAdapter != null) {
            timeSlotAdapter.clearSelection();
        }

        // Refresh the adapter
        if (timeSlotAdapter != null) {
            timeSlotAdapter.notifyDataSetChanged();
        }
    }

    private void navigateToPreviewZone(TimeSlot timeSlot) {
        Intent intent = new Intent(this, PreviewZoneActivity.class);
        intent.putExtra("playstation_id", playstationId);
        intent.putExtra("playstation_name", playstationName);
        intent.putExtra("card_type", cardType);
        intent.putExtra("session_number", timeSlot.getSessionNumber());
        intent.putExtra("time_range", timeSlot.getTimeRange());
        intent.putExtra("duration", timeSlot.getDuration());
        intent.putExtra("selected_date", tvSelectedDate.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (calendarOverlay != null && calendarOverlay.getVisibility() == View.VISIBLE) {
            hideCalendar();
        } else {
            super.onBackPressed();
        }
    }

    // Inner class for TimeSlot data model
    public static class TimeSlot {
        private int sessionNumber;
        private String timeRange;
        private String duration;

        public TimeSlot(int sessionNumber, String timeRange, String duration) {
            this.sessionNumber = sessionNumber;
            this.timeRange = timeRange;
            this.duration = duration;
        }

        public int getSessionNumber() {
            return sessionNumber;
        }

        public String getTimeRange() {
            return timeRange;
        }

        public String getDuration() {
            return duration;
        }
    }
}