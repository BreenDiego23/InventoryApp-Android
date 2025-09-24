package com.andrewtorrez.inventoryapp;

/**
 * Represents a model object for inventory items, holding properties such as ID, name, and quantity.
 */
public class InventoryItem {
    private int id;         // Unique ID for the item (used by the database)
    private String name;    // Name of the inventory item
    private int quantity;   // Quantity of the item

    /**
     * Constructor WITH ID: used when retrieving data from the database.
     * @param id the unique identifier of the item in the database
     * @param name the name of the inventory item
     * @param quantity the quantity of the item
     */
    public InventoryItem(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Constructor WITHOUT ID: used when creating a new item to insert into the database.
     * @param name the name of the inventory item
     * @param quantity the quantity of the item
     */
    public InventoryItem(String name, int quantity) {
        this.id = -1; // Placeholder ID; will be set by the database upon insertion
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Returns the unique database identifier of the item.
     * @return the item's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the inventory item.
     * @return the item's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current quantity of the item.
     * @return the item's quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Updates the quantity of the inventory item.
     * @param quantity the new quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}