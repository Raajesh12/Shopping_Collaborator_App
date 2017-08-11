package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class GIDResponse {
    @SerializedName("gid")
    private long gid;

    public long getGid(){
        return gid;
    }
}
