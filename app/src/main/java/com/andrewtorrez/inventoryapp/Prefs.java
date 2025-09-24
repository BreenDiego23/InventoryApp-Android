package com.andrewtorrez.inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Simple preferences helper for SMS settings (phone + low-stock threshold).
 */
public final class Prefs {
    private static final String PREFS_NAME   = "sms_prefs";
    private static final String KEY_PHONE    = "phone";
    private static final String KEY_THRESHOLD = "threshold";

    private Prefs() { /* no instances */ }

    public static String getPhone(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_PHONE, "5554"); // emulator default
    }

    public static void setPhone(Context ctx, String phone) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_PHONE, phone)
                .apply();
    }

    public static int getThreshold(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEY_THRESHOLD, 5); // default if not set
    }

    public static void setThreshold(Context ctx, int value) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_THRESHOLD, value)
                .apply();
    }
}