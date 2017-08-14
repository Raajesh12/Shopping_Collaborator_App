package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class Task {
    @SerializedName("task_id")
    private long taskId;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("task_description")
    private String taskDescription;

    public long getTaskId() {return taskId;}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }
}
