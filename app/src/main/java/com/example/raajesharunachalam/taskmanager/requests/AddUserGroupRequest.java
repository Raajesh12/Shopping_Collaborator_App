package com.example.raajesharunachalam.taskmanager.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class AddUserGroupRequest {
    public AddUserGroupRequest(int gid, String userEmail) {
        this.gid = gid;
        this.userEmail = userEmail;
    }

    @SerializedName("gid")
    private int gid;

    @SerializedName("user_email")
    private String userEmail;
}
