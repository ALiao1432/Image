package com.study.ian.image.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

public class MorphView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "XmlLabelParser";

    private final SvgData svgData;
    private final Paint paint = new Paint();
    private DataPath path;
    private ValueAnimator pointAnimator;
    private int currentId;


    @SuppressWarnings("ClickableViewAccessibility")
    MorphView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);

        svgData = new SvgData(context);

        initPaint();
        initAnimator();
    }

    private void initPaint() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getWidth() / 10);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initAnimator() {
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        final long animationDuration = 250;

        pointAnimator = ValueAnimator.ofFloat(0, 1, 1.025f, 1.0125f, 1);
        pointAnimator.setDuration(animationDuration);
        pointAnimator.setInterpolator(linearInterpolator);
        pointAnimator.addUpdateListener(valueAnimator -> {
            path.reset();
            path = svgData.getMorphPath((float) valueAnimator.getAnimatedValue());
            invalidate();
        });
    }

    private void initPath(int id) {
        path = svgData.getPath(id, this);
        currentId = id;
    }

    public void performAnimation(int toId) {
        svgData.setMorphRes(currentId, toId, this);
        currentId = toId;
        pointAnimator.start();
    }

    public void setCurrentId(int id) {
        initPath(id);
        postInvalidate();
    }

    public void setPaintColor(int color) {
        paint.setColor(color);
    }

    public void setPaintWidth(int w) {
        paint.setStrokeWidth(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(path, paint);
    }
}
