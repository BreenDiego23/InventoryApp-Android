package com.andrewtorrez.inventoryapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * CreateAccountActivity
 * A simple screen to register a new user (separate from login).
 * For demo purposes, this calls DatabaseHelper.createUser(username, password).
 * In a production app, passwords must be hashed/salted.
 */
public class CreateAccountActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back arrow
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnCreate = findViewById(R.id.btnCreateAccount);

        dbHelper = new DatabaseHelper(this);

        btnCreate.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(u) || TextUtils.isEmpty(p)) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = dbHelper.createUser(u, p);  // You’ll add this method below
            if (ok) {
                Toast.makeText(this, "Account created. Please log in.", Toast.LENGTH_SHORT).show();
                finish(); // go back to LoginActivity
            } else {
                Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}