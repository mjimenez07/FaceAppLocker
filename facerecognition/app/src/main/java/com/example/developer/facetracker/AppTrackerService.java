package com.example.developer.facetracker;


import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AppTrackerService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("Service to track apps", "Created");

        Timer timer = new Timer();
        TimerTask refresher = new TimerTask() {
            String topPackageName = null;
            Context context = getApplicationContext();
            UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*1000, time);
            public void run() {
                if (stats != null) {
                    SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
                    for (UsageStats usageStats : stats) {
                        runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (runningTask.isEmpty()) {
                       Log.v("service", "Nothing running");
                    }
                    topPackageName =  runningTask.get(runningTask.lastKey()).getPackageName();
                    Log.v("service", "Current app" + topPackageName);
                }
            };
        };
        //TIMER RUNS EVERY 1 SECOND
        timer.scheduleAtFixedRate(refresher, 1000,1000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("Service to track apps", "Destroyed");
    }
}
