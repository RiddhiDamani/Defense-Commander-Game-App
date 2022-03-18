package com.riddhidamani.defensecommanderapp;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    // Screen Height & Width
    private int screenHeight;
    private int screenWidth;
    private float base1leftSW;
    private float base2midSW;
    private float base3rightSW;

    // Layout Variables
    private ViewGroup layout;
    private TextView score, level;

    // Bases
    private Base base1, base2, base3;
    private final ArrayList<Base> baseAlive = new ArrayList<>();

    // Missile Maker object
    private MissileMaker missileMaker;

    // Scores
    private int totalScore;
    private int lowestGameScore;

    // Flight Interceptor count
    private int interceptorInFlight = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        setupFullScreen();
        getScreenDimensions();
        setupImages();
        setupBases();
        setupOnTouchListener();

        missileMaker = new MissileMaker(this, screenWidth, screenHeight);
        new Thread(missileMaker).start();
    }

    // Set up Full Screen
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

    // Getting Screen Dimensions
    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels + getBarHeight();

        // defining 3 parts of screen for 3 bases
        base1leftSW = (float) (screenWidth * 0.33);
        base2midSW = (float) (screenWidth * 0.5);
        base3rightSW = (float) (screenWidth * 0.66);
    }

    private int getBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // Setting Up Background Images
    private void setupImages() {
        layout = findViewById(R.id.constraint_layout);
        score = findViewById(R.id.score);
        level = findViewById(R.id.level);
        new CloudScroller(this, layout, R.drawable.clouds, 30000, screenHeight, screenWidth);
    }

    // Setting up 3 Bases
    private void setupBases() {
        // Base 1
        base1 = new Base(this, findViewById(R.id.base1), baseAlive);
        base1.setX((float) (screenWidth * 0.25));
        base1.setY(screenHeight);

        // Base 2
        base2 = new Base(this, findViewById(R.id.base2), baseAlive);
        base2.setX((float) (screenWidth * 0.50));
        base2.setY(screenHeight);

        // Base 3
        base3 = new Base(this, findViewById(R.id.base3), baseAlive);
        base3.setX((float) (screenWidth * 0.75));
        base3.setY(screenHeight);
    }

    // Layout
    public ViewGroup getLayout() {
        return layout;
    }

    // removing missiles
    public void removeMissile(Missile m) {
        missileMaker.removeMissile(m);
    }

    // Setting Levels
    public void setLevel(final int value) {
        runOnUiThread(() -> level.setText(String.format(Locale.getDefault(), "Level: %d", value)));
    }

    public void applyInterceptorBlast(Interceptor interceptor, int id) {
        missileMaker.applyInterceptorBlast(interceptor, id);
    }

    // Array List for all bases
    public ArrayList<Base> getBaseAlive() {
        return baseAlive;
    }

    // gameOver logic
    public void gameOver() {
        missileMaker.setRunning(false);
        // Game Over title
        ImageView titleImageView= findViewById(R.id.titleImageGameOver);
        titleImageView.setVisibility(View.VISIBLE);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        titleImageView.startAnimation(myFadeInAnimation);

        // Validating Scores
        validateScores();
    }

    // increment scores
    public void incrementScore() {
        totalScore++;
        score.setText(String.format(Locale.getDefault(), "%d", totalScore));
    }

    public void lowerFlightInterceptor() {
        interceptorInFlight--;
    }

    // Validating Total Scores against the DB
    private void validateScores() {
        TopPlayersDatabaseHandler databaseHandler =
                new TopPlayersDatabaseHandler(this, "", 0, -1);
        new Thread(databaseHandler).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupOnTouchListener() {
        layout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        });
    }

    // handle touch logic : EC #1 and EC #4
    private void handleTouch(float x, float y) {
        ImageView launcher;

        // Extra Credit: 1: Limiting number of interceptors allowed in flight to 3 at any one time.
        if (interceptorInFlight >= 3) {
            return;
        }

        interceptorInFlight++;
        // EC : #4 Interceptors that explode near a Base can destroy the Base.
        if (x < base1leftSW) {
            if (baseAlive.contains(base1)) {
                launcher = findViewById(R.id.base1);
            }
            else if (baseAlive.contains(base2)) {
                launcher = findViewById(R.id.base2);
            }
            else {
                launcher = findViewById(R.id.base3);
            }
        }
        else if (x < base3rightSW) {
            if (baseAlive.contains(base2)) {
                launcher = findViewById(R.id.base2);
            }
            else {
                if (x < base2midSW) {
                    if (baseAlive.contains(base1)) {
                        launcher = findViewById(R.id.base1);
                    }
                    else {
                        launcher = findViewById(R.id.base3);
                    }
                }
                else {
                    if (baseAlive.contains(base3)) {
                        launcher = findViewById(R.id.base3);
                    }
                    else {
                        launcher = findViewById(R.id.base1);
                    }
                }
            }
        }
        else {
            if (baseAlive.contains(base3)) {
                launcher = findViewById(R.id.base3);
            }
            else if (baseAlive.contains(base2)) {
                launcher = findViewById(R.id.base2);
            }
            else {
                launcher = findViewById(R.id.base1);
            }
        }

        if (launcher == null) return;

        double startX = launcher.getX() + (0.5 * launcher.getWidth());
        double startY = launcher.getY() + (0.5 * launcher.getHeight());

        Interceptor interceptor = new Interceptor(this,  (float) (startX - 10), (float) (startY - 30), x, y);
        SoundPlayer.getInstance().start("launch_interceptor");
        interceptor.launch();
    }

    public void finalScoresResult(TopPlayersDetails topPlayersDetails) {
        lowestGameScore = topPlayersDetails.getLowestGameScore();
        if (totalScore > lowestGameScore) {
            createAlertDialog(topPlayersDetails);
        }
        else {
            setResults(topPlayersDetails.getTopPlayerDetails());
        }
    }

    private void createAlertDialog(TopPlayersDetails topPlayersDetails) {
        final EditText et;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        et = new EditText(getApplicationContext());
        int maxLength = 3;
        et.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});

        alert.setTitle("You are a Top-Player!");
        alert.setMessage("Please enter your initials (up to 3 characters)");
        alert.setView(et);

        // Ok Button
        alert.setPositiveButton("OK", (dialog, i) -> {
            String initials = et.getText().toString();
            String levelStr = level.getText().toString();
            int levelValue = Integer.parseInt(levelStr.substring(7));

            TopPlayersDatabaseHandler databaseHandler =
                    new TopPlayersDatabaseHandler(MainActivity.this, initials, totalScore, levelValue);
            new Thread(databaseHandler).start();
        });

        // Cancel Button
        alert.setNegativeButton("CANCEL", (dialog, i) -> setResults(topPlayersDetails.getTopPlayerDetails()));
        alert.show();
    }

    public void setResults(String s) {
        Intent intent = new Intent(this, TopPlayersActivity.class);
        intent.putExtra("DATA", s);
        startActivity(intent);
    }

}