package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class TaskIDResponse {
    @SerializedName("id")
    private int taskId;

    public int getTaskId(){
        return taskId;
    }
}
