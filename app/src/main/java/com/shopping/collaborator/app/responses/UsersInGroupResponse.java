package com.shopping.collaborator.app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishn on 8/16/2017.
 */

public class UsersInGroupResponse {
    @SerializedName("users")
    private User[] users;

    public User[] getUsers(){
        return users;
    }
}
