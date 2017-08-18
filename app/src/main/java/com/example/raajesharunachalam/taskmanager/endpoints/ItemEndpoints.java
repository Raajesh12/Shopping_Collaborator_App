package com.example.raajesharunachalam.taskmanager.endpoints;

import com.example.raajesharunachalam.taskmanager.requests.CreateItemRequest;
import com.example.raajesharunachalam.taskmanager.requests.UpdateItemRequest;
import com.example.raajesharunachalam.taskmanager.requests.UpdateUserRequest;
import com.example.raajesharunachalam.taskmanager.responses.ItemIDResponse;
import com.example.raajesharunachalam.taskmanager.responses.ItemListResponse;

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

public interface ItemEndpoints {
    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @GET("/items")
    Call<ItemListResponse> getItems(@Query("gid") long gid);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @POST("/items")
    Call<ItemIDResponse> createItem(@Body CreateItemRequest createItemRequest);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @PUT("/items/{itemId}")
    Call<Void> updateItem(@Path("itemId") long itemId, @Body UpdateItemRequest updateItemRequest);

    @Headers("Token: 5c8ab94e-3c95-40f9-863d-e31ae49e5d8d")
    @DELETE("/items/{itemId}")
    Call<Void> deleteItem(@Path("itemId") long itemId);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://taskmanager.host")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ItemEndpoints ITEM_ENDPOINTS = retrofit.create(ItemEndpoints.class);
}