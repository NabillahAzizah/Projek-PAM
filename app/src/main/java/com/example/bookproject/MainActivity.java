// MainActivity.java
package com.example.bookproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvPlaystationCards;
    private PlayStationAdapter playstationAdapter;
    private List<PlayStation> playstationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupPlayStationCards();
        setupBottomNavigation();
    }

    private void initViews() {
        rvPlaystationCards = findViewById(R.id.rv_playstation_cards);
    }

    private void setupPlayStationCards() {
        playstationList = new ArrayList<>();

        // Add 5 PlayStation cards - semua menggunakan bg_item.png yang sama
        playstationList.add(new PlayStation(1, "Play Station 1", "Available"));
        playstationList.add(new PlayStation(2, "Play Station 2", "Available"));
        playstationList.add(new PlayStation(3, "Play Station 3", "Available"));
        playstationList.add(new PlayStation(4, "Play Station 4", "Available"));
        playstationList.add(new PlayStation(5, "Play Station 5", "Available"));

        playstationAdapter = new PlayStationAdapter(playstationList, new PlayStationAdapter.OnPlayStationClickListener() {
            @Override
            public void onPlayStationClick(PlayStation playStation) {
                if (playStation.getStatus().equals("Available")) {
                    navigateToBookingZone(playStation);
                } else {
                    Toast.makeText(MainActivity.this, "PlayStation is currently occupied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rvPlaystationCards.setLayoutManager(new LinearLayoutManager(this));
        rvPlaystationCards.setAdapter(playstationAdapter);
    }

    private void setupBottomNavigation() {
        // Find the booked icon layout in bottom navigation
        View bookedIconLayout = findViewById(R.id.bottom_navigation).findViewById(R.id.av_booked);

        // Set click listener for booked icon
        bookedIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToBookedActivity();
            }
        });
    }

    private void navigateToBookingZone(PlayStation playStation) {
        Intent intent = new Intent(this, BookingZoneActivity.class);
        intent.putExtra("playstation_id", playStation.getId());
        intent.putExtra("playstation_name", playStation.getName());

        // Pass card type berdasarkan posisi untuk menentukan background
        int cardType = (playStation.getId() - 1) % 2; // 0 = orange, 1 = mint
        intent.putExtra("card_type", cardType);

        startActivity(intent);
    }

    private void navigateToBookedActivity() {
        Intent intent = new Intent(this, BookedActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from other activities
        if (playstationAdapter != null) {
            playstationAdapter.notifyDataSetChanged();
        }
    }

    // PlayStation data model - simplified without color type
    public static class PlayStation {
        private int id;
        private String name;
        private String status;

        public PlayStation(int id, String name, String status) {
            this.id = id;
            this.name = name;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}