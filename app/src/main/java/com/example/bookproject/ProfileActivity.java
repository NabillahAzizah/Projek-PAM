package com.example.bookproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    TextView tvEmail, tvName, tvNim, tvMajor;
    CardView btnEdit;
    ImageView backLink, ivBack;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Inisialisasi tampilan utama
        tvEmail = findViewById(R.id.tv_email);
        tvName = findViewById(R.id.tv_name);
        tvNim = findViewById(R.id.tv_nim);
        tvMajor = findViewById(R.id.tv_major);
        btnEdit = findViewById(R.id.edit);
        backLink = findViewById(R.id.back_link);
        ivBack = findViewById(R.id.iv_back);

        // Load user data from Firebase
        loadUserProfile();

        // Ketika tombol Edit diklik
        btnEdit.setOnClickListener(v -> showEditDialog());

        // Ketika tombol logout/back diklik
        backLink.setOnClickListener(v -> showLogoutDialog());

        setupBottomNavigation();
        setupClickListeners();
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        userId = currentUser.getUid();

        // Set email from Firebase Auth
        String email = currentUser.getEmail();
        if (email != null) {
            tvEmail.setText(email);
        }

        // Load profile data from Realtime Database
        dbRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get username
                    String username = dataSnapshot.child("username").getValue(String.class);
                    if (username != null && !username.isEmpty()) {
                        tvName.setText(username);
                    } else {
                        // Fallback: extract name from email
                        setNameFromEmail(email);
                    }

                    // Get NIM (optional)
                    String nim = dataSnapshot.child("nim").getValue(String.class);
                    if (nim != null && !nim.isEmpty()) {
                        tvNim.setText(nim);
                    } else {
                        tvNim.setText("Not set"); // Default jika kosong
                    }

                    // Get Major (optional)
                    String major = dataSnapshot.child("major").getValue(String.class);
                    if (major != null && !major.isEmpty()) {
                        tvMajor.setText(major);
                    } else {
                        tvMajor.setText("Not set"); // Default jika kosong
                    }

                    Log.d(TAG, "Profile data loaded successfully");
                } else {
                    Log.d(TAG, "No profile data found, creating default");
                    // Create default profile if doesn't exist
                    createDefaultProfile(currentUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to load profile: " + databaseError.getMessage());
                Toast.makeText(ProfileActivity.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            String name = email.split("@")[0];
            // Capitalize first letter
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            tvName.setText(name);
        } else {
            tvName.setText("User");
        }
    }

    private void createDefaultProfile(FirebaseUser user) {
        // Create default profile with email and username, leave NIM and major empty
        String username = user.getDisplayName();
        if (username == null || username.isEmpty()) {
            // Extract from email
            String email = user.getEmail();
            if (email != null && email.contains("@")) {
                username = email.split("@")[0];
                username = username.substring(0, 1).toUpperCase() + username.substring(1);
            } else {
                username = "User";
            }
        }

        // Save to database
        dbRef.child("users").child(userId).child("username").setValue(username);
        dbRef.child("users").child(userId).child("email").setValue(user.getEmail());
        dbRef.child("users").child(userId).child("nim").setValue(""); // Empty by default
        dbRef.child("users").child(userId).child("major").setValue(""); // Empty by default

        // Update UI
        tvName.setText(username);
        tvNim.setText("Not set");
        tvMajor.setText("Not set");
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

        // Set nilai awal dari TextView
        editEmail.setText(tvEmail.getText().toString());
        editName.setText(tvName.getText().toString());

        // Set NIM dan Major, tapi kosongkan jika "Not set"
        String currentNim = tvNim.getText().toString();
        String currentMajor = tvMajor.getText().toString();

        editNim.setText(currentNim.equals("Not set") ? "" : currentNim);
        editMajor.setText(currentMajor.equals("Not set") ? "" : currentMajor);

        // Email tidak bisa diedit (readonly)
        editEmail.setEnabled(false);
        editEmail.setAlpha(0.6f);

        // Saat klik Update
        btnUpdate.setOnClickListener(view -> {
            String newName = editName.getText().toString().trim();
            String newNim = editNim.getText().toString().trim();
            String newMajor = editMajor.getText().toString().trim();

            if (newName.isEmpty()) {
                editName.setError("Name cannot be empty");
                return;
            }

            // Save to Firebase
            saveProfileToFirebase(newName, newNim, newMajor);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void saveProfileToFirebase(String name, String nim, String major) {
        if (userId == null) return;

        // Update di Firebase
        dbRef.child("users").child(userId).child("username").setValue(name);
        dbRef.child("users").child(userId).child("nim").setValue(nim);
        dbRef.child("users").child(userId).child("major").setValue(major)
                .addOnSuccessListener(aVoid -> {
                    // Update UI
                    tvName.setText(name);
                    tvNim.setText(nim.isEmpty() ? "Not set" : nim);
                    tvMajor.setText(major.isEmpty() ? "Not set" : major);

                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Profile updated in Firebase");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update profile", e);
                });
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
            // Sign out from Firebase
            mAuth.signOut();
            navigateToLogin();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupClickListeners() {
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
    }

    private void setupBottomNavigation() {
        View bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation == null) return;

        // Find navigation tabs
        View bookedTab = bottomNavigation.findViewById(R.id.booked_tab);
        View profileTab = bottomNavigation.findViewById(R.id.profile_tab);

        // Set click listeners
        if (bookedTab != null) {
            bookedTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToBookedActivity();
                }
            });
        }

        if (profileTab != null) {
            profileTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already in ProfileActivity, do nothing
                }
            });
        }
    }

    private void navigateToBookedActivity() {
        Intent intent = new Intent(this, BookedActivity.class);
        startActivity(intent);
    }
}