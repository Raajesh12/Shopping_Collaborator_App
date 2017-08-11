package com.example.raajesharunachalam.taskmanager.endpoints;

import com.example.raajesharunachalam.taskmanager.requests.CreateGroupRequest;
import com.example.raajesharunachalam.taskmanager.requests.UpdateGroupRequest;
import com.example.raajesharunachalam.taskmanager.requests.UpdateUserRequest;
import com.example.raajesharunachalam.taskmanager.responses.GIDResponse;
import com.example.raajesharunachalam.taskmanager.responses.GroupListResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public interface GroupEndpoints {
    @GET("/groups")
    Call<GroupListResponse> getGroups(@Query("uid") int uid);

    @POST("/groups/")
    Call<GIDResponse> createGroup(@Body CreateGroupRequest createGroupRequest);

    @PUT("/groups/{gid}")
    Call<Void> updateGroup(@Path("gid") int gid, @Body UpdateGroupRequest updateGroupRequest);

    @DELETE("/groups/{gid}")
    Call<Void> deleteGroup(@Path("gid") int gid);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://taskmanager.host")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    GroupEndpoints groupEndpoints = retrofit.create(GroupEndpoints.class);

}
