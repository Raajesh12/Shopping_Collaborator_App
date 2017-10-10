package com.shopping.collaborator.app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class ItemIDResponse {
    @SerializedName("id")
    private int itemId;

    public int getItemId(){
        return itemId;
    }
}
