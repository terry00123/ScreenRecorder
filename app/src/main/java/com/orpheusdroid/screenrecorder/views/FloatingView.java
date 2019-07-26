package com.orpheusdroid.screenrecorder.views;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orpheusdroid.screenrecorder.interfaces.OrientationChangeListener;
import com.orpheusdroid.screenrecorder.utils.Log;

public class FloatingView extends FrameLayout {
    public boolean isLandScape = false;
    private FloatMoveCallback mCallBack;
    private float mDownX;
    private float mDownY;
    private int mInterceptX = 0;
    private int mInterceptY = 0;
    private OrientationChangeListener mListener;
    private int mScreenWidth;
    private int mStatusBarHeight;
    private int mTouchSlop = 8;

    public interface FloatMoveCallback {
        void onActionDown(MotionEvent motionEvent);

        void onActionMove(MotionEvent motionEvent, float f, float f2);

        void onActionUp(MotionEvent motionEvent, boolean z);
    }

    public void setListener(OrientationChangeListener listener) {
        this.mListener = listener;
    }

    public FloatingView(Context context) {
        super(context);
    }

    public FloatingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        this.mStatusBarHeight = getStatusBarHeight(context);
        Log.d("Float", "initial view");
    }

    public FloatingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCallback(FloatMoveCallback callback) {
        this.mCallBack = callback;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                this.mInterceptX = (int) ev.getX();
                this.mInterceptY = (int) ev.getY();
                this.mDownX = ev.getX();
                this.mDownY = ev.getY();
                return false;
            case 2:
                boolean isIntercept = Math.abs(ev.getX() - ((float) this.mInterceptX)) > ((float) this.mTouchSlop) || Math.abs(ev.getY() - ((float) this.mInterceptY)) > ((float) this.mTouchSlop);
                return isIntercept;
            default:
                return false;
        }
    }

    private int getStatusBarHeight(Context ctx) {
        int identifier = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return ctx.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                actionDown(event);
                break;
            case 1:
                actionUp(event);
                break;
            case 2:
                actionMove(event);
                break;
            case 4:
                actionOutSide(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void actionOutSide(MotionEvent event) {
    }

    private void actionUp(MotionEvent event) {
        if (this.mCallBack != null) {
            this.mCallBack.onActionUp(event, event.getRawX() <= ((float) this.mScreenWidth) / 2.0f);
        }
    }

    private void actionMove(MotionEvent event) {
        float rawX = event.getRawX();
        float rawY = event.getRawY() - ((float) this.mStatusBarHeight);
        if (this.mCallBack != null) {
            this.mCallBack.onActionMove(event, rawX - this.mDownX, rawY - this.mDownY);
        }
    }

    private void actionDown(MotionEvent event) {
        this.mDownX = event.getX();
        this.mDownY = event.getY();
        if (this.mCallBack != null) {
            this.mCallBack.onActionDown(event);
        }
    }

    protected void onConfigurationChanged(Configuration newConfig) {
        boolean isLandscape = false;
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2) {
            this.isLandScape = true;
            this.mListener.update(true);
        } else if (newConfig.orientation == 1) {
            this.isLandScape = false;
            this.mListener.update(false);
        }
        String str = "Float";
        StringBuilder append = new StringBuilder().append("onConfigurationChanged isLand=");
        if (newConfig.orientation != 2) {
            isLandscape = false;
        }
        Log.d(str, append.append(isLandscape).toString());
    }
}
