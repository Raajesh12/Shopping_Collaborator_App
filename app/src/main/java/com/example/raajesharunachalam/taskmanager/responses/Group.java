package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class Group {
    @SerializedName("gid")
    private long gid;

    @SerializedName("group_name")
    private String groupName;

    public long getGroupId(){
        return gid;
    }

    public String getGroupName(){
        return groupName;
    }
}
