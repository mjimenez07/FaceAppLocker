package com.example.developer.facetracker;


import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AppTrackerService extends Service {
//    public String whatsapp = 'com.whatsapp';
    ActivityManager mActivityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("Service to track apps", "Created");

        Timer timer = new Timer();
        TimerTask refresher = new TimerTask() {
            public void run() {
                List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals("com.hipchat")) {
                    Log.v("Calling", "Recognition locker");
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
