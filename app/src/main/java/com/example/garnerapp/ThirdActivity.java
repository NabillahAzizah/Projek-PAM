package com.example.garnerapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ThirdActivity extends AppCompatActivity {

    // Variabel untuk UI dan Firebase
    EditText usernameInput, emailInput, passwordInput;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    // Variabel untuk Google Sign-In
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // Inisialisasi View
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        LinearLayout googleSignUpLayout = findViewById(R.id.googleSignUpLayout); // Inisialisasi tombol Google

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        // Fungsionalitas Link "Sign In"
        handleSignInLink();

        // Fungsionalitas Tombol Sign Up dengan Email/Password
        btnSignUp.setOnClickListener(view -> signUpWithEmail());


        /************************************************************/
        /* BAGIAN BARU: Fungsionalitas Tombol Sign Up dengan Google   */
        /************************************************************/

        // 1. Konfigurasi Opsi Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 2. Set OnClickListener untuk Tombol Google
        googleSignUpLayout.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void signUpWithEmail() {
        String username = usernameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            dbRef.child(userId).child("username").setValue(username);
                            dbRef.child(userId).child("email").setValue(email);
                            Toast.makeText(this, "Sign Up Success!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleSignInLink() {
        TextView signInLink = findViewById(R.id.signInLink);
        String fullText = signInLink.getText().toString();
        SpannableString ss = new SpannableString(fullText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                finish(); // Kembali ke activity sebelumnya
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(Color.WHITE);
            }
        };

        String textToSpan = "Sign In";
        int startIndex = fullText.indexOf(textToSpan);
        if (startIndex != -1) {
            ss.setSpan(clickableSpan, startIndex, startIndex + textToSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        signInLink.setText(ss);
        signInLink.setMovementMethod(LinkMovementMethod.getInstance());
        signInLink.setHighlightColor(Color.TRANSPARENT);
    }


    /************************************************************/
    /* BAGIAN BARU: Metode untuk menangani hasil dari Google      */
    /************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign Up Failed. Error: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Simpan informasi pengguna ke Realtime Database
                            String userId = user.getUid();
                            dbRef.child(userId).child("username").setValue(user.getDisplayName());
                            dbRef.child(userId).child("email").setValue(user.getEmail());
                            Toast.makeText(this, "Sign Up with Google Success!", Toast.LENGTH_SHORT).show();
                            // Anda bisa tambahkan navigasi ke halaman utama di sini jika perlu
                        }
                    } else {
                        Toast.makeText(this, "Firebase Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}