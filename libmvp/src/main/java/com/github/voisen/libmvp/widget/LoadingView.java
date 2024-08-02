package com.github.voisen.libmvp.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class LoadingView extends View implements ValueAnimator.AnimatorUpdateListener {

    private final String TAG = "LoadingView";
    private final RectF mOvalRect = new RectF();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mStrokeWidth = 4;
    private float mStartAngleValue1 = 0;
    private float mStartAngleValue2 = 0;
    private float mProgress = -1;
    private boolean mDarkMode = false;

    private final ValueAnimator mValueAnimator1 = ValueAnimator.ofFloat(0, 360);
    private final ValueAnimator mValueAnimator2 = ValueAnimator.ofFloat(360, 0);

    private SweepGradient mSweepGradient1;
    private SweepGradient mSweepGradient2;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mValueAnimator1.addUpdateListener(this);
        mValueAnimator1.setDuration(1300);
        mValueAnimator1.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator1.setInterpolator(new LinearInterpolator());

        mValueAnimator2.addUpdateListener(this);
        mValueAnimator2.setDuration(1500);
        mValueAnimator2.setInterpolator(new LinearInterpolator());
        mValueAnimator2.setRepeatCount(ValueAnimator.INFINITE);

        mStrokeWidth = AutoSizeUtils.dp2px(context, mStrokeWidth);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    public void setProgress(float progress){
        mProgress = Math.min(Math.max(progress, -1), 1.0f);
        if (mProgress < 0){
            startAnimator();
        }else{
            pauseAnimator();
            postInvalidate();
        }
    }

    private void startAnimator() {
        mValueAnimator1.start();
        mValueAnimator2.start();
    }

    private void pauseAnimator() {
        mValueAnimator1.pause();
        mValueAnimator2.pause();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mProgress < 0){
            startAnimator();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pauseAnimator();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int size = Math.min(getWidth()-mStrokeWidth*2, getHeight()-mStrokeWidth*2);
        mOvalRect.top = (getHeight()-size) >> 1;
        mOvalRect.left = (getWidth()-size) >> 1;
        mOvalRect.right = mOvalRect.left + size;
        mOvalRect.bottom = mOvalRect.top + size;
        setDarkMode(mDarkMode);
    }

    public void setDarkMode(boolean darkMode) {
        mDarkMode = darkMode;
        if(mDarkMode){
            mSweepGradient1 = new SweepGradient(getWidth() >> 1, getHeight() >> 1, Color.TRANSPARENT, Color.parseColor("#55ffffff"));
            mSweepGradient2 = new SweepGradient(getWidth() >> 1, getHeight() >> 1, Color.parseColor("#55ffffff"), Color.TRANSPARENT);
        }else{
            mSweepGradient1 = new SweepGradient(getWidth() >> 1, getHeight() >> 1, Color.TRANSPARENT, Color.parseColor("#55000000"));
            mSweepGradient2 = new SweepGradient(getWidth() >> 1, getHeight() >> 1, Color.parseColor("#55000000"), Color.TRANSPARENT);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mProgress < 0){
            mPaint.setColor(Color.LTGRAY);
            mPaint.setStyle(Paint.Style.STROKE);

            mPaint.setShader(mSweepGradient1);
            canvas.save();
            canvas.rotate(mStartAngleValue1, getWidth()>>1, getHeight()>>1);
            canvas.drawArc(mOvalRect, 0, 360f, false, mPaint);
            canvas.restore();

            canvas.save();
            canvas.rotate(mStartAngleValue2, getWidth()>>1, getHeight()>>1);
            mPaint.setShader(mSweepGradient2);
            canvas.drawArc(mOvalRect, 0, 360f, false, mPaint);
            canvas.restore();
        }else{
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setShader(null);
            if (mDarkMode){
                mPaint.setColor(Color.GRAY);
            }else{
                mPaint.setColor(Color.LTGRAY);
            }
            canvas.drawArc(mOvalRect, 0, 360f, false, mPaint);
            if (mDarkMode){
                mPaint.setColor(Color.WHITE);
            }else{
                mPaint.setColor(Color.GRAY);
            }
            canvas.drawArc(mOvalRect, -90, 360f * mProgress, false, mPaint);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (animation == mValueAnimator1){
            mStartAngleValue1 = (float) animation.getAnimatedValue();
        }else{
            mStartAngleValue2 = (float) animation.getAnimatedValue();
        }
        this.postInvalidate();
    }
}
