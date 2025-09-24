package com.andrewtorrez.inventoryapp;

import android.content.Context;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

// Custom adapter for displaying inventory items in a GridView
public class InventoryItemAdapter extends BaseAdapter {

    private Context context;                         // The context from the parent activity
    private List<InventoryItem> itemList;            // The list of inventory items to display
    private DatabaseHelper dbHelper;                 // Database helper for CRUD operations

    // Constructor for the adapter
    public InventoryItemAdapter(Context context, List<InventoryItem> itemList, DatabaseHelper dbHelper) {
        this.context = context;
        this.itemList = itemList;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return itemList.size(); // Total number of items in the list
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i); // Return item at the specified position
    }

    @Override
    public long getItemId(int i) {
        return itemList.get(i).getId(); // Return the ID of the item
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InventoryItem item = itemList.get(position); // Get the item at the current position

        // Inflate layout if this is the first time the view is created
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
        }

        // Link UI elements to the layout
        TextView itemName = convertView.findViewById(R.id.itemName);
        TextView quantityLabel = convertView.findViewById(R.id.quantityLabel);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        /**
         * Button to increase the quantity of the item and update the database/UI.
         */
        Button increaseButton = convertView.findViewById(R.id.increaseButton);
        /**
         * Button to decrease the quantity of the item, update the database/UI,
         * and trigger a low stock warning if the threshold is reached.
         */
        Button decreaseButton = convertView.findViewById(R.id.decreaseButton);

        // Display item name and quantity
        itemName.setText(item.getName());
        quantityLabel.setText("Qty: " + item.getQuantity());

        // Delete button functionality
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                .setTitle("Delete item?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteItem(item.getId());       // Remove item from database
                    itemList.remove(position);               // Remove item from list
                    notifyDataSetChanged();                  // Refresh the GridView
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
        });

        // Increase quantity button functionality
        increaseButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1); // Increment quantity
            dbHelper.updateItem(item.getId(), item.getName(), item.getQuantity(), "");
            quantityLabel.setText("Qty: " + item.getQuantity()); // Update label
            notifyDataSetChanged(); // Refresh UI
        });

        // Decrease quantity button functionality
        decreaseButton.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;
            if (newQty >= 0) {
                item.setQuantity(newQty); // Decrease and update quantity
                dbHelper.updateItem(item.getId(), item.getName(), item.getQuantity(), "");
                quantityLabel.setText("Qty: " + item.getQuantity());
                notifyDataSetChanged();

                // Show a warning toast if stock is low based on saved threshold
                int threshold = Prefs.getThreshold(context);
                if (newQty < threshold) {
                    Toast.makeText(context, "Alert: Stock is low (threshold: " + threshold + ")!", Toast.LENGTH_SHORT).show();

                    // Uncomment below if real SMS functionality is enabled (requires permission)
                    /*
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(Prefs.getPhone(context), null,
                        "Inventory Alert: " + item.getName() + " is below " + threshold + " in stock!",
                        null, null);
                    */
                }
            }
        });

        return convertView; // Return the view to be displayed
    }
}