package com.example.garnerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PsAdapter psAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recycle_contact);
        List<String> psList = Arrays.asList("Play Station 1", "Play Station 2", "Play Station 3", "Play Station 4", "Play Station 5");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        psAdapter = new PsAdapter(this, psList);
        recyclerView.setAdapter(psAdapter);

        // Inisialisasi ikon navigasi manual
        ImageView iconHome = findViewById(R.id.icon_home);
        ImageView iconProfile = findViewById(R.id.icon_profile);

        // Aksi saat ikon Home diklik
        iconHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookedActivity.class);
                startActivity(intent);
            }
        });

        // Aksi saat ikon Profile diklik
        iconProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
