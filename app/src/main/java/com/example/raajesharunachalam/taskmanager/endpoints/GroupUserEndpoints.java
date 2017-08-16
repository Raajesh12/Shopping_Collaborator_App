package com.example.raajesharunachalam.taskmanager.endpoints;

import com.example.raajesharunachalam.taskmanager.requests.AddUserGroupRequest;
import com.example.raajesharunachalam.taskmanager.responses.ErrorResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public interface GroupUserEndpoints {
    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @POST("/group_user")
    Call<ErrorResponse> addUserToGroup(@Body AddUserGroupRequest request);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @DELETE("/group_user")
    Call<Void> deleteUserFromGroup(@Query("gid") long gid, @Query("uid") long uid);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://taskmanager.host")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    GroupUserEndpoints groupUserEndpoints = retrofit.create(GroupUserEndpoints.class);
}
