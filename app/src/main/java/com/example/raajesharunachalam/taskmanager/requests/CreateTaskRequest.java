package com.example.raajesharunachalam.taskmanager.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class CreateTaskRequest {
    public CreateTaskRequest(int uid, int gid, String taskDescription){
        this.uid = uid;
        this.gid = gid;
        this.taskDescription = taskDescription;
    }
    @SerializedName("uid")
    private int uid;

    @SerializedName("gid")
    private int gid;

    @SerializedName("task_description")
    private String taskDescription;
}
