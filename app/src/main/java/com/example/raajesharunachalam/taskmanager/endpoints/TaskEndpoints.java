package com.example.raajesharunachalam.taskmanager.endpoints;

import com.example.raajesharunachalam.taskmanager.requests.CreateTaskRequest;
import com.example.raajesharunachalam.taskmanager.requests.UpdateTaskRequest;
import com.example.raajesharunachalam.taskmanager.responses.GroupListResponse;
import com.example.raajesharunachalam.taskmanager.responses.TaskIDResponse;
import com.example.raajesharunachalam.taskmanager.responses.TaskListResponse;

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

public interface TaskEndpoints {
    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @GET("/tasks")
    Call<TaskListResponse> getTasks(@Query("gid") int gid);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @POST("/tasks")
    Call<TaskIDResponse> createTask(@Body CreateTaskRequest createTaskRequest);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @PUT("/tasks/{taskId}")
    Call<Void> updateTask(@Path("taskId") int taskId, @Body UpdateTaskRequest updateTaskRequest);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @DELETE("/tasks/{taskId}")
    Call<Void> deleteTask(@Path("taskId") int taskId);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://taskmanager.host")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    TaskEndpoints taskEndpoints = retrofit.create(TaskEndpoints.class);
}
