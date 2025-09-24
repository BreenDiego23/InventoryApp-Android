package com.andrewtorrez.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "inventoryApp.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ITEMS = "inventory";

    // Users table column names
    private static final String USER_ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    // Inventory table column names
    private static final String ITEM_ID = "id";
    private static final String ITEM_NAME = "name";
    private static final String ITEM_QUANTITY = "quantity";

    // Constructor to initialize the database
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USERNAME + " TEXT,"
                + PASSWORD + " TEXT" + ")";

        // SQL statement to create inventory table
        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ITEM_NAME + " TEXT,"
                + ITEM_QUANTITY + " INTEGER" + ")";

        // Execute SQL commands
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_INVENTORY_TABLE);
    }

    // Called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // Add a new item to the inventory table
    public void addItem(InventoryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, item.getName());
        values.put(ITEM_QUANTITY, item.getQuantity());
        db.insert(TABLE_ITEMS, null, values); // Insert new row
        db.close();
    }

    // Retrieve all items from the inventory table
    public List<InventoryItem> getAllItems() {
        List<InventoryItem> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);

        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ITEM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_NAME));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ITEM_QUANTITY));
                InventoryItem item = new InventoryItem(id, name, quantity);
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    // Delete an inventory item by ID
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, ITEM_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Update an existing inventory item by ID
    public void updateItem(int id, String name, int quantity, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        // You can ignore or extend with "category" if needed

        db.update("inventory", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * Inserts a new user record into the users table.
     * Stores the provided username and password credentials.
     *
     * @param username The username to be stored.
     * @param password The password to be stored.
     */
    public void addUser(String username, String password) {
        // Open a writable database to allow data insertion
        SQLiteDatabase db = this.getWritableDatabase();
        // Prepare the values to be inserted
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);
        // Insert the new user record into the users table
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    /**
     * Validates login credentials by checking if the username and password
     * exist in the users table.
     *
     * @param username The username to check.
     * @param password The password to check.
     * @return true if credentials are valid, false otherwise.
     */
    public boolean checkUser(String username, String password) {
        // Open a readable database for querying
        SQLiteDatabase db = this.getReadableDatabase();
        // Execute a query to check for matching username and password
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + USERNAME + "=? AND " + PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        // Check if a matching row exists (i.e., valid credentials)
        boolean result = cursor.moveToFirst(); // True if a match is found
        // Close resources to avoid memory leaks
        cursor.close();
        db.close();
        return result;
    }
}