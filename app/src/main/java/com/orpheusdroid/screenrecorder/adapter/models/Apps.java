package com.orpheusdroid.screenrecorder.adapter.models;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class Apps implements Comparable<Apps> {
    private String appName;
    private String packageName;
    private Drawable appIcon;
    private boolean isSelectedApp;

    public Apps(String appName, String packageName, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public boolean isSelectedApp() {
        return isSelectedApp;
    }

    public void setSelectedApp(boolean selectedApp) {
        isSelectedApp = selectedApp;
    }

    @Override
    public int compareTo(@NonNull Apps apps) {
        return appName.compareTo(apps.appName);
    }
}
