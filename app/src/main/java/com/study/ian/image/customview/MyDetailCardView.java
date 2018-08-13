package com.study.ian.image.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;

import java.util.List;

public class MyDetailCardView extends CardView {

    private final String TAG = "MyDetailCardView";

    private final int ANIMATOR_DURATION = 350;
    private boolean isOpen = false;

    public MyDetailCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.setVisibility(View.INVISIBLE);
        this.setRadius(75);
        this.setElevation(2);

        this.setOnClickListener(view -> {

        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.round(MeasureSpec.getSize(widthMeasureSpec) * .75f);
        int height = Math.round(MeasureSpec.getSize(heightMeasureSpec) * .6f);

        setMeasuredDimension(width, height);
    }

    public void openCardView() {
        int cx = getWidth() / 2;
        int cy = 0;
        float finalRadius = (float) Math.hypot(getWidth(), getHeight());

        Animator animator = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0, finalRadius);
        animator.setDuration(ANIMATOR_DURATION);
        animator.setInterpolator(new DecelerateInterpolator());
        this.setVisibility(View.VISIBLE);
        animator.start();
        isOpen = true;
    }

    public void closeCardView() {
        int cx = getWidth() / 2;
        int cy = 0;
        float finalRadius = (float) Math.hypot(getWidth(), getHeight());

        Animator animator = ViewAnimationUtils.createCircularReveal(this, cx, cy, finalRadius, 0);
        animator.setDuration(ANIMATOR_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                MyDetailCardView.this.setVisibility(View.INVISIBLE);
            }
        });
        animator.start();
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void getColorViaPalette(Drawable drawable) {
        Bitmap bitmap = drawableToBitmap(drawable);
        Palette palette = Palette.from(bitmap).generate();
        List<Palette.Swatch> list = palette.getSwatches();

        int index = 1;

        this.setCardBackgroundColor(list.get(index).getRgb());
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
