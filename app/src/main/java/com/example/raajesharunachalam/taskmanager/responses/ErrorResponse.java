package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class ErrorResponse {
    @SerializedName("error")
    private String error;

    public String getError(){
        return error;
    }
}
