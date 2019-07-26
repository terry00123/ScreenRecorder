package com.orpheusdroid.screenrecorder.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.orpheusdroid.screenrecorder.Config;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.interfaces.IRecordingState;
import com.orpheusdroid.screenrecorder.utils.Log;
import com.orpheusdroid.screenrecorder.views.FloatingView;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

public class FloatingControlService extends Service implements View.OnClickListener, FloatingView.FloatMoveCallback {
    private WindowManager mWindowManager;
    private DisplayMetrics mDisplayMetrics;
    private FloatingView mView;
    private boolean isPaused;

    private Chronometer chronometer;
    private View recordingView;
    private View idleView;
    private long elapsedTime;
    private ImageView recordPauseBtn;

    private int startX;
    private int startY;
    private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT;
    private int mWidth = WindowManager.LayoutParams.WRAP_CONTENT;
    private boolean isLandScape;
    private boolean isLandScapeInit;
    private WindowManager.LayoutParams mLayoutParams;

    public FloatingControlService() {
    }

    private IBinder binder = new ServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Const.TAG, "Floating control started");
        this.mView = (FloatingView) LayoutInflater.from(this).inflate(R.layout.float_view_content, null);

        initView();

        this.startX = ((int) getResources().getDisplayMetrics().density) * 28;
        this.startY = ((int) getResources().getDisplayMetrics().density) * 106;


        this.mWindowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        this.mDisplayMetrics = new DisplayMetrics();
        this.mWindowManager.getDefaultDisplay().getRealMetrics(this.mDisplayMetrics);

        initLayoutParams();

        mWindowManager.addView(mView, mLayoutParams);
        return START_STICKY;
    }

    private void initView(){
        chronometer = mView.findViewById(R.id.tv_time);
        ImageView recordStartBtn = mView.findViewById(R.id.iv_start);
        recordPauseBtn = mView.findViewById(R.id.iv_pause_resume);
        ImageView recordStopBtn = mView.findViewById(R.id.iv_stop);
        ImageView recordCancelBtn = mView.findViewById(R.id.iv_cancel);

        idleView = mView.findViewById(R.id.parent_idle);
        recordingView = mView.findViewById(R.id.parent_recording);

        recordingView.setVisibility(View.GONE);

        recordStartBtn.setOnClickListener(this);
        recordPauseBtn.setOnClickListener(this);
        recordStopBtn.setOnClickListener(this);
        recordCancelBtn.setOnClickListener(this);
    }

    private void initLayoutParams() {
        Log.d("FloatWindowisLandScape init", " init" + isLandscape());
        this.isLandScape = isLandscape();
        if (this.isLandScape) {
            this.isLandScapeInit = true;
        }

        this.mLayoutParams = new WindowManager.LayoutParams();
        this.mLayoutParams.flags = 262184;
        this.mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

        mView.setCallback(this);

        this.mLayoutParams.setTitle("Screenrecorder");

        Log.d(Const.TAG, "mLayoutParams.height: " + this.mLayoutParams.height + "mLayoutParams.width: " + this.mLayoutParams.width);
        if (this.mHeight != WindowManager.LayoutParams.WRAP_CONTENT) {
            this.mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        if (this.mWidth != WindowManager.LayoutParams.WRAP_CONTENT) {
            this.mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        }

        this.mLayoutParams.gravity = Gravity.TOP | Gravity.START;

        this.mLayoutParams.format = PixelFormat.RGBA_8888;

        if (Build.VERSION.SDK_INT >= 26) {
            this.mLayoutParams.type = TYPE_APPLICATION_OVERLAY;
        } else {
            this.mLayoutParams.type = TYPE_SYSTEM_ALERT;
        }

        if (isLandscape()) {
            this.mLayoutParams.y = (int) (((float) this.mDisplayMetrics.heightPixels) - (((float) getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin)) + pxFromDp(this, 51.0f)));
            this.mLayoutParams.x = (int) (((float) this.mDisplayMetrics.widthPixels) - (((float) getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin)) + pxFromDp(this, 183.0f)));
            Log.d("FloatWindowisLandscape", "mLayoutParams.x: " + this.mLayoutParams.x + " " + "mLayoutParams.y: " + this.mLayoutParams.y);
            return;
        }
        this.mLayoutParams.x = (int) (((float) this.mDisplayMetrics.widthPixels) - pxFromDp(this, 192.0f));
        this.mLayoutParams.y = this.mDisplayMetrics.heightPixels - (this.mDisplayMetrics.heightPixels / 3);
    }

    private static float pxFromDp(Context context, float dp) {
        Log.d(Const.TAG, context.getResources().getDisplayMetrics().density + " ");
        return context.getResources().getDisplayMetrics().density * dp;
    }

    @Override
    public void onDestroy() {
        if (mView != null) mWindowManager.removeView(mView);
        Log.d(Const.TAG, "Unbinding successful!");
        super.onDestroy();
    }

    //Return ServiceBinder instance on successful binding
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(Const.TAG, "Binding successful!");
        return binder;
    }

    //Stop the service once the service is unbinded from recording service
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(Const.TAG, "Unbinding and stopping service");
        stopSelf();
        return super.onUnbind(intent);
    }

    private boolean isLandscape(){
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_start:
                startService(new Intent(
                        this, RecordingService.class)
                        .setAction(Const.SCREEN_RECORDING_START)
                );
                break;
            case R.id.iv_pause_resume:
                Intent pause_Resume_Intent = new Intent(this, RecordingService.class);
                if (isPaused)
                    pause_Resume_Intent.setAction(Const.SCREEN_RECORDING_RESUME);
                else
                    pause_Resume_Intent.setAction(Const.SCREEN_RECORDING_PAUSE);
                startService(pause_Resume_Intent);
                break;
            case R.id.iv_stop:
                startService(new Intent(
                        this, RecordingService.class)
                        .setAction(Const.SCREEN_RECORDING_STOP)
                );
                break;
            case R.id.iv_cancel:
                startService(new Intent(
                        this, RecordingService.class)
                        .setAction(Const.SCREEN_RECORDING_STOP)
                );
                break;
        }
    }

    @Override
    public void onActionDown(MotionEvent motionEvent) {

    }

    @Override
    public void onActionMove(MotionEvent motionEvent, float offsetX, float offsetY) {
        mLayoutParams.x = (int) offsetX;
        mLayoutParams.y = (int) offsetY;
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }

    @Override
    public void onActionUp(MotionEvent motionEvent, boolean z) {

    }

    public void onRecordingStarted() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setFormat("%s");
        chronometer.start();

        idleView.setVisibility(View.GONE);
        recordingView.setVisibility(View.VISIBLE);

        Log.d(Const.TAG, "Recording start handler");
    }

    public void onRecordingPaused() {
        elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        chronometer.stop();
        isPaused = true;
        recordPauseBtn.setImageResource(R.drawable.ic_record);
        Log.d(Const.TAG, "Recording pause handler");
    }

    public void onRecordingResumed() {
        chronometer.setBase(SystemClock.elapsedRealtime() - elapsedTime);
        chronometer.start();
        isPaused = false;
        recordPauseBtn.setImageResource(R.drawable.ic_pause);
        Log.d(Const.TAG, "Recording resume handler");
    }

    public void onRecordingStopped() {
        Log.d(Const.TAG, "Recording stop handler");
    }

    /**
     * Binder class for binding to recording service
     */
    class ServiceBinder extends Binder {
        FloatingControlService getService() {
            return FloatingControlService.this;
        }
    }
}
