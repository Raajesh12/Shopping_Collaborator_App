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
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public interface GroupEndpoints {
    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @GET("/groups")
    Call<GroupListResponse> getGroups(@Query("uid") int uid);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @POST("/groups/")
    Call<GIDResponse> createGroup(@Body CreateGroupRequest createGroupRequest);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @PUT("/groups/{gid}")
    Call<Void> updateGroup(@Path("gid") int gid, @Body UpdateGroupRequest updateGroupRequest);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @DELETE("/groups/{gid}")
    Call<Void> deleteGroup(@Path("gid") int gid);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://taskmanager.host")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    GroupEndpoints groupEndpoints = retrofit.create(GroupEndpoints.class);

}
