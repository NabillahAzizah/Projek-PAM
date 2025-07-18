package com.example.bookproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookproject.adapter.PlayStationAdapter;
import com.example.bookproject.utils.FirebaseDebugHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvPlaystationCards;
    private PlayStationAdapter playstationAdapter;
    private List<PlayStation> playstationList;
    private DatabaseReference dbRef;
    private ValueEventListener playstationStatusListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Debug Firebase
        FirebaseDebugHelper.debugFirebaseConfiguration();

        initViews();
        setupPlayStationCards();
        setupBottomNavigation();
        loadPlayStationStatus();
    }

    private void initViews() {
        rvPlaystationCards = findViewById(R.id.rv_playstation_cards);
    }

    private void setupPlayStationCards() {
        playstationList = new ArrayList<>();

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

    private void loadPlayStationStatus() {
        playstationStatusListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot statusSnapshot : dataSnapshot.getChildren()) {
                    try {
                        int playstationId = Integer.parseInt(statusSnapshot.getKey());
                        String status = statusSnapshot.child("status").getValue(String.class);
                        Long occupiedUntil = statusSnapshot.child("occupied_until").getValue(Long.class);

                        if ("Occupied".equals(status) && occupiedUntil != null) {
                            if (System.currentTimeMillis() > occupiedUntil) {
                                updatePlayStationStatusInFirebase(playstationId, "Available");
                                status = "Available";
                            }
                        }

                        updatePlayStationStatusLocal(playstationId, status != null ? status : "Available");

                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load PlayStation status", Toast.LENGTH_SHORT).show();
            }
        };

        dbRef.child("playstation_status").addValueEventListener(playstationStatusListener);
    }

    private void updatePlayStationStatusLocal(int playstationId, String newStatus) {
        for (int i = 0; i < playstationList.size(); i++) {
            PlayStation ps = playstationList.get(i);
            if (ps.getId() == playstationId && !ps.getStatus().equals(newStatus)) {
                ps.setStatus(newStatus);
                playstationAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    private void updatePlayStationStatusInFirebase(int playstationId, String status) {
        dbRef.child("playstation_status").child(String.valueOf(playstationId))
                .child("status").setValue(status);

        if ("Available".equals(status)) {
            dbRef.child("playstation_status").child(String.valueOf(playstationId))
                    .child("occupied_until").removeValue();
        }
    }

    private void setupBottomNavigation() {
        View bottomNavigation = findViewById(R.id.bottom_navigation);

        // Tombol Booked
        View bookedIconLayout = bottomNavigation.findViewById(R.id.av_booked);
        bookedIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToBookedActivity();
            }
        });

        // Tombol Profile
        View profileIconLayout = bottomNavigation.findViewById(R.id.profile_tab);
        profileIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfileActivity();
            }
        });
    }

    private void navigateToBookingZone(PlayStation playStation) {
        Intent intent = new Intent(this, BookingZoneActivity.class);
        intent.putExtra("playstation_id", playStation.getId());
        intent.putExtra("playstation_name", playStation.getName());
        int cardType = (playStation.getId() - 1) % 2;
        intent.putExtra("card_type", cardType);
        startActivity(intent);
    }

    private void navigateToBookedActivity() {
        Intent intent = new Intent(this, BookedActivity.class);
        startActivity(intent);
    }

    private void navigateToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playstationAdapter != null) {
            playstationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playstationStatusListener != null) {
            dbRef.child("playstation_status").removeEventListener(playstationStatusListener);
        }
    }

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
