package com.example.garnerapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView tvEmail, tvName, tvNim, tvMajor, backLink;
    Button btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inisialisasi tampilan utama
        tvEmail = findViewById(R.id.tv_email);
        tvName = findViewById(R.id.tv_name);
        tvNim = findViewById(R.id.tv_nim);
        tvMajor = findViewById(R.id.tv_major);
        btnEdit = findViewById(R.id.btn_edit);
        backLink = findViewById(R.id.back_link);

        // Ketika tombol Edit diklik
        btnEdit.setOnClickListener(v -> showEditDialog());

        // Ketika tombol logout/back diklik
        TextView backLink = findViewById(R.id.back_link);
        backLink.setOnClickListener(v -> showLogoutDialog());

    }

    // Dialog untuk edit biodata
    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText editEmail = dialogView.findViewById(R.id.edit_email);
        EditText editName = dialogView.findViewById(R.id.edit_name);
        EditText editNim = dialogView.findViewById(R.id.edit_nim);
        EditText editMajor = dialogView.findViewById(R.id.edit_major);
        Button btnUpdate = dialogView.findViewById(R.id.btn_update);

        // Set nilai awal
        editEmail.setText(tvEmail.getText().toString());
        editName.setText(tvName.getText().toString());
        editNim.setText(tvNim.getText().toString());
        editMajor.setText(tvMajor.getText().toString());

        // Saat klik Update
        btnUpdate.setOnClickListener(view -> {
            tvEmail.setText(editEmail.getText().toString());
            tvName.setText(editName.getText().toString());
            tvNim.setText(editNim.getText().toString());
            tvMajor.setText(editMajor.getText().toString());
            dialog.dismiss();
        });

        dialog.show();
    }

    // Dialog untuk konfirmasi keluar dari aplikasi
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_exit, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        Button btnYes = view.findViewById(R.id.btn_yes);
        Button btnNo = view.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            finishAffinity(); // keluar dari aplikasi
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
