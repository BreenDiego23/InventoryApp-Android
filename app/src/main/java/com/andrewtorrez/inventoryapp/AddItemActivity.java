package com.andrewtorrez.inventoryapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for adding a new item to the inventory.
 * Allows the user to input item name and quantity, then saves it to the database.
 */
public class AddItemActivity extends AppCompatActivity {

    // Declare UI elements and database helper
    private EditText itemNameField, itemQuantityField;
    private Button saveItemButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_add_item);  // Must match the corresponding XML layout file

        // Enable the back button in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Link UI elements to XML components
        itemNameField = findViewById(R.id.itemNameField);
        itemQuantityField = findViewById(R.id.itemQuantityField);
        saveItemButton = findViewById(R.id.saveItemButton);
        // Initialize the database helper responsible for managing database operations
        dbHelper = new DatabaseHelper(this);  // Initialize the database helper

        // Set the button click listener to handle saving an item
        saveItemButton.setOnClickListener(v -> {
            // Get the text from the input fields and trim whitespace
            String name = itemNameField.getText().toString().trim();
            String quantityStr = itemQuantityField.getText().toString().trim();

            // Validate that both fields are filled
            if (name.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;  // Exit early if validation fails
            }

            int quantity;
            try {
                // Try to convert quantity to an integer
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                // Handle non-numeric input
                Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new inventory item object
            InventoryItem newItem = new InventoryItem(name, quantity);
            // Add the new item to the database
            dbHelper.addItem(newItem);
            Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();

            // Close this activity and return to the inventory screen
            finish(); // Close this activity and return to the inventory screen
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}