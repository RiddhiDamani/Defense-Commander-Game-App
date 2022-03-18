package com.riddhidamani.defensecommanderapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;

public class Interceptor {

    private static int count = 0;
    private final int id;
    private final MainActivity mainActivity;
    private ImageView imageview;
    private ObjectAnimator moveX, moveY;
    private final float startX;
    private final float startY;
    private float endX;
    private float endY;
    private static int idVal = -1;
    static final int INTERCEPTOR_BLAST = 180;
    private static final double DISTANCE_TIME = 0.75;
    ArrayList<Base> activeBases;
    ArrayList<Base> inactiveBase = new ArrayList<>();

    Interceptor(MainActivity mainActivity, float startX, float startY, float endX, float endY) {
        this.mainActivity = mainActivity;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.id = count++;
        initialize();
    }

    private void initialize() {
        imageview = new ImageView(mainActivity);
        imageview.setId(idVal--);
        imageview.setImageResource(R.drawable.interceptor);
        imageview.setTransitionName("Interceptor " + id);

        final int www = (int) (imageview.getDrawable().getIntrinsicWidth() * 0.5);

        imageview.setX(startX);
        imageview.setY(startY);
        imageview.setZ(-2);

        endX -= www;
        endY -= www;

        float a = calculateAngle(imageview.getX(), imageview.getY(), endX, endY);

        imageview.setRotation(a);
        mainActivity.getLayout().addView(imageview);

        double distance =  Math.sqrt((endY - imageview.getY()) * (endY - imageview.getY()) + (endX - imageview.getX()) * (endX - imageview.getX()));

        moveX = ObjectAnimator.ofFloat(imageview, "x", endX);
        moveX.setInterpolator(new AccelerateInterpolator());
        moveX.setDuration((long) (distance * DISTANCE_TIME));

        moveY = ObjectAnimator.ofFloat(imageview, "y", endY);
        moveY.setInterpolator(new AccelerateInterpolator());
        moveY.setDuration((long) (distance * DISTANCE_TIME));

        moveX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageview);
                mainActivity.lowerFlightInterceptor();
                makeBlast();
                // EC : #4 Interceptors that explode near a Base can destroy the Base.
                verifyBaseHitted();
            }
        });
    }

    private float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (180.0f - angle);

    }

    private void makeBlast() {
        SoundPlayer.getInstance().start("interceptor_blast");
        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.i_explode);

        iv.setTransitionName("Interceptor blast");

        float w = iv.getDrawable().getIntrinsicWidth();
        iv.setX(this.getX() - (w/2));

        iv.setY(this.getY() - (w/2));

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

        mainActivity.applyInterceptorBlast(this, imageview.getId());
    }

    private void verifyBaseHitted() {
        activeBases = mainActivity.getBaseAlive();
        for (Base base: activeBases) {
            float x1 = (int) base.getX();
            float y1 = (int) base.getY();
            float x2 = (int) (imageview.getX() + (0.5 * imageview.getWidth()));
            float y2 = (int) (imageview.getY() + (0.5 * imageview.getHeight()));

            float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));

            if (f < 180) {
                inactiveBase.add(base);
            }
        }

        for (Base base: inactiveBase) {
            base.blastTheBase();
            mainActivity.getLayout().removeView(base.getImageView());
            activeBases.remove(base);
            if (activeBases.size() == 0) {
                mainActivity.gameOver();
            }
        }
    }

    float getX() {
        int xVar = imageview.getWidth() / 2;
        return imageview.getX() + xVar;
    }

    float getY() {
        int yVar = imageview.getHeight() / 2;
        return imageview.getY() + yVar;
    }

    void launch() {
        moveX.start();
        moveY.start();
    }
}
