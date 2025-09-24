package com.andrewtorrez.inventoryapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public final class Prefs {
    private static final String PREFS = "sms_prefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_THRESH = "threshold";

    private Prefs() {}

    public static int getThreshold(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getInt(KEY_THRESH, 5); // default 5
    }

    public static void setThreshold(Context ctx, int value) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putInt(KEY_THRESH, value).apply();
    }

    public static String getPhone(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getString(KEY_PHONE, "5554"); // default emulator
    }

    public static void setPhone(Context ctx, String phone) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_PHONE, phone).apply();
    }
}