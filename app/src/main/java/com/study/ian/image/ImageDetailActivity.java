package com.study.ian.image;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.study.ian.image.view.MyRecyclerVIewAdapter;

public class ImageDetailActivity extends AppCompatActivity {

    private final String TAG = "ImageDetailActivity";

    private ImageView detailImageView;
    private LinearLayout linearLayout;
    private String[] detailData;
    private View decorView;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private ValueAnimator scaleAnimator;
    private float currentScale = 1f;
    private float dx = 0f;
    private float dy = 0f;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            hideUi();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        scaleGestureDetector = new ScaleGestureDetector(this, new CusScaleGestureListener());
        gestureDetector = new GestureDetector(this, new CusGestureListener());
        scaleAnimator = new ValueAnimator();

        initValueAnimator();
        findView();
        setView();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Log.d(TAG, "savedInstanceState is null");
        } else {
            detailData = bundle.getStringArray(MyRecyclerVIewAdapter.CLICKED_IMG);

            Glide.with(this)
                    .load(detailData[0])
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(detailImageView);
        }
    }

    private void initValueAnimator() {
        scaleAnimator.setDuration(120);
        scaleAnimator.setInterpolator(new LinearInterpolator());
    }

    private void findView() {
        decorView = getWindow().getDecorView();
        linearLayout = findViewById(R.id.detailLinearLayout);
        detailImageView = findViewById(R.id.detailImageView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setView() {
        linearLayout.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void hideUi() {
        if (decorView != null) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    private void scaleImageWithAnimation(float fromScale, float toScale) {
        scaleAnimator.setFloatValues(fromScale, toScale);
        scaleAnimator.addUpdateListener(valueAnimator -> {
            detailImageView.setScaleX((float) valueAnimator.getAnimatedValue());
            detailImageView.setScaleY((float) valueAnimator.getAnimatedValue());
        });
        scaleAnimator.start();
        currentScale = toScale;
    }

    private void setImageToNormalSize() {
        detailImageView.setTranslationX(0);
        detailImageView.setTranslationY(0);
        dx = 0f;
        dy = 0f;
    }

    private class CusScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = Math.abs(detector.getScaleFactor());
            float scale = (currentScale - 1f) * scaleFactor + scaleFactor;

            if (scale >= 0.75f) {
                detailImageView.setScaleX(scale);
                detailImageView.setScaleY(scale);
            }
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            currentScale = detailImageView.getScaleX();

            if (currentScale < 1f) {
                scaleImageWithAnimation(currentScale, 1f);
                setImageToNormalSize();
                currentScale = 1f;
            }
        }
    }

    private class CusGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float currentScale = detailImageView.getScaleX();

            if (currentScale != 1f) {
                scaleImageWithAnimation(currentScale, 1f);
            } else {
                scaleImageWithAnimation(1f, 2.5f);
            }
            setImageToNormalSize();
            return false;
        }

        // TODO: 2018-08-01 there is a bug : translation margin need to tale care
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            switch (e2.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    dx += distanceX;
                    dy += distanceY;
                    detailImageView.setTranslationX(-dx);
                    detailImageView.setTranslationY(-dy);
                    break;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}
