package com.riddhidamani.defensecommanderapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

//Parallax Class
public class CloudScroller {

    private final Context context;
    private final ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private final long duration;
    private final int resId;
    private final int screenHeight;
    private final int screenWidth;
    final int[] alphaValue = {100};
    final int[] speedValue = {1};
    private static final String TAG = "CloudScroller";


    CloudScroller(Context context, ViewGroup layout, int resId, long duration, int screenHeight, int screenWidth) {
        this.context = context;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        setupBackground();
    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);

        Log.d(TAG, "setupBackground: Inside setupBackground method");
        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(screenWidth + getBarHeight(), screenHeight);
        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);

        backImageA.setImageResource(resId);
        backImageB.setImageResource(resId);

        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        backImageA.setZ(-1);
        backImageB.setZ(-1);

        animateForward();
    }

    private void animateForward() {
        float width = screenWidth + getBarHeight();
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);

        animator.addUpdateListener(animation -> {
            final float progress = (float) animation.getAnimatedValue();

            float a_translationX = width * progress;
            float b_translationX = width * progress - width;

            backImageA.setTranslationX(-a_translationX);
            backImageB.setTranslationX(-b_translationX);

            // EC #3: Make the scrolling clouds fade in and out over time by slowly varying their “alpha” value
            if (alphaValue[0] > 200) {
                speedValue[0] *= -1;
            }
            if (alphaValue[0] < 30) {
                speedValue[0] *= -1;
            }
            alphaValue[0] += speedValue[0];

            // Log.d(TAG, "animateForward: Alpha Value:" + alphaValue[0]);
            // Log.d(TAG, "animateForward: Speed Value:" + speedValue[0]);

            backImageA.setImageAlpha(alphaValue[0]);
            backImageB.setImageAlpha(alphaValue[0]);

        });
        animator.start();
    }

    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

}

