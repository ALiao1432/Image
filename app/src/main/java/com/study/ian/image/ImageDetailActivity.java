package com.study.ian.image;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.study.ian.image.customview.MyDetailCardView;
import com.study.ian.image.customview.MyRecyclerViewAdapter;

public class ImageDetailActivity extends AppCompatActivity {

    private final String TAG = "ImageDetailActivity";

    private ImageView detailImageView;
    private RelativeLayout relativeLayout;
    private View decorView;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private ValueAnimator scaleAnimator;
    private ImageParameter imageParameter;
    private MyDetailCardView cardView;
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
            ImageData imageData = bundle.getParcelable(MyRecyclerViewAdapter.CLICKED_IMG);

            if (imageData != null) {
                Glide.with(this)
                        .load(imageData.getData())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
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
                                cardView.getColorViaPalette(resource);
                                cardView.setViewText(imageData);
                                setListener();
                                return false;
                            }
                        })
                        .into(detailImageView);
            }
        }
    }

    private void initValueAnimator() {
        scaleAnimator.setDuration(120);
        scaleAnimator.setInterpolator(new LinearInterpolator());
    }

    private void findView() {
        decorView = getWindow().getDecorView();
        relativeLayout = findViewById(R.id.detailRelativeLayout);
        detailImageView = findViewById(R.id.detailImageView);
        cardView = findViewById(R.id.detailCardView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        // scaleGestureDetector need to use relativeLayout's event rather than detailImageView's event
        relativeLayout.setOnTouchListener((v, e) -> {
            if (cardView.isOpen()) {
                cardView.setListenerParameter(v, e);
            } else {
                scaleGestureDetector.onTouchEvent(e);
                gestureDetector.onTouchEvent(e);
            }
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

    @Override
    public void onBackPressed() {
        if (cardView.isOpen()) {
            cardView.closeCardView();
        } else {
            ImageDetailActivity.this.finish();
        }
    }

    private class CusScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = Math.abs(detector.getScaleFactor());
            float scale = currentScale * scaleFactor;

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

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float eventTime = e2.getEventTime() - e1.getEventTime();

            if (currentScale == 1 && eventTime < 3000) {
                if (Math.abs(velocityX) < 5000 && velocityY < -2500) {
                    // swipe up
                    if (cardView.isOpen()) {
                        cardView.closeCardView();
                    } else {
                        ImageDetailActivity.this.finish();
                    }
                } else if (Math.abs(velocityX) < 5000 && velocityY > 2500) {
                    // swipe down
                    if (!cardView.isOpen()) {
                        cardView.openCardView();
                    }
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
