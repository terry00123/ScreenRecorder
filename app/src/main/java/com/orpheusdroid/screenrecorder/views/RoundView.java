package com.orpheusdroid.screenrecorder.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.orpheusdroid.screenrecorder.R;

public class RoundView extends View {
    private static final int DURATION = 150;
    private int mMaxWidth;
    private int mMinWidth;
    private Paint mPaint;
    private int mPaintColor;
    private int mRadius;
    private RectF mRectF;
    private float mScaleFraction;

    public RoundView(Context context) {
        super(context);
        this.mScaleFraction = 0.0f;
    }

    public RoundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mScaleFraction = 0.0f;
        this.mPaintColor = getResources().getColor(android.R.color.darker_gray);
        this.mRadius = getResources().getDimensionPixelOffset(R.dimen.floating_controls_view_radius);
        this.mMinWidth = getResources().getDimensionPixelOffset(R.dimen.floating_controls_min_width);
        this.mMaxWidth = getResources().getDimensionPixelOffset(R.dimen.floating_controls_max_width);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mPaintColor);
        this.mPaint.setStyle(Style.FILL);
        this.mRectF = new RectF();
    }

    public RoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mScaleFraction = 0.0f;
    }

    public RoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mScaleFraction = 0.0f;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int leftOffset = (this.mMaxWidth - this.mMinWidth) / 2;
        this.mRectF.left = ((float) leftOffset) * this.mScaleFraction;
        this.mRectF.right = ((float) getWidth()) - this.mRectF.left;
        this.mRectF.top = 0.0f;
        this.mRectF.bottom = (float) getHeight();
        canvas.drawRoundRect(this.mRectF, (float) this.mRadius, (float) this.mRadius, this.mPaint);
    }

    public void collapse() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "scaleFraction", new float[]{0.0f, 1.0f});
        objectAnimator.setDuration(150);
        objectAnimator.start();
    }

    public void expand() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "scaleFraction", new float[]{1.0f, 0.0f});
        objectAnimator.setDuration(150);
        objectAnimator.start();
    }

    public void expand(boolean animated) {
        if (animated) {
            expand();
            return;
        }
        this.mScaleFraction = 0.0f;
        invalidate();
    }

    public float getScaleFraction() {
        return this.mScaleFraction;
    }

    public void setScaleFraction(float scaleFraction) {
        this.mScaleFraction = scaleFraction;
        invalidate();
    }
}
