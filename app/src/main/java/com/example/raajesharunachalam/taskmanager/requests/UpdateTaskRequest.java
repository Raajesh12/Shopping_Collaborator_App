package com.example.raajesharunachalam.taskmanager.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class UpdateTaskRequest {
    public UpdateTaskRequest(String taskDescription){
        this.taskDescription = taskDescription;
    }
    @SerializedName("task_description")
    private String taskDescription;
}
