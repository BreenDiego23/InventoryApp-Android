package com.andrewtorrez.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// This activity handles both user login and account creation
public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;      // Input fields for username and password
    private Button loginButton, createAccountButton;    // Buttons for login and account creation
    private DatabaseHelper dbHelper;                    // Database helper to handle authentication and data storage

    /**
     * Initializes the login screen, links UI elements, sets up database helper,
     * and configures button actions for login and account creation.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);        // Sets the layout for the login screen

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        // Link UI elements to their IDs from the layout
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        // Handle login button press
        loginButton.setOnClickListener(view -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();

            // Check if the entered credentials match a record in the database
            if (dbHelper.checkUser(username, password)) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                // Navigate to InventoryActivity upon successful login
                startActivity(new Intent(LoginActivity.this, InventoryActivity.class));
            } else {
                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle create account button press
        createAccountButton.setOnClickListener(view -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();

            // Validate input fields before attempting to create an account
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Add new user credentials to the database
                dbHelper.addUser(username, password);
                Toast.makeText(LoginActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
            }
        });
    }
}