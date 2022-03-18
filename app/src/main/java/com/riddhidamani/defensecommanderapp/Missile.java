package com.riddhidamani.defensecommanderapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import java.util.ArrayList;

public class Missile {
    private final MainActivity mainActivity;
    private final ImageView missileImageView;
    private final AnimatorSet aSet = new AnimatorSet();
    private final int screenHeight;
    private final int screenWidth;
    private final long screenTime;
    private static final String TAG = "Missile";
    private boolean hitByInterceptor = false;

    Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;


        missileImageView = new ImageView(mainActivity);
        missileImageView.setY(-200);
        missileImageView.setZ(-2);

        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(missileImageView));

    }

    AnimatorSet setData(final int drawId) {
        mainActivity.runOnUiThread(() -> missileImageView.setImageResource(drawId));

        int startX = (int) (Math.random() * screenWidth);
        int endX = (int) (Math.random() * screenWidth);
        int startY = -200;
        int endY = screenHeight - 100;

        float a = calculateAngle(startX, startY, endX, endY);
        missileImageView.setRotation(a);


        ObjectAnimator xAnim = ObjectAnimator.ofFloat(missileImageView, "x", startX, endX);
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration(screenTime);
        xAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.runOnUiThread(() -> {
                    if (!hitByInterceptor) {
                        if (!hittedBase()) {
                            interceptorBlast(missileImageView.getX(), missileImageView.getY());
                            SoundPlayer.getInstance().start("missile_miss");
                            mainActivity.removeMissile(Missile.this);
                        }
                    }
                    Log.d(TAG, "run: NUM VIEWS " +
                            mainActivity.getLayout().getChildCount());
                });

            }
        });

        ObjectAnimator yAnim = ObjectAnimator.ofFloat(missileImageView, "y", startY, endY);
        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);

        aSet.playTogether(xAnim, yAnim);
        return aSet;

    }

    private boolean hittedBase() {
        ArrayList<Base> invisibleBases = new ArrayList<>();
        ArrayList<Base> activeBases = mainActivity.getBaseAlive();

        for (Base b: activeBases) {
            float x1 = (int) b.getX();
            float y1 = (int) b.getY();
            float x2 = (int) (missileImageView.getX() + (0.5 * missileImageView.getWidth()));
            float y2 = (int) (missileImageView.getY() + (0.5 * missileImageView.getHeight()));
            float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
            if (f < 180) {
                invisibleBases.add(b);
            }
        }

        if (!invisibleBases.isEmpty()) {
            for (Base b: invisibleBases) {
                b.blastTheBase();
                mainActivity.getLayout().removeView(b.getImageView());
                activeBases.remove(b);
                interceptorBlast(missileImageView.getX(), missileImageView.getY());
                mainActivity.removeMissile(Missile.this);
                if (activeBases.size() == 0) {
                    mainActivity.gameOver();
                }
                return true;
            }
        }
        return false;
    }

    void stop() {
        aSet.cancel();
    }

    float getX() {
        return missileImageView.getX();
    }

    float getY() {
        return missileImageView.getY();
    }

    float getWidth() {
        return missileImageView.getWidth();
    }

    float getHeight() {
        return missileImageView.getHeight();
    }

    void hitByInterceptor() { hitByInterceptor = true; }

    void interceptorBlast(float x, float y) {

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);

        iv.setTransitionName("Missile Intercepted Blast");

        int w = missileImageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x - offset);
        iv.setY(y - offset);
        iv.setZ(-2);
        iv.setRotation((float) (360.0 * Math.random()));

        aSet.cancel();

        mainActivity.getLayout().removeView(missileImageView);
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

    private float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (180.0f - angle);
    }
}
