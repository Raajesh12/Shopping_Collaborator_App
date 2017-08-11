package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class UIDResponse {
    @SerializedName("uid")
    private long uid;

    public long getUid(){
        return uid;
    }
}
