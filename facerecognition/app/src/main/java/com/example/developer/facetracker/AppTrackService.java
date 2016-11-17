package com.example.developer.facetracker;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.developer.facetracker.utility.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AppTrackService extends Service {
    private String mCurrentApp;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPref = getSharedPreferences("AppsToBeBlocked", MODE_PRIVATE);
        final String[] arrayToCheck = sharedPref.getString("ListToTrack", null).split(",");
        Timer timer = new Timer();
        TimerTask refresher = new TimerTask() {
            public void run() {
                String topPackageName = null;
                Context context = getApplicationContext();
                UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);

                if (stats != null) {
                    SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();

                    for (UsageStats usageStats : stats) {
                        runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                    }

                    if (runningTask.isEmpty()) {
                        Log.v("service", "Nothing running");
                    }

                    topPackageName = runningTask.get(runningTask.lastKey()).getPackageName();
                    Log.v("TrackApps", "Current app " + topPackageName);
                    if (!topPackageName.equalsIgnoreCase("com.example.developer.trackapps")) {
                        if (Arrays.asList(arrayToCheck).contains(topPackageName) && Constants.IS_RUNNING) {
                            mCurrentApp = topPackageName;
                            Intent intent = new Intent(context, FaceRecognitionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }

                        if (mCurrentApp != null && !topPackageName.equalsIgnoreCase(mCurrentApp)) {
                            Log.i("TrackApps", "Activando servicio");
                            Constants.IS_RUNNING = true;
                            mCurrentApp = null;
                        }
                    }
                }
            }
        };

        //TIMER RUNS EVERY 1 SECOND
        timer.scheduleAtFixedRate(refresher, 0, 1000);
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


    public static class EnableAppReceiver extends BroadcastReceiver {

        public EnableAppReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //set the variable to false
            Log.i("TrackApps", "Desabilitando servicio");
            Constants.IS_RUNNING = false;
        }
    }
}