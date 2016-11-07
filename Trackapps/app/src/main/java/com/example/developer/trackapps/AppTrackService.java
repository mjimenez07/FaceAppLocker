package com.example.developer.trackapps;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by developer on 11/1/2016.
 */
public class AppTrackService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("Service to track apps", "Created");
        SharedPreferences sharedPref = getSharedPreferences("AppsToBeBlocked", MODE_PRIVATE);
        Log.v("sharedpref", sharedPref.getString("ListToTrack", null));
        Timer timer = new Timer();
        TimerTask refresher = new TimerTask() {
            public void run() {
                String topPackageName = null;
                Context context = getApplicationContext();
                UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*1000, time);

                if (stats != null) {
                    SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
                    for (UsageStats usageStats : stats) {
                        runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (runningTask.isEmpty()) {
                        Log.v("service", "Nothing running");
                    }
                    topPackageName =  runningTask.get(runningTask.lastKey()).getPackageName();
                    Log.v("service", "Current app " + topPackageName);
                }
            };
        };

        //TIMER RUNS EVERY 1 SECOND
        timer.scheduleAtFixedRate(refresher, 1000,1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
