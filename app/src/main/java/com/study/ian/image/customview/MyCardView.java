package com.study.ian.image.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;

public class MyCardView extends CardView {

    private final String TAG = "MyCardView";

    private boolean isOpen = false;
    public int ANIMATOR_DURATION = 300;
    public int cx;
    public int cy;

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void openCardView() {
        float finalRadius = (float) Math.hypot(getWidth(), getHeight());

        Animator animator = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0, finalRadius);
        animator.setDuration(ANIMATOR_DURATION);
        animator.setInterpolator(new DecelerateInterpolator());
        this.setVisibility(View.VISIBLE);
        animator.start();
        isOpen = true;
    }

    public void closeCardView() {
        float finalRadius = (float) Math.hypot(getWidth(), getHeight());

        Animator animator = ViewAnimationUtils.createCircularReveal(this, cx, cy, finalRadius, 0);
        animator.setDuration(ANIMATOR_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                MyCardView.this.setVisibility(View.INVISIBLE);
            }
        });
        animator.start();
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
