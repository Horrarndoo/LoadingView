package com.zyw.horrarndoo.loadingview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zyw on 2017/3/11.
 */

public class CycleLoadingView extends View {

    // 画笔
    private Paint mPaint;

    // View 宽高
    private int mViewWidth;
    private int mViewHeight;

    private float diameter = 200;//圆直径
    private float radius = diameter / 2;//半径
    private float circumference;//圆周长
    private float maxSegment;//最大截取弧长

    public CycleLoadingView(Context context) {
        this(context, null);
    }

    public CycleLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAll();
    }

    public void initAll() {

        initPaint();

        initPath();

        initAnimator();

        // 进入动画
        mSearchingAnimator.start();
    }

    // 圆环
    private Path path_circle;

    // 测量Path 并截取部分的工具
    private PathMeasure mMeasure;

    // 默认的动效周期 2s
    private int defaultDuration = 2000;

    private ValueAnimator mSearchingAnimator;

    // 动画数值
    private float mAnimatorValue = 0;

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.customYellow));
        mPaint.setStrokeWidth(8);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
    }

    private void initPath() {
        path_circle = new Path();

        mMeasure = new PathMeasure();

        RectF oval2 = new RectF(-radius, -radius, radius, radius);      // 圆环

        path_circle.addArc(oval2, 0, -359.9f);
    }

    private void initAnimator() {
        mSearchingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mSearchingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mSearchingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCycleSegment(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);

        diameter = (mViewWidth < mViewHeight ? mViewWidth : mViewHeight) / 2;//保证圆环居中
        radius = diameter / 2;
        circumference = (float) (diameter * Math.PI);
        maxSegment = circumference * 3 / 4;

        initPath();
    }

    private void drawCycleSegment(Canvas canvas) {
        canvas.translate(mViewWidth / 2, mViewHeight / 2);

        canvas.drawColor(Color.BLACK);

        mMeasure.setPath(path_circle, false);
        Path dst = new Path();
        float stop = mMeasure.getLength() * mAnimatorValue;

        float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * maxSegment));
        mMeasure.getSegment(start, stop, dst, true);
        canvas.drawPath(dst, mPaint);
    }
}
