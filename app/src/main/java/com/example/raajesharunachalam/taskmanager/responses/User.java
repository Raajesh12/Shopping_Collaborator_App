package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vishn on 8/16/2017.
 */

public class User {
    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("uid")
    private long uid;


    public String getFirstName(){return firstName;}

    public String getLastName(){return lastName;}

    public long getUid(){return uid;}
}
