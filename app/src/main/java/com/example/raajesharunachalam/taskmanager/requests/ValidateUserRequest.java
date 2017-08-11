package com.example.raajesharunachalam.taskmanager.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class ValidateUserRequest {
    public ValidateUserRequest(String email, String password){
        this.email = email;
        this.password = password;
    }

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;
}
