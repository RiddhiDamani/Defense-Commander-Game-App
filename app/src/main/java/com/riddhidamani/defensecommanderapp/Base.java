package com.riddhidamani.defensecommanderapp;

import android.widget.ImageView;

import java.util.ArrayList;

public class Base {
    private float x;
    private float y;
    private final ImageView imageView;
    private final MainActivity mainActivity;
    private final ArrayList<Base> activeBases;

    public Base(MainActivity mainActivity, ImageView view, ArrayList<Base> activeBases) {
        this.mainActivity = mainActivity;
        this.imageView = view;
        this.activeBases = activeBases;
        activeBases.add(this);
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
