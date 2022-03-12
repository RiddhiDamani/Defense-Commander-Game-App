package com.riddhidamani.defensecommanderapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CloudScrollerParallaxBG implements Runnable {

    private final Context context;
    private final ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private final long duration;
    private final int resId;
    private final int screenHeight;
    private final int screenWidth;
    private static final String TAG = "CloudScrollerParallaxBG";


    CloudScrollerParallaxBG(Context context, ViewGroup layout, int resId, long duration, int screenHeight, int screenWidth) {
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

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(screenWidth + getBarHeight(), screenHeight);
        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);

        Bitmap backBitmapA = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap backBitmapB = BitmapFactory.decodeResource(context.getResources(), resId);

        backImageA.setImageBitmap(backBitmapA);
        backImageB.setImageBitmap(backBitmapB);

        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        backImageA.setZ(-1);
        backImageB.setZ(-1);

        animateForward();
        //new Thread(this).start();
    }

    @Override
    public void run() {

        backImageA.setX(0);
        backImageB.setX(-(screenWidth + getBarHeight()));
        double cycleTime = 25.0;

        double cycles = duration / cycleTime;
        double distance = (screenWidth + getBarHeight()) / cycles;

        while (true) {

            long start = System.currentTimeMillis();

            double aX = backImageA.getX() - distance;
            backImageA.setX((float) aX);
            double bX = backImageB.getX() - distance;
            backImageB.setX((float) bX);

            long workTime = System.currentTimeMillis() - start;

            if (backImageA.getX() < -(screenWidth + getBarHeight()))
                backImageA.setX((screenWidth + getBarHeight()));

            if (backImageB.getX() < -(screenWidth + getBarHeight()))
                backImageB.setX((screenWidth + getBarHeight()));

            long sleepTime = (long) (cycleTime - workTime);

            if (sleepTime <= 0) {
                Log.d(TAG, "run: NOT KEEPING UP! " + sleepTime);
                continue;
            }

            try {
                Thread.sleep((long) (cycleTime - workTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void animateForward() {

        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        float width = screenWidth + getBarHeight();
        final int[] alpha = {100};
        final int[] rate = {1};

        animator.addUpdateListener(animation -> {
            final float progress = (float) animation.getAnimatedValue();

            float a_translationX = width * progress;
            float b_translationX = width * progress - width;

            backImageA.setTranslationX(-a_translationX);
            backImageB.setTranslationX(-b_translationX);

            if (alpha[0] > 220 || alpha[0] < 20) rate[0] = rate[0] * -1;
            alpha[0] = alpha[0] + rate[0];
            backImageA.setImageAlpha(alpha[0]);
            backImageB.setImageAlpha(alpha[0]);

            //Log.d(TAG, "onAnimationUpdate: A " + translationX + "   B " + (translationX - width));
            //Log.d(TAG, "onAnimationUpdate: A " + backImageA.getY() + "   B " + backImageB.getY());

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

