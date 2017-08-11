package com.example.raajesharunachalam.taskmanager.endpoints;

import com.example.raajesharunachalam.taskmanager.requests.CreateUserRequest;
import com.example.raajesharunachalam.taskmanager.requests.UpdateUserRequest;
import com.example.raajesharunachalam.taskmanager.requests.ValidateUserRequest;
import com.example.raajesharunachalam.taskmanager.responses.UIDResponse;
import com.example.raajesharunachalam.taskmanager.responses.UserInfoResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public interface UserEndpoints {
    @GET("/users/{uid}")
    Call<UserInfoResponse> getUserInfo(@Path("uid") int uid);

    @POST("/users/")
    Call<UIDResponse> createUser(@Body CreateUserRequest createUserRequest);

    @PUT("/users/{uid}")
    Call<Void> updateUser(@Path("uid") int uid, @Body UpdateUserRequest updateUserRequest);

    @DELETE("/users/{uid}")
    Call<Void> deleteUser(@Path("uid") int uid);

    @POST("/validate_user")
    Call<Void> validateUser(@Body ValidateUserRequest validateUserRequest);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://taskmanager.host")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    UserEndpoints userEndpoints = retrofit.create(UserEndpoints.class);
}
