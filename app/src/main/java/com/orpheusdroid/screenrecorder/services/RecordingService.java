package com.orpheusdroid.screenrecorder.services;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.orpheusdroid.screenrecorder.Config;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.interfaces.IRecordingState;
import com.orpheusdroid.screenrecorder.ui.MainActivity;
import com.orpheusdroid.screenrecorder.utils.ConfigHelper;
import com.orpheusdroid.screenrecorder.utils.Log;
import com.orpheusdroid.screenrecorder.utils.NotificationHelper;
import com.orpheusdroid.screenrecorder.utils.Resolution;
import com.orpheusdroid.screenrecorder.utils.ResolutionHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordingService extends Service {
    private FloatingControlService floatingControlService;
    private IRecordingState recordingState;
    private MediaProjectionCallback mMediaProjectionCallback;

    private Config config;
    private ConfigHelper configHelper;
    private ResolutionHelper resolutionHelper;
    private Resolution resolution;

    private String SAVEPATH;
    private long startTime, elapsedTime = 0;

    private Intent data;
    private int result;

    private boolean isBound = false;
    private NotificationHelper notificationHelper;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaRecorder mMediaRecorder;

    private AudioManager mAudioManager;

    private boolean isRecording = false;

    public RecordingService() {
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Get the service instance
            FloatingControlService.ServiceBinder binder = (FloatingControlService.ServiceBinder) service;
            floatingControlService = binder.getService();
            Log.d(Const.TAG, "Floating service bound to recorder service");
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            floatingControlService = null;
            isBound = false;
            Log.d(Const.TAG, "Floating service unbound to recorder service");
        }
    };

    //Start service as a foreground service. We dont want the service to be killed in case of low memory
    private void startNotificationForeGround(Notification notification, int ID) {
        startForeground(ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Const.TAG, "Starting Service. isBound: " + isBound + ", isRecording: " + isRecording);

        if (intent != null) {
            data = intent.getParcelableExtra(Const.RECORDER_INTENT_DATA);
            result = intent.getIntExtra(Const.RECORDER_INTENT_RESULT, Activity.RESULT_OK);
        }

        if (config == null){
            config = Config.getInstance(this);
        }

        if (notificationHelper == null){
            notificationHelper = NotificationHelper.getInstance(this);
        }

        if(resolutionHelper == null) {
            resolutionHelper = ResolutionHelper.getInstance(this);
        }

        if(configHelper == null) {
            configHelper = ConfigHelper.getInstance(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationHelper.createNotificationChannels();

        if (intent != null && intent.getAction() != null){
            switch (intent.getAction()){
                //case Const.SCREEN_RECORDING_SHOW_FLOATING_ACTIONS:
                    //break;
                case Const.SCREEN_RECORDING_START:
                    SAVEPATH = configHelper.getFileSaveName(config.getSaveLocation());
                    notificationHelper.setSAVEPATH(SAVEPATH);
                    if (!isRecording) {
                        startRecording();
                        isRecording = true;
                    }
                    break;
                case Const.SCREEN_RECORDING_STOP:
                    stopRecording();
                    break;
                case Const.SCREEN_RECORDING_PAUSE:
                    pauseRecording();
                    break;
                case Const.SCREEN_RECORDING_RESUME:
                    resumeRecording();
                    break;
            }
        }
        return START_STICKY;
    }

    private void startRecording(){

        config.buildConfig();
        resolution = resolutionHelper.getWidthHeight();

        mMediaRecorder = new MediaRecorder();

        if (config.isFloatingControls()){
            if (!isBound) {
                showFloatingControls();
            }
        }

        mMediaRecorder.setOnErrorListener((mr, what, extra) -> {
            android.util.Log.e(Const.TAG, "Screencam Error: " + what + ", Extra: " + extra);
            Toast.makeText(this, R.string.recording_failed_toast, Toast.LENGTH_SHORT).show();
            destroyMediaProjection();
        });

        mMediaRecorder.setOnInfoListener((mr, what, extra) -> {
            android.util.Log.d(Const.TAG, "Screencam Info: " + what + ", Extra: " + extra);
        });

        initRecorder();

        mMediaProjectionCallback = new MediaProjectionCallback();
        MediaProjectionManager mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        //Initialize MediaProjection using data received from Intent
        if (mProjectionManager != null) {
            mMediaProjection = mProjectionManager.getMediaProjection(result, data);
        } else {
            Log.d(Const.TAG, "Creating media projection failed");
            destroyMediaProjection();
            stopSelf();
        }
        mMediaProjection.registerCallback(mMediaProjectionCallback, null);

        mVirtualDisplay = createVirtualDisplay();

        if (floatingControlService != null)
            floatingControlService.onRecordingStarted();

        try {
            mMediaRecorder.start();
            showNotification();
        } catch (IllegalStateException ise){
            Log.d(Const.TAG, "188: Media recorder start failed");
            ise.printStackTrace();
            mMediaProjection.stop();
            stopSelf();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void pauseRecording(){
        mMediaRecorder.pause();
        //calculate total elapsed time until pause
        elapsedTime += (System.currentTimeMillis() - startTime);

        //Set Resume action to Notification and update the current notification
        Intent recordResumeIntent = new Intent(this, RecordingService.class);
        recordResumeIntent.setAction(Const.SCREEN_RECORDING_RESUME);
        PendingIntent precordResumeIntent = PendingIntent.getService(this, 0, recordResumeIntent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_record_white,
                getString(R.string.screen_recording_notification_action_resume), precordResumeIntent);
        notificationHelper.updateNotification(
                notificationHelper.createRecordingNotification(action)
                        .setUsesChronometer(false).build(),
                Const.SCREEN_RECORDER_NOTIFICATION_ID
        );
        Toast.makeText(this, R.string.screen_recording_paused_toast, Toast.LENGTH_SHORT).show();

        //Send a broadcast receiver to the plugin app to disable show touches since the recording is paused
        /*if (showTouches) {
            Intent TouchIntent = new Intent();
            TouchIntent.setAction("com.orpheusdroid.screenrecorder.DISABLETOUCH");
            TouchIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(TouchIntent);
        }*/
        if (floatingControlService != null)
            floatingControlService.onRecordingPaused();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void resumeRecording(){
        mMediaRecorder.resume();

        startTime = System.currentTimeMillis();

        Intent recordPauseIntent = new Intent(this, RecordingService.class);
        recordPauseIntent.setAction(Const.SCREEN_RECORDING_PAUSE);
        PendingIntent precordPauseIntent = PendingIntent.getService(this, 0, recordPauseIntent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_pause_white,
                getString(R.string.screen_recording_notification_action_pause), precordPauseIntent);
        notificationHelper.updateNotification(
                notificationHelper.createRecordingNotification(action).setUsesChronometer(true)
                .setWhen((System.currentTimeMillis() - elapsedTime)).build(), Const.SCREEN_RECORDER_NOTIFICATION_ID);
        Toast.makeText(this, R.string.screen_recording_resumed_toast, Toast.LENGTH_SHORT).show();


        if (floatingControlService != null)
            floatingControlService.onRecordingResumed();
    }

    private void stopRecording(){
        if (isBound) {
            unbindService(serviceConnection);
            android.util.Log.d(Const.TAG, "Unbinding connection service");
        }

        if (config.isFloatingControls())
            floatingControlService.onRecordingStopped();
        stopScreenSharing();
    }

    private VirtualDisplay createVirtualDisplay() {
        Log.d(Const.TAG, resolution.toString());
        return mMediaProjection.createVirtualDisplay("MainActivity",
                resolution.getWIDTH(), resolution.getHEIGHT(), resolution.getDPI(),
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    private void initRecorder(){
        boolean mustRecAudio = false;
        String audioBitRate = config.getAudioBitrate();
        String audioSamplingRate = config.getAudioSamplingRate();
        String audioChannel = config.getAudioChannel();
        String audioRecSource = config.getAudioSource();
        //String SAVEPATH = config.getSaveLocation();
        int FPS = Integer.valueOf(config.getFps());
        int BITRATE = Integer.valueOf(config.getVideoBitrate());

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        try {
            switch (audioRecSource) {
                case "1":
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mustRecAudio = true;
                    break;
                case "2":
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    mMediaRecorder.setAudioEncodingBitRate(Integer.parseInt(audioBitRate));
                    mMediaRecorder.setAudioSamplingRate(Integer.parseInt(audioSamplingRate));
                    mMediaRecorder.setAudioChannels(Integer.parseInt(audioChannel));
                    mustRecAudio = true;

                    android.util.Log.d(Const.TAG, "bit rate: " + audioBitRate + " sampling: " + audioSamplingRate + " channel" + audioChannel);
                    break;
                case "3":
                    mAudioManager.setParameters("screenRecordAudioSource=8");
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.REMOTE_SUBMIX);
                    mMediaRecorder.setAudioEncodingBitRate(Integer.parseInt(audioBitRate));
                    mMediaRecorder.setAudioSamplingRate(Integer.parseInt(audioSamplingRate));
                    mMediaRecorder.setAudioChannels(Integer.parseInt(audioChannel));
                    mustRecAudio = true;
                    break;
            }
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(SAVEPATH);
            mMediaRecorder.setVideoSize(resolution.getWIDTH(), resolution.getHEIGHT());
            mMediaRecorder.setVideoEncoder(configHelper.getBestVideoEncoder(resolution.getWIDTH(), resolution.getHEIGHT()));
            mMediaRecorder.setMaxFileSize(configHelper.getFreeSpaceInBytes(config.getSaveLocation()));
            if (mustRecAudio)
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setVideoEncodingBitRate(BITRATE);
            mMediaRecorder.setVideoFrameRate(FPS);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyMediaProjection(){
        mAudioManager.setParameters("screenRecordAudioSource=0");
        try {
            mMediaRecorder.stop();
            indexFile();
            android.util.Log.i(Const.TAG, "MediaProjection Stopped");
        } catch (RuntimeException e) {
            Log.d(Const.TAG, "Fatal exception! Destroying media projection failed." + "\n" + e.getMessage());
            if (new File(config.getSaveLocation()).delete())
                Log.d(Const.TAG, "Corrupted file delete successful");
            Toast.makeText(this, getString(R.string.fatal_exception_message), Toast.LENGTH_SHORT).show();
        } finally {
            mMediaRecorder.reset();
            mVirtualDisplay.release();
            mMediaRecorder.release();
            if (mMediaProjection != null) {
                mMediaProjection.stop();
            }
            stopSelf();
        }
    }

    /* Its weird that android does not index the files immediately once its created and that causes
     * trouble for user in finding the video in gallery. Let's explicitly announce the file creation
     * to android and index it */
    private void indexFile() {
        //Create a new ArrayList and add the newly created video file path to it
        ArrayList<String> toBeScanned = new ArrayList<>();
        toBeScanned.add(SAVEPATH);
        String[] toBeScannedStr = new String[toBeScanned.size()];
        toBeScannedStr = toBeScanned.toArray(toBeScannedStr);

        //Request MediaScannerConnection to scan the new file and index it
        MediaScannerConnection.scanFile(getApplicationContext(), toBeScannedStr, null, (path, uri) -> {
            Log.d(Const.TAG, "SCAN COMPLETED: " + path + ", URI: " + uri);
            //Show toast on main thread
            Message message = mHandler.obtainMessage();
            message.sendToTarget();
            //stopSelf();
            notificationHelper.showShareNotification(uri);
        });
    }

    private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            Log.d(Const.TAG, "Virtual display is null. Screen sharing already stopped");
            return;
        }
        destroyMediaProjection();
    }

    private void showFloatingControls(){
        Intent floatingControlsIntent = new Intent(this, FloatingControlService.class);
        startService(floatingControlsIntent);
        bindService(floatingControlsIntent,
                serviceConnection, BIND_AUTO_CREATE);
    }

    private void showNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //startTime is to calculate elapsed recording time to update notification during pause/resume
            startTime = System.currentTimeMillis();
            Intent recordPauseIntent = new Intent(this, RecordingService.class);
            recordPauseIntent.setAction(Const.SCREEN_RECORDING_PAUSE);
            PendingIntent precordPauseIntent = PendingIntent.getService(this, 0, recordPauseIntent, 0);
            NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_pause_white,
                    getString(R.string.screen_recording_notification_action_pause), precordPauseIntent);

            //Start Notification as foreground
            startNotificationForeGround(notificationHelper.createRecordingNotification(action).build(), Const.SCREEN_RECORDER_NOTIFICATION_ID);
        } else
            startNotificationForeGround(notificationHelper.createRecordingNotification(null).build(), Const.SCREEN_RECORDER_NOTIFICATION_ID);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Const.TAG, "Recording service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            Toast.makeText(RecordingService.this, R.string.screen_recording_stopped_toast, Toast.LENGTH_SHORT).show();
        }
    };

    private void unregisterCallback() {
        mMediaProjection.unregisterCallback(mMediaProjectionCallback);
        Log.d(Const.TAG, "Recording: Unregistering callback");
        mMediaProjection = null;
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.d(Const.TAG, "Recording Stopped");
            unregisterCallback();
        }
    }
}
