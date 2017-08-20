package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/20/17.
 */

public class TotalPriceResponse {

    @SerializedName("total")
    private String total;

    public String getTotal(){
        return total;
    }
}