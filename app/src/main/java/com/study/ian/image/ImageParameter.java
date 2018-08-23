package com.study.ian.image;

import android.util.Log;

public class ImageParameter {

    private final String TAG = "ImageParameter";

    private int width;
    private int height;
    private int screenWidth;
    private int screenHeight;
    private int left;
    private int right;
    private int top;
    private int bottom;

    ImageParameter(int width, int height, int screenWidth, int screenHeight) {
        this.width = width;
        this.height = height;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setImageFourSides(int currentWidth, int currentHeight, int translateX, int translateY) {
        left = (screenWidth - currentWidth) / 2 + translateX;
        right = (screenWidth + currentWidth) / 2 + translateX;
        top = (screenHeight - currentHeight) / 2 + translateY;
        bottom = (screenHeight + currentHeight) / 2 + translateY;
    }

    public boolean isLeftOutside() {
        return left < 0;
    }

    public boolean isRightOutside() {
        return right > screenWidth;
    }

    public boolean isTopOutside() {
        return top < 0;
    }

    public boolean isBottomOutside() {
        return bottom > screenHeight;
    }

    @Override
    public String toString() {
        return "ImageParameter{" +
                "width=" + width +
                ", height=" + height +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                ", left=" + left +
                ", right=" + right +
                ", top=" + top +
                ", bottom=" + bottom +
                '}';
    }
}
