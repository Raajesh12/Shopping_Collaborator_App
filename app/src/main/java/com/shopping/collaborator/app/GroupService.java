package com.shopping.collaborator.app;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.shopping.collaborator.app.endpoints.UserEndpoints;
import com.shopping.collaborator.app.responses.LastModifiedResponse;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class GroupService extends IntentService {
    private static final int MAX_ITERATIONS = 10;

    public GroupService() {
        super("GroupService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean notRecentlyUpdated = true;
        long uid = intent.getLongExtra(IntentKeys.UID, 0L);
        Calendar lastRefreshed = (Calendar) intent.getSerializableExtra(IntentKeys.LAST_REFRESHED);
        int count = 0;
        while(notRecentlyUpdated) {
            Call<LastModifiedResponse> call = UserEndpoints.userEndpoints.getUserLastModified(uid);
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
                        broadcastIntent.setAction(GroupsActivity.GroupResponseReceiver.ACTION_RESP);
                        broadcastIntent.putExtra(IntentKeys.SHOULD_REFRESH, true);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        sendBroadcast(broadcastIntent);
                    }
                }
            } catch (IOException e){}

            count++;
            if(count >= MAX_ITERATIONS) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(GroupsActivity.GroupResponseReceiver.ACTION_RESP);
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
