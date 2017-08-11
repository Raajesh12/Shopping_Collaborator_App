package com.example.raajesharunachalam.taskmanager.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class Task {
    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;
}
