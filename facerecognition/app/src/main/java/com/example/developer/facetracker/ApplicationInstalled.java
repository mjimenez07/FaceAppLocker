package com.example.developer.facetracker;


import android.content.pm.ApplicationInfo;

public class ApplicationInstalled {
    private ApplicationInfo appInfo;
    private boolean isActive = false;

    public ApplicationInstalled(ApplicationInfo appInfo, boolean isActive) {
        this.appInfo = appInfo;
        this.isActive = isActive;
    }

    public ApplicationInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(ApplicationInfo appInfo) {
        this.appInfo = appInfo;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
