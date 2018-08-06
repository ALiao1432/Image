package com.study.ian.image;

import android.util.Log;

public class ImageParameter {

    private final String TAG = "ImageParameter";

    private int width;
    private int height;

    ImageParameter(int w, int h) {
        this.width = w;
        this.height = h;

        Log.d(TAG, "ImageParameter w : " + width + ", h : " + height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
