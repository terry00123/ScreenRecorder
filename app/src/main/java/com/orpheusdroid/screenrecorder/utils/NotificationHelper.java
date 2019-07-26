package com.orpheusdroid.screenrecorder.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.orpheusdroid.screenrecorder.BuildConfig;
import com.orpheusdroid.screenrecorder.Config;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.services.RecordingService;
import com.orpheusdroid.screenrecorder.ui.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NotificationHelper {
    private static NotificationHelper notificationHelper;
    private Context mContext;
    private NotificationManager mNotificationManager;
    private Config config;

    private String SAVEPATH;

    public String getSAVEPATH() {
        return SAVEPATH;
    }

    public void setSAVEPATH(String SAVEPATH) {
        this.SAVEPATH = SAVEPATH;
    }

    public static NotificationHelper getInstance(Context context){
        if (notificationHelper == null){
            notificationHelper = new NotificationHelper(context);
        }
        return notificationHelper;
    }

    private NotificationHelper(Context context){
        this.mContext = context;
        config = Config.getInstance(context);
    }

    private NotificationHelper(){
    }

    private NotificationManager getManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    //Add notification channel for supporting Notification in Api 26 (Oreo)
    @TargetApi(26)
    public void createNotificationChannels() {
        List<NotificationChannel> notificationChannels = new ArrayList<>();
        NotificationChannel recordingNotificationChannel = new NotificationChannel(
                Const.RECORDING_NOTIFICATION_CHANNEL_ID,
                Const.RECORDING_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        recordingNotificationChannel.enableLights(true);
        recordingNotificationChannel.setLightColor(Color.RED);
        recordingNotificationChannel.setShowBadge(true);
        recordingNotificationChannel.enableVibration(true);
        recordingNotificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationChannels.add(recordingNotificationChannel);

        NotificationChannel shareNotificationChannel = new NotificationChannel(
                Const.SHARE_NOTIFICATION_CHANNEL_ID,
                Const.SHARE_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        shareNotificationChannel.enableLights(true);
        shareNotificationChannel.setLightColor(Color.RED);
        shareNotificationChannel.setShowBadge(true);
        shareNotificationChannel.enableVibration(true);
        shareNotificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationChannels.add(shareNotificationChannel);

        getManager().createNotificationChannels(notificationChannels);
    }

    public NotificationCompat.Builder createRecordingNotification(NotificationCompat.Action action) {
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                R.mipmap.ic_launcher_round);

        Intent recordStopIntent = new Intent(mContext, RecordingService.class);
        recordStopIntent.setAction(Const.SCREEN_RECORDING_STOP);
        PendingIntent precordStopIntent = PendingIntent.getService(mContext, 0, recordStopIntent, 0);

        Intent UIIntent = new Intent(mContext, MainActivity.class);
        PendingIntent notificationContentIntent = PendingIntent.getActivity(mContext, 0, UIIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, Const.RECORDING_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(mContext.getResources().getString(R.string.screen_recording_notification_title))
                .setTicker(mContext.getResources().getString(R.string.screen_recording_notification_title))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(
                Bitmap.createScaledBitmap(icon, 64, 64, false))
                .setLargeIcon(icon)
                .setOngoing(true)
                .setContentIntent(notificationContentIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(R.drawable.ic_stop, mContext.getResources().getString(R.string.screen_recording_notification_action_stop),
                        precordStopIntent);
        if (!config.isFloatingControls())
            notification.setUsesChronometer(true);

        if (action != null)
            notification.addAction(action);
        return notification;
    }

    //Update existing notification with its ID and new Notification data
    public void updateNotification(Notification notification, int ID) {
        getManager().notify(ID, notification);
    }

    public void showShareNotification(Uri videoUri) {
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                R.mipmap.ic_notification_big);

        /*Uri videoUri = FileProvider.getUriForFile(
                mContext, BuildConfig.APPLICATION_ID + ".provider",
                new File(SAVEPATH));*/

        Intent Shareintent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, videoUri)
                .setType("video/mp4");

        /*Intent editIntent = new Intent(this, EditVideoActivity.class);
        editIntent.putExtra(Const.VIDEO_EDIT_URI_KEY, SAVEPATH);
        PendingIntent editPendingIntent = PendingIntent.getActivity(this, 0, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/
        PendingIntent sharePendingIntent = PendingIntent.getActivity(mContext, 0, Intent.createChooser(
                Shareintent, mContext.getString(R.string.share_intent_title)), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, MainActivity.class).setAction(Const.SCREEN_RECORDER_VIDEOS_LIST_FRAGMENT_INTENT), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder shareNotification = new NotificationCompat.Builder(mContext, Const.SHARE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(mContext.getString(R.string.share_intent_notification_title))
                .setContentText(mContext.getString(R.string.share_intent_notification_content))
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .addAction(android.R.drawable.ic_menu_share, mContext.getString(R.string.share_intent_notification_action_text)
                        , sharePendingIntent);
                /*.addAction(android.R.drawable.ic_menu_edit, mContext.getString(R.string.edit_intent_notification_action_text)
                        , editPendingIntent);*/
        updateNotification(shareNotification.build(), Const.SCREEN_RECORDER_SHARE_NOTIFICATION_ID);
    }
}
