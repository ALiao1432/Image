package com.study.ian.image;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.study.ian.image.view.MyAdapter;

public class ImageDetailActivity extends AppCompatActivity {

    private final String TAG = "ImageDetailActivity";

    private ImageView detailImageView;
    private LinearLayout linearLayout;
    private String[] detailData;
    private View decorView;
    private GestureDetectorCompat gestureDetectorCompat;

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

        findView();
        setView();

        gestureDetectorCompat = new GestureDetectorCompat(this.getApplicationContext(), new CusGestureListener());

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Log.d(TAG, "savedInstanceState is null");
        } else {
            detailData = bundle.getStringArray(MyAdapter.CLICKED_IMG);

            Glide.with(this)
                    .load(detailData[0])
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(detailImageView);
        }
    }

    private void findView() {
        decorView = getWindow().getDecorView();
        linearLayout = findViewById(R.id.detailLinearLayout);
        detailImageView = findViewById(R.id.detailImageView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setView() {
        linearLayout.setOnTouchListener((v, event) -> {
            gestureDetectorCompat.onTouchEvent(event);
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
            );
        }
    }

    private class CusGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            hideUi();
            return true;
        }
    }
}
