package com.riddhidamani.defensecommanderapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import java.util.ArrayList;

// Base Class
public class Base {
    private float x;
    private float y;
    private final ImageView imageView;
    private final MainActivity mainActivity;
    private final ArrayList<Base> baseAlive;

    public Base(MainActivity mainActivity, ImageView view, ArrayList<Base> baseAlive) {
        this.mainActivity = mainActivity;
        this.imageView = view;
        this.baseAlive = baseAlive;
        baseAlive.add(this);
    }

    public void blastTheBase() {
        SoundPlayer.getInstance().start("base_blast");
        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.blast);
        iv.setTransitionName("Base blast");

        int w = iv.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(this.getX() - offset);
        iv.setY(this.getY() - offset);

        iv.setZ(-2);

        mainActivity.getLayout().addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(iv);
            }
        });
        alpha.start();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
