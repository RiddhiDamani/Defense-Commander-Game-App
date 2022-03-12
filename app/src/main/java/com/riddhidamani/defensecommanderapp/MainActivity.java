package com.riddhidamani.defensecommanderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.riddhidamani.defensecommanderapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private int screenHeight;
    private int screenWidth;
    private float leftScreenPart, midScreenPart, rightScreenPart;
    private ViewGroup layout;
    private TextView score, level;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        setupFullScreen();
        getScreenDimensions();
        setupImages();

    }
    private void setupFullScreen() {
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

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels + getBarHeight();
        leftScreenPart = (float) (screenWidth * 0.33);
        midScreenPart = (float) (screenWidth * 0.5);
        rightScreenPart = (float) (screenWidth * 0.66);
    }

    private int getBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void setupImages() {
        layout = findViewById(R.id.constraint_layout);
        score = findViewById(R.id.score);
        level = findViewById(R.id.level);
        new CloudScrollerParallaxBG(this, layout, R.drawable.clouds, 30000, screenHeight, screenWidth);
    }

}