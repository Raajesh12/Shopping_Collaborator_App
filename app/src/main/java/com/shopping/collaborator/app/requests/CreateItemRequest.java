package com.shopping.collaborator.app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class CreateItemRequest {
    public CreateItemRequest(long uid, long gid, String itemName, double estimate){
        this.uid = uid;
        this.gid = gid;
        this.itemName = itemName;
        this.estimate = estimate;
    }
    @SerializedName("uid")
    private long uid;

    @SerializedName("gid")
    private long gid;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("estimate")
    private double estimate;
}
