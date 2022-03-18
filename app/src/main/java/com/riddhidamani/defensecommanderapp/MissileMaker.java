package com.riddhidamani.defensecommanderapp;

import static com.riddhidamani.defensecommanderapp.Interceptor.INTERCEPTOR_BLAST;

import android.animation.AnimatorSet;
import android.util.Log;

import java.util.ArrayList;

public class MissileMaker implements Runnable{

    private static final String TAG = "MissileMaker";
    private final MainActivity mainActivity;
    private boolean isRunning;
    private final ArrayList<Missile> activeMissiles = new ArrayList<>();
    private final int screenWidth;
    private final int screenHeight;
    private static final int LEVEL_COUNT = 5;

    MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight) {
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    void setRunning(boolean running) {
        isRunning = running;
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for (Missile m : temp) {
            m.stop();
        }
    }

    @Override
    public void run() {
        setRunning(true);
        long delay = LEVEL_COUNT * 1000;
        while (isRunning) {

            int resId = missileDraw();

            long missileTime = (long) ((delay * 0.5) + (Math.random() * delay));
            final Missile missile = new Missile(screenWidth, screenHeight, missileTime, mainActivity);
            activeMissiles.add(missile);
            final AnimatorSet as = missile.setData(resId);

            mainActivity.runOnUiThread(as::start);
            SoundPlayer.getInstance().start("launch_missile");

            try {
                Thread.sleep((long) (delay * 0.333));
            }
            catch (InterruptedException exception) {
                exception.printStackTrace();
            }

            delay -= 10;
            if (delay <= 0)
                delay = 10;
            Log.d(TAG, "run: DELAY: " + delay);

            int levelNum = (int) ((LEVEL_COUNT * 4) - (delay / 250));
            Log.d(TAG, "run: LEVEL " + levelNum);
            mainActivity.setLevel(levelNum);

        }
    }

    private int missileDraw() {
        return R.drawable.missile;
    }

    void removeMissile(Missile m) {
        activeMissiles.remove(m);
    }

    void applyInterceptorBlast(Interceptor interceptor, int id) {
        Log.d(TAG, "applyInterceptorBlast: ----------START----------" + id);

        float x1 = interceptor.getX();
        float y1 = interceptor.getY();

        Log.d(TAG, "applyInterceptorBlast: INTERCEPTOR: " + x1 + ", " + y1);

        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);

        for (Missile m : temp) {

            float x2 = (int) (m.getX() + (0.5 * m.getWidth()));
            float y2 = (int) (m.getY() + (0.5 * m.getHeight()));

            Log.d(TAG, "applyInterceptorBlast:    Missile: " + x2 + ", " + y2);

            float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
            Log.d(TAG, "applyInterceptorBlast:    DIST: " + f);

            if (f < INTERCEPTOR_BLAST) {
                SoundPlayer.getInstance().start("interceptor_hit_missile");
                mainActivity.incrementScore();
                Log.d(TAG, "applyInterceptorBlast:    Hit: " + f);
                m.hitByInterceptor();
                m.interceptorBlast(x2, y2);
                nowGone.add(m);
            }
            Log.d(TAG, "applyInterceptorBlast: ----------END----------");
        }

        for (Missile m : nowGone) {
            activeMissiles.remove(m);
        }
    }

}
