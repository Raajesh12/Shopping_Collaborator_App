package com.example.raajesharunachalam.taskmanager.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class CreateGroupRequest {

    public CreateGroupRequest(long uid, String groupName){
        this.uid = uid;
        this.groupName = groupName;
    }

    @SerializedName("uid")
    private long uid;

    @SerializedName("group_name")
    private String groupName;
}
