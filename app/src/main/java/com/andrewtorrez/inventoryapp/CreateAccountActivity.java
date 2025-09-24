package com.andrewtorrez.inventoryapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Screen for creating a new user account.
 * Validates inputs and writes a new user into the local DB via DatabaseHelper.
 */
public class CreateAccountActivity extends AppCompatActivity {

    private EditText etNewUsername, etNewPassword, etConfirmPassword;
    private Button btnCreateAccount;
    private DatabaseHelper dbHelper; // your existing helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);

        // Show Up (back) arrow in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(v -> {
            String username = etNewUsername.getText() != null ? etNewUsername.getText().toString().trim() : "";
            String password = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
            String confirm  = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

            // Basic validation
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
                Toast.makeText(this, "Please enter all fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert user (adjust if your DatabaseHelper returns long instead of boolean)
            dbHelper.addUser(username, password);
            boolean created = dbHelper.checkUser(username, password);
            // If your addUser returns long rowId, use:
            // long rowId = dbHelper.addUser(username, password);
            // boolean created = rowId != -1;

            if (created) {
                Toast.makeText(this, "Account created! Please log in.", Toast.LENGTH_SHORT).show();
                finish(); // back to Login
            } else {
                Toast.makeText(this, "Username may already exist. Try another.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}