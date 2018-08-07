package com.study.ian.image;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.study.ian.image.view.MyRecyclerVIewAdapter;

public class ImageDetailActivity extends AppCompatActivity {

    private final String TAG = "ImageDetailActivity";

    private ImageView detailImageView;
    private LinearLayout linearLayout;
    private View decorView;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private ValueAnimator scaleAnimator;
    private ImageParameter imageParameter;
    private int currentWidth;
    private int currentHeight;
    private float currentScale = 1f;
    private float dx = 0f;
    private float dy = 0f;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            hideUi();
            loadImage();
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
    }

    private void loadImage() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Log.d(TAG, "savedInstanceState is null");
        } else {
            String[] detailData = bundle.getStringArray(MyRecyclerVIewAdapter.CLICKED_IMG);

            Glide.with(this)
                    .load(detailData[0])
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // set width and height after resource is ready
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
                            int screenWidth = displayMetrics.widthPixels;
                            int screenHeight = displayMetrics.heightPixels;

                            currentWidth = resource.getIntrinsicWidth();
                            currentHeight = resource.getIntrinsicHeight();
                            imageParameter = new ImageParameter(
                                    currentWidth,
                                    currentHeight,
                                    screenWidth,
                                    screenHeight
                            );
                            setView();
                            return false;
                        }
                    })
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
        currentWidth = (int) (imageParameter.getWidth() * toScale);
        currentHeight = (int) (imageParameter.getHeight() * toScale);
    }

    private void setImageToOriginPosition() {
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

            if (scale >= .5f) {
                detailImageView.setScaleX(scale);
                detailImageView.setScaleY(scale);
            }
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            currentScale = detailImageView.getScaleX();

            if (currentScale < .7f) {
                ImageDetailActivity.this.finish();
            } else if (currentScale < 1f) {
                scaleImageWithAnimation(currentScale, 1f);
                setImageToOriginPosition();
                currentScale = 1f;
            } else {
                currentWidth = (int) (currentScale * imageParameter.getWidth());
                currentHeight = (int) (currentScale * imageParameter.getHeight());
            }

            imageParameter.setImageFourSides(
                    currentWidth,
                    currentHeight,
                    (int) detailImageView.getTranslationX(),
                    (int) detailImageView.getTranslationY()
            );
        }
    }

    private class CusGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float currentScale = detailImageView.getScaleX();

            if (currentScale != 1f) {
                scaleImageWithAnimation(currentScale, 1f);
                setImageToOriginPosition();
            } else if (dx != 0 || dy != 0) {
                setImageToOriginPosition();
            } else {
                scaleImageWithAnimation(1f, 2.5f);
            }

            imageParameter.setImageFourSides(
                    currentWidth,
                    currentHeight,
                    (int) detailImageView.getTranslationX(),
                    (int) detailImageView.getTranslationY()
            );
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            switch (e2.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if ((imageParameter.isLeftOutside() && distanceX < 0)
                            || (imageParameter.isRightOutside() && distanceX > 0)) {
                        dx += distanceX;
                        detailImageView.setTranslationX(-dx);
                        imageParameter.setImageFourSides(
                                currentWidth,
                                currentHeight,
                                (int) detailImageView.getTranslationX(),
                                (int) detailImageView.getTranslationY()
                        );
                    }
                    if ((imageParameter.isTopOutside() && distanceY < 0)
                            || imageParameter.isBottomOutside() && distanceY > 0) {
                        dy += distanceY;
                        detailImageView.setTranslationY(-dy);
                        imageParameter.setImageFourSides(
                                currentWidth,
                                currentHeight,
                                (int) detailImageView.getTranslationX(),
                                (int) detailImageView.getTranslationY()
                        );
                    }
                    break;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}
