package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class ItemListResponse {
    @SerializedName("items")
    private Item[] items;

    public Item[] getItems(){
        return items;
    }
}
