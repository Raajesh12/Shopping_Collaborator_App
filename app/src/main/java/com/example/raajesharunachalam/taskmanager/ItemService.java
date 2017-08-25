package com.example.raajesharunachalam.taskmanager;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.raajesharunachalam.taskmanager.GroupsActivity;
import com.example.raajesharunachalam.taskmanager.IntentKeys;
import com.example.raajesharunachalam.taskmanager.endpoints.GroupEndpoints;
import com.example.raajesharunachalam.taskmanager.endpoints.UserEndpoints;
import com.example.raajesharunachalam.taskmanager.responses.LastModifiedResponse;

import java.io.IOException;
import java.security.acl.Group;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;


public class ItemService extends IntentService {

    public static final int MAX_ITERATIONS = 10;
    int count = 0;
    public ItemService() {
        super("Item Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean notRecentlyUpdated = true;
        long gid = intent.getLongExtra(IntentKeys.GID, 0L);
        Calendar lastRefreshed = (Calendar) intent.getSerializableExtra(IntentKeys.LAST_REFRESHED);
        while(notRecentlyUpdated) {
            Log.d("ItemService", "New Iteration Of Loop");
            Call<LastModifiedResponse> call = GroupEndpoints.groupEndpoints.getGroupLastModified(gid);
            try {
                Response<LastModifiedResponse> response = call.execute();
                if(response.code() == ResponseCodes.HTTP_OK) {
                    LastModifiedResponse responseJson = response.body();
                    int year = responseJson.getYear();
                    int month = responseJson.getMonth() - 1;
                    int day = responseJson.getDay();
                    int hour = responseJson.getHour();
                    int minute = responseJson.getMinute();
                    int second = responseJson.getSecond();

                    Calendar lastModified = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    lastModified.set(year, month, day, hour, minute, second);

                    if (lastModified.after(lastRefreshed)) {
                        notRecentlyUpdated = false;
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(ItemsActivity.ItemResponseReceiver.ACTION_RESP);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra(IntentKeys.SHOULD_REFRESH, true);
                        sendBroadcast(broadcastIntent);
                    }
                }
            } catch (IOException e){}
            count++;
            if(count >= MAX_ITERATIONS){
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ItemsActivity.ItemResponseReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(IntentKeys.SHOULD_REFRESH, false);
                sendBroadcast(broadcastIntent);
                return;
            }

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e){}
        }
    }
}
