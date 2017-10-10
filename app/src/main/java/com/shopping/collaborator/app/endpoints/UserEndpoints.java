package com.shopping.collaborator.app.endpoints;

import com.shopping.collaborator.app.requests.CreateUserRequest;
import com.shopping.collaborator.app.requests.UpdateUserRequest;
import com.shopping.collaborator.app.requests.ValidateCurrentUserRequest;
import com.shopping.collaborator.app.requests.ValidateUserRequest;
import com.shopping.collaborator.app.responses.LastModifiedResponse;
import com.shopping.collaborator.app.responses.UIDResponse;
import com.shopping.collaborator.app.responses.UserInfoResponse;

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

public interface UserEndpoints {
    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @GET("/users/{uid}")
    Call<UserInfoResponse> getUserInfo(@Path("uid") long uid);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @POST("/users/")
    Call<UIDResponse> createUser(@Body CreateUserRequest createUserRequest);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @PUT("/users/{uid}")
    Call<Void> updateUser(@Path("uid") long uid, @Body UpdateUserRequest updateUserRequest);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @DELETE("/users/{uid}")
    Call<Void> deleteUser(@Path("uid") long uid);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @POST("/validate_user")
    Call<UIDResponse> validateUser(@Body ValidateUserRequest validateUserRequest);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @POST("/validate_current_user")
    Call<Void> validateCurrentUser(@Body ValidateCurrentUserRequest validateCurrentUserRequest);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @GET("/user_last_modified")
    Call<LastModifiedResponse> getUserLastModified(@Query("uid") long uid);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.taskmanager.host")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    UserEndpoints userEndpoints = retrofit.create(UserEndpoints.class);
}
