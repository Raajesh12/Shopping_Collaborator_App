package com.shopping.collaborator.app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/23/17.
 */

public class LastModifiedResponse {
    @SerializedName("year")
    private int year;

    @SerializedName("month")
    private int month;

    @SerializedName("day")
    private int day;

    @SerializedName("hour")
    private int hour;

    @SerializedName("minute")
    private int minute;

    @SerializedName("second")
    private int second;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }
}
