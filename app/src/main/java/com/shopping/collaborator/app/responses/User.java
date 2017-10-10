package com.shopping.collaborator.app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishn on 8/16/2017.
 */

public class User {
    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("uid")
    private long uid;

    public String getFirstName(){return firstName;}

    public String getLastName(){return lastName;}

    public String getEmail(){return email;}

    public long getUid(){return uid;}
}
