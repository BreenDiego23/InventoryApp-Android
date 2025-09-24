package com.andrewtorrez.inventoryapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// This activity handles sending SMS alerts for low inventory
public class SmsNotificationActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100; // Request code to identify SMS permission result
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_notification);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sendButton = findViewById(R.id.sendSmsButton);

        // When the user clicks the "Send SMS" button
        sendButton.setOnClickListener(view -> {
            // Check if SMS permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // If not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
            } else {
                // If already granted, send the SMS
                sendSms();
            }
        });
    }

    // This method checks for low stock and sends an SMS with the alert
    private void sendSms() {
        DatabaseHelper dbHelper = new DatabaseHelper(this); // Access the database
        List<InventoryItem> items = dbHelper.getAllItems(); // Get all inventory items

        StringBuilder messageBuilder = new StringBuilder(); // Build the message

        /**
         * Loop through each item in the inventory and check if its quantity is below the threshold (currently hardcoded as 5).
         * If so, add the item and its quantity to the SMS message.
         * This is important because it alerts the user about items that are low in stock,
         * allowing timely restocking and preventing stockouts.
         */
        for (InventoryItem item : items) {
            if (item.getQuantity() < 5) {
                messageBuilder.append(item.getName())
                        .append(" is low (Qty: ")
                        .append(item.getQuantity())
                        .append(")\n");
            }
        }

        String message = messageBuilder.toString(); // Final message to send

        // Only send if there's at least one low-stock item
        if (!message.isEmpty()) {
            try {
                String phoneNumber = "5554"; // Emulator's default test number
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null); // Send the SMS
                Toast.makeText(this, "Low stock SMS sent", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // If sending fails, show error message
                Toast.makeText(this, "SMS failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            // If no items are low in stock, let user know
            Toast.makeText(this, "No low stock items. No SMS sent.", Toast.LENGTH_LONG).show();
        }
    }

    // Callback for when the user responds to the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Always call super
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSms(); // Permission granted, send SMS
            } else {
                // Permission denied, continue without SMS feature
                Toast.makeText(this, "SMS permission denied. Notifications disabled.", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}