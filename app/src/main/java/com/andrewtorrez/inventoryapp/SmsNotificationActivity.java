package com.andrewtorrez.inventoryapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText etPhone;
    private EditText etThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_notification);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sendButton = findViewById(R.id.sendSmsButton);
        etPhone = findViewById(R.id.etPhone);
        etThreshold = findViewById(R.id.etThreshold);

        // Prefill UI from saved preferences so values persist between visits
        if (etPhone != null) {
            etPhone.setText(Prefs.getPhone(this));
        }
        if (etThreshold != null) {
            etThreshold.setText(String.valueOf(Prefs.getThreshold(this)));
        }

        sendButton.setOnClickListener(view -> {
            // Persist current UI values to shared preferences
            if (etPhone != null && etPhone.getText() != null) {
                String phone = etPhone.getText().toString().trim();
                if (!phone.isEmpty()) {
                    Prefs.setPhone(this, phone);
                }
            }
            if (etThreshold != null && etThreshold.getText() != null) {
                String tStr = etThreshold.getText().toString().trim();
                try {
                    int t = Integer.parseInt(tStr);
                    if (t > 0) {
                        Prefs.setThreshold(this, t);
                    }
                } catch (NumberFormatException ignored) { /* keep previous threshold */ }
            }

            // Check permission and send
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
            } else {
                sendSms();
            }
        });
    }

    // This method checks for low stock and sends an SMS with the alert
    private void sendSms() {
        int threshold = Prefs.getThreshold(this);
        DatabaseHelper dbHelper = new DatabaseHelper(this); // Access the database
        List<InventoryItem> items = dbHelper.getAllItems(); // Get all inventory items

        StringBuilder messageBuilder = new StringBuilder(); // Build the message

        /**
         * Loop through each item in the inventory and check if its quantity is below the user-defined threshold.
         * If so, add the item and its quantity to the SMS message.
         * This is important because it alerts the user about items that are low in stock,
         * allowing timely restocking and preventing stockouts.
         */
        for (InventoryItem item : items) {
            if (item.getQuantity() < threshold) {
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
                String phoneNumber = Prefs.getPhone(this);
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

    @Override
    protected void onPause() {
        super.onPause();
        if (etPhone != null && etPhone.getText() != null) {
            String p = etPhone.getText().toString().trim();
            if (!p.isEmpty()) {
                Prefs.setPhone(this, p);
            }
        }
        if (etThreshold != null && etThreshold.getText() != null) {
            String tStr = etThreshold.getText().toString().trim();
            try {
                int t = Integer.parseInt(tStr);
                if (t > 0) {
                    Prefs.setThreshold(this, t);
                }
            } catch (NumberFormatException ignored) { /* keep previous value */ }
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