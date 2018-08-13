package com.study.ian.image.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class MorphView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "MorphView";

    private final SvgData svgData;
    private final Paint paint = new Paint();
    private DataPath path;
    private int currentId;

    @SuppressWarnings("ClickableViewAccessibility")
    MorphView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);

        svgData = new SvgData(context);

        initPaint();
    }

    private void initPaint() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    private void initPath(int id) {
        path = svgData.getPath(id, this);
        currentId = id;
    }

    public void clearPath() {
        path.reset();
        currentId = 0;
        postInvalidate();
    }

    public void setCurrentId(int id) {
        initPath(id);
        postInvalidate();
    }

    public void setPaintColor(int color) {
        paint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentId != 0) {
            paint.setStrokeWidth(getWidth() / 20);
            path = svgData.getPath(currentId, this);
        }
        canvas.drawPath(path, paint);
    }
}
