package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/20/17.
 */

public class ItemsCompletedResponse {
    @SerializedName("items_bought")
    private long itemsBought;

    @SerializedName("total_items")
    private long totalItems;

    public long getItemsBought(){
        return itemsBought;
    }

    public long getTotalItems(){
        return totalItems;
    }

    public String getItemsString(){
        StringBuilder builder = new StringBuilder();
        builder.append(itemsBought);
        builder.append("/");
        builder.append(totalItems);
        return builder.toString();
    }

}
