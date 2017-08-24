package com.example.raajesharunachalam.taskmanager;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.raajesharunachalam.taskmanager.endpoints.UserEndpoints;
import com.example.raajesharunachalam.taskmanager.responses.LastModifiedResponse;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupService extends IntentService {
    private static final int MAX_ITERATIONS = 20;

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
            Log.d("GroupService", "New Iteration Of Loop");
            Call<LastModifiedResponse> call = UserEndpoints.userEndpoints.getUserLastModified(uid);
            try {
                Response<LastModifiedResponse> response = call.execute();
                LastModifiedResponse responseJson = response.body();
                int year = responseJson.getYear();
                int month = responseJson.getMonth() - 1;
                int day = responseJson.getDay();
                int hour = responseJson.getHour();
                int minute = responseJson.getMinute();
                int second = responseJson.getSecond();

                Calendar lastModified = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                lastModified.set(year, month, day, hour, minute, second);
                if(lastModified.after(lastRefreshed)) {
                    notRecentlyUpdated = false;
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(GroupsActivity.GroupResponseReceiver.ACTION_RESP);
                    broadcastIntent.putExtra(IntentKeys.SHOULD_REFRESH, true);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    sendBroadcast(broadcastIntent);
                }
            } catch (IOException e){}

            count++;
            if(count > MAX_ITERATIONS) {
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
