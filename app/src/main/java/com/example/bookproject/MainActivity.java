package com.example.bookproject;
import android.content.Intent;
import android.os.Bundle;
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
    }

    private void initViews() {
        rvPlaystationCards = findViewById(R.id.rv_playstation_cards);
    }

    private void setupPlayStationCards() {
        playstationList = new ArrayList<>();

        // Add 5 PlayStation cards with alternating colors
        playstationList.add(new PlayStation(1, "Play Station 1", "Available", PlayStationAdapter.COLOR_ORANGE));
        playstationList.add(new PlayStation(2, "Play Station 2", "Available", PlayStationAdapter.COLOR_MINT));
        playstationList.add(new PlayStation(3, "Play Station 3", "Available", PlayStationAdapter.COLOR_ORANGE));
        playstationList.add(new PlayStation(4, "Play Station 4", "Available", PlayStationAdapter.COLOR_MINT));
        playstationList.add(new PlayStation(5, "Play Station 5", "Available", PlayStationAdapter.COLOR_ORANGE));

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

    private void navigateToBookingZone(PlayStation playStation) {
        Intent intent = new Intent(this, BookingZoneActivity.class);
        intent.putExtra("playstation_id", playStation.getId());
        intent.putExtra("playstation_name", playStation.getName());
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

    // PlayStation data model with color support
    public static class PlayStation {
        private int id;
        private String name;
        private String status;
        private int colorType;

        public PlayStation(int id, String name, String status, int colorType) {
            this.id = id;
            this.name = name;
            this.status = status;
            this.colorType = colorType;
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

        public int getColorType() {
            return colorType;
        }

        public void setColorType(int colorType) {
            this.colorType = colorType;
        }
    }
}