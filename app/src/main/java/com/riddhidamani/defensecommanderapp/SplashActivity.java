package com.riddhidamani.defensecommanderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_LENGTH = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fullScreenSetUp();
        fadeInTitle();
        setupBackGroundSound();
        initiateMainActivity();
    }

    private void fullScreenSetUp() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void fadeInTitle() {
        ImageView titleImageView= findViewById(R.id.titleImage);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        titleImageView.startAnimation(myFadeInAnimation);
    }

    private void setupBackGroundSound() {
        SoundPlayer soundPlayer = SoundPlayer.getInstance();
        soundPlayer.setupSound(this, "background", R.raw.background, true);
        soundPlayer.setupSound(this, "base_blast", R.raw.base_blast, false);
        soundPlayer.setupSound(this, "interceptor_blast", R.raw.interceptor_blast, false);
        soundPlayer.setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile, false);
        soundPlayer.setupSound(this, "launch_interceptor", R.raw.launch_interceptor, false);
        soundPlayer.setupSound(this, "launch_missile", R.raw.launch_missile, false);
        soundPlayer.setupSound(this, "missile_miss", R.raw.missile_miss, false);
    }

    private void initiateMainActivity() {
        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }, SPLASH_LENGTH);
    }
}