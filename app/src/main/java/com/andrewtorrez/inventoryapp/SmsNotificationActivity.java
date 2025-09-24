package com.andrewtorrez.inventoryapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// This activity handles sending SMS alerts for low inventory
public class SmsNotificationActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100; // Request code to identify SMS permission result
    private static final String PREFS = "sms_prefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_THRESH = "threshold";

    private Button sendButton;
    private EditText etPhone;
    private EditText etThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_notification);

        // Find inputs (if present in layout)
        etPhone = findViewById(R.id.etPhone);
        etThreshold = findViewById(R.id.etThreshold);

        // Load saved settings or defaults
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        String savedPhone = sp.getString(KEY_PHONE, "5554");
        int savedThreshold = sp.getInt(KEY_THRESH, 5);

        if (etPhone != null) etPhone.setText(savedPhone);
        if (etThreshold != null) etThreshold.setText(String.valueOf(savedThreshold));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back arrow
        }
        // If the info TextView exists in the layout, append current defaults for clarity
        TextView info = findViewById(R.id.tvSmsInfo);
        if (info != null) {
            info.append("\nUsing phone: " + savedPhone + ", threshold: " + savedThreshold);
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

    private void sendSms() {
        // Read current settings from inputs (fallback to defaults)
        String phoneNumber = "5554";
        int threshold = 5;

        if (etPhone != null) {
            String p = etPhone.getText().toString().trim();
            if (!p.isEmpty()) phoneNumber = p;
        }
        if (etThreshold != null) {
            try {
                int t = Integer.parseInt(etThreshold.getText().toString().trim());
                if (t > 0) threshold = t;
            } catch (NumberFormatException ignored) {}
        }

        // Persist settings
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        sp.edit().putString(KEY_PHONE, phoneNumber).putInt(KEY_THRESH, threshold).apply();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<InventoryItem> items = dbHelper.getAllItems();

        int lowCount = 0;
        StringBuilder messageBuilder = new StringBuilder();
        for (InventoryItem item : items) {
            if (item.getQuantity() < threshold) {
                lowCount++;
                messageBuilder.append(item.getName())
                        .append(" is low (Qty: ")
                        .append(item.getQuantity())
                        .append(")\n");
            }
        }

        String message = messageBuilder.toString();

        if (!message.isEmpty()) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this,
                        "SMS sent to " + phoneNumber + " (" + lowCount + " items below " + threshold + ")",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
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