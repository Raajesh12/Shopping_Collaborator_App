package com.shopping.collaborator.app.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/22/17.
 */

public class ValidateCurrentUserRequest {
    public ValidateCurrentUserRequest(long uid, String email, String password){
        this.uid = uid;
        this.email = email;
        this.password = password;
    }

    @SerializedName("uid")
    private long uid;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;
}
