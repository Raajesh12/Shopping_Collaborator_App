package com.example.raajesharunachalam.taskmanager.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class UpdateItemRequest {
    public UpdateItemRequest(String itemName, double estimate, boolean done){
        this.itemName = itemName;
        this.estimate = estimate;
        this.done = done;
    }

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("estimate")
    private double estimate;

    @SerializedName("done")
    private boolean done;
}
