package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class Group {
    @SerializedName("gid")
    private int gid;

    @SerializedName("group_name")
    private String groupName;

    public int getGroupId(){
        return gid;
    }

    public String getGroupName(){
        return groupName;
    }
}
