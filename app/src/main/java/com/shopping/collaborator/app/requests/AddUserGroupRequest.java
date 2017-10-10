package com.shopping.collaborator.app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class AddUserGroupRequest {
    public AddUserGroupRequest(long gid, String userEmail) {
        this.gid = gid;
        this.userEmail = userEmail;
    }

    @SerializedName("gid")
    private long gid;

    @SerializedName("user_email")
    private String userEmail;
}
