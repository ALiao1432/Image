package com.study.ian.image.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.study.ian.image.ImageData;
import com.study.ian.image.R;

import java.util.List;

public class MyDetailCardView extends CardView {

    private final String TAG = "MyDetailCardView";

    private final int ANIMATOR_DURATION = 300;
    private boolean isOpen = false;
    private int colorIndex = 1;
    private ConstraintLayout constraintLayout;
    private TextView infoTextView;
    private TextView pathTextView;
    private TextView dataSizeTextView;
    private TextView sizeTextView;
    private ImageView closeImageView;
    private ImageView shareImageView;
    private ImageView deleteImageView;

    private OnTouchListener onTouchListener = (v, e) -> {
        switch (v.getId()) {
            case R.id.shareImageView:

                break;
            case R.id.deleteImageView:

                break;
            case R.id.closeImageView:
                if (e.getActionMasked() == MotionEvent.ACTION_UP) {
                        closeCardView();
                }
                break;
        }
        v.performClick();
        return true;
    };

    public MyDetailCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setCardView();
    }

    private void setCardView() {
        this.setVisibility(View.INVISIBLE);
        this.setRadius(75);
        this.setElevation(2);
    }

    private void findView() {
        constraintLayout = findViewById(R.id.detailConstraintLayout);
        infoTextView = findViewById(R.id.infoText);
        pathTextView = findViewById(R.id.pathText);
        dataSizeTextView = findViewById(R.id.dataSizeText);
        sizeTextView = findViewById(R.id.sizeText);
        closeImageView = findViewById(R.id.closeImageView);
        shareImageView = findViewById(R.id.shareImageView);
        deleteImageView = findViewById(R.id.deleteImageView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setView(List<Palette.Swatch> swatchList) {
        constraintLayout.measure(getWidth(), getHeight());

        infoTextView.setTextColor(swatchList.get(colorIndex).getTitleTextColor());
        pathTextView.setTextColor(swatchList.get(colorIndex).getBodyTextColor());
        dataSizeTextView.setTextColor(swatchList.get(colorIndex).getBodyTextColor());
        sizeTextView.setTextColor(swatchList.get(colorIndex).getBodyTextColor());
        closeImageView.setImageTintList(ColorStateList.valueOf(swatchList.get(colorIndex).getTitleTextColor()));
        shareImageView.setImageTintList(ColorStateList.valueOf(swatchList.get(colorIndex).getBodyTextColor()));
        deleteImageView.setImageTintList(ColorStateList.valueOf(swatchList.get(colorIndex).getBodyTextColor()));

        closeImageView.setOnTouchListener(onTouchListener);
        shareImageView.setOnTouchListener(onTouchListener);
        deleteImageView.setOnTouchListener(onTouchListener);
    }

    public void setListenerParameter(View view, MotionEvent event) {
        onTouchListener.onTouch(view, event);
    }

    @SuppressLint("SetTextI18n")
    public void setViewText(ImageData imageData) {
        long size = Long.valueOf(imageData.getDataSize());
        float kBytes = size / 1000;
        float MBytes = kBytes / 1000;
        String tempSize;

        if (MBytes > 1) {
            tempSize = MBytes + " MB";
        } else {
            tempSize = kBytes + " kB";
        }

        infoTextView.setTextSize(24);

        pathTextView.setText(imageData.getData());
        sizeTextView.setText(imageData.getWidth() + " x " + imageData.getHeight());
        dataSizeTextView.setText(tempSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.round(MeasureSpec.getSize(widthMeasureSpec) * .8f);
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

        this.setCardBackgroundColor(list.get(colorIndex).getRgb());

        findView();
        setView(list);
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
