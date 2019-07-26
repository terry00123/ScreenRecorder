package com.orpheusdroid.screenrecorder.services;

import android.content.Context;

public class NotificationService {
    private static NotificationService notificationService;
    private Context mContext;

    public static NotificationService getInstance(Context mContext){
        if (notificationService == null)
            notificationService = new NotificationService(mContext);
        return notificationService;
    }

    private NotificationService(Context context){
    }
}
