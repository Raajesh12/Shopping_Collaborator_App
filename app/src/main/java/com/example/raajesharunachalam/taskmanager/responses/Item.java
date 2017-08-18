package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class Item {
    @SerializedName("item_id")
    private long itemId;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("estimate")
    private double estimate;

    public long getItemId() {return itemId;}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getItemName() {
        return itemName;
    }

    public double getEstimate(){
        return estimate;
    }
}
