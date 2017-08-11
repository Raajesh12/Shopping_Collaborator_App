package com.example.raajesharunachalam.taskmanager.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class UpdateGroupRequest {
    public UpdateGroupRequest(String groupName) {
        this.groupName = groupName;
    }

    @SerializedName("group_name")
    private String groupName;
}
