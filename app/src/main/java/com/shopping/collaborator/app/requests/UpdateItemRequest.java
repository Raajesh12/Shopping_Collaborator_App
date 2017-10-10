package com.shopping.collaborator.app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class UpdateItemRequest {
    public UpdateItemRequest(String itemName, double estimate, double actual, boolean done){
        this.itemName = itemName;
        this.estimate = estimate;
        this.actual = actual;
        this.done = done;
    }

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("estimate")
    private double estimate;

    @SerializedName("actual")
    private double actual;

    @SerializedName("done")
    private boolean done;


}
