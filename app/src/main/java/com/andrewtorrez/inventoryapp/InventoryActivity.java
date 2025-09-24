package com.andrewtorrez.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * This activity serves as the main dashboard for displaying, adding,
 * and managing inventory items.
 */
public class InventoryActivity extends AppCompatActivity {

    // UI elements
    private GridView inventoryGrid;
    private Button addItemButton;
    private Button smsButton;
    private DatabaseHelper dbHelper;
    private InventoryItemAdapter adapter;
    private TextView emptyMessage; // Message shown when inventory is empty

    /**
     * Initializes the UI, sets up button actions, and loads the inventory.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory); // Set the layout XML

        // Initialize UI elements and database helper
        dbHelper = new DatabaseHelper(this);
        inventoryGrid = findViewById(R.id.inventoryGrid);
        addItemButton = findViewById(R.id.addItemButton);
        smsButton = findViewById(R.id.smsButton);
        emptyMessage = findViewById(R.id.emptyMessage); // Link to the "no items" message

        loadInventory(); // Load inventory items into the grid

        // When "Add Item" is clicked, navigate to AddItemActivity
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        // When "Notify Low Stock" is clicked, navigate to SmsNotificationActivity
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, SmsNotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Refreshes the inventory list when returning from another activity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadInventory(); // Refresh inventory list
    }

    /**
     * Loads all items from the database, handles the empty state message,
     * and updates the grid view with an adapter.
     */
    private void loadInventory() {
        List<InventoryItem> items = dbHelper.getAllItems();

        // Check whether the database returned any results
        if (items.isEmpty()) {
            // No items: hide grid and show message
            inventoryGrid.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            // Items found: show grid and hide message
            inventoryGrid.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);

            // Set up the adapter and assign it to the grid
            // The adapter binds the items to the grid UI
            adapter = new InventoryItemAdapter(this, items, dbHelper);
            inventoryGrid.setAdapter(adapter);
        }
    }
}