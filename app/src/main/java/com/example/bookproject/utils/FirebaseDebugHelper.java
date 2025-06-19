package com.example.bookproject.utils;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDebugHelper {
    private static final String TAG = "FirebaseDebug";

    public static void debugFirebaseConfiguration() {
        try {
            // Check Firebase Auth
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();

            Log.d(TAG, "=== FIREBASE DEBUG INFO ===");
            Log.d(TAG, "Firebase Auth initialized: " + (auth != null));

            if (user != null) {
                Log.d(TAG, "User logged in: TRUE");
                Log.d(TAG, "User UID: " + user.getUid());
                Log.d(TAG, "User Email: " + user.getEmail());
                Log.d(TAG, "Email Verified: " + user.isEmailVerified());
            } else {
                Log.e(TAG, "User logged in: FALSE");
            }

            // Check Firebase Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            Log.d(TAG, "Database URL: " + database.getReference().toString());

            // Test database connection
            database.getReference().child("test").setValue("connection_test")
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Database write test: SUCCESS");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Database write test: FAILED - " + e.getMessage());
                    });

        } catch (Exception e) {
            Log.e(TAG, "Firebase configuration error: " + e.getMessage());
        }
    }
}