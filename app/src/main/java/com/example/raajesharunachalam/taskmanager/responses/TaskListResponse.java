package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class TaskListResponse {
    @SerializedName("tasks")
    private Task[] tasks;

    public Task[] getTasks(){
        return tasks;
    }
}
