package com.example.bookproject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
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

    private TextView tvSelectedDate, tvPlayStationName;
    private RecyclerView rvTimeSlots;
    private View calendarOverlay;
    private CalendarView calendarView;
    private ImageView ivBack, ivPrevDate, ivNextDate;
    private TimeSlotAdapter timeSlotAdapter;
    private List<TimeSlot> timeSlotList;
    private Calendar selectedCalendar;
    private String playstationName;
    private int playstationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_zone);

        getIntentData();
        initViews();
        setupCalendar();
        setupTimeSlots();
        setupClickListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        playstationId = intent.getIntExtra("playstation_id", 1);
        playstationName = intent.getStringExtra("playstation_name");
        if (playstationName == null) {
            playstationName = "Play Station 1";
        }
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvPlayStationName = findViewById(R.id.tv_playstation_name);
        ivPrevDate = findViewById(R.id.iv_prev_date);
        ivNextDate = findViewById(R.id.iv_next_date);
        rvTimeSlots = findViewById(R.id.rv_time_slots);
        calendarOverlay = findViewById(R.id.calendar_overlay);
        calendarView = calendarOverlay.findViewById(R.id.calendar_view);

        selectedCalendar = Calendar.getInstance();

        // Set PlayStation name
        tvPlayStationName.setText(playstationName);
    }

    private void setupCalendar() {
        updateDateDisplay();

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
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });

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

        // Hide calendar when clicking outside (background)
        calendarOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCalendar();
            }
        });

        // Prevent calendar from closing when clicking on calendar itself
        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing - prevent event bubbling
            }
        });
    }

    private void updateDateDisplay() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());

        String dayName = dayFormat.format(selectedCalendar.getTime());
        String dateString = dateFormat.format(selectedCalendar.getTime());

        tvSelectedDate.setText(dayName + ", " + dateString);

        // Update calendar overlay date if it's visible
        if (calendarOverlay.getVisibility() == View.VISIBLE) {
            TextView overlayDate = calendarOverlay.findViewById(R.id.tv_selected_date);
            if (overlayDate != null) {
                overlayDate.setText(dayName + ", " + dateString);
            }
        }
    }

    private void showCalendar() {
        calendarOverlay.setVisibility(View.VISIBLE);
        calendarView.setDate(selectedCalendar.getTimeInMillis());

        // Update PlayStation name in overlay
        TextView overlayPlaystationName = calendarOverlay.findViewById(R.id.tv_playstation_name);
        if (overlayPlaystationName != null) {
            overlayPlaystationName.setText(playstationName);
        }

        // Update date in overlay
        updateDateDisplay();
    }

    private void hideCalendar() {
        calendarOverlay.setVisibility(View.GONE);
    }

    private void refreshTimeSlots() {
        // Clear any previous selection
        if (timeSlotAdapter != null) {
            timeSlotAdapter.clearSelection();
        }

        // Here you could implement logic to check availability for selected date
        // For now, we'll just refresh the adapter
        if (timeSlotAdapter != null) {
            timeSlotAdapter.notifyDataSetChanged();
        }
    }

    private void navigateToPreviewZone(TimeSlot timeSlot) {
        Intent intent = new Intent(this, PreviewZoneActivity.class);
        intent.putExtra("playstation_id", playstationId);
        intent.putExtra("playstation_name", playstationName);
        intent.putExtra("session_number", timeSlot.getSessionNumber());
        intent.putExtra("time_range", timeSlot.getTimeRange());
        intent.putExtra("duration", timeSlot.getDuration());
        intent.putExtra("selected_date", tvSelectedDate.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (calendarOverlay.getVisibility() == View.VISIBLE) {
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