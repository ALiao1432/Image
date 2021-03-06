package com.study.ian.image.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.study.ian.image.BuildConfig;
import com.study.ian.image.ImageData;
import com.study.ian.image.ImageDetailActivity;
import com.study.ian.image.R;

import java.io.File;
import java.util.List;

public class MyDetailCardView extends MyCardView {

    private final String TAG = "MyDetailCardView";

    private final int colorIndex = 1;
    private ConstraintLayout constraintLayout;
    private TextView infoTextView;
    private TextView pathTextView;
    private TextView dataSizeTextView;
    private TextView sizeTextView;
    private ImageView closeImageView;
    private ImageView shareImageView;
    private ImageView deleteImageView;
    private ImageData imageData;
    private final Context context;
    private List<Palette.Swatch> swatchList;

    @SuppressLint("ClickableViewAccessibility")
    private final OnTouchListener onTouchListener = (v, e) -> {
        switch (v.getId()) {
            case R.id.shareImageView:
                if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    shareImageView.setImageTintList(ColorStateList.valueOf(swatchList.get(colorIndex).getTitleTextColor()));
                } else if (e.getActionMasked() == MotionEvent.ACTION_UP) {
                    shareImageView.setImageTintList(ColorStateList.valueOf(swatchList.get(colorIndex).getBodyTextColor()));
                    shareImage();
                }
                break;
            case R.id.deleteImageView:
                if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    deleteImageView.setImageTintList(ColorStateList.valueOf(swatchList.get(colorIndex).getTitleTextColor()));
                } else if (e.getActionMasked() == MotionEvent.ACTION_UP) {
                    deleteImageView.setImageTintList(ColorStateList.valueOf(swatchList.get(colorIndex).getBodyTextColor()));
                    showConfirmDialog();
                }
                break;
            case R.id.closeImageView:
                if (e.getActionMasked() == MotionEvent.ACTION_UP) {
                    closeCardView();
                }
                break;
        }
        return true;
    };

    public MyDetailCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    private void setCardView() {
        this.cx = getWidth() / 2;
        this.cy = 0;
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
        this.swatchList = swatchList;

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
        this.imageData = imageData;

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
        setCardView();
    }

    public void getColorViaPalette(Drawable drawable) {
        Bitmap bitmap = drawableToBitmap(drawable);
        Palette palette = Palette.from(bitmap).generate();
        List<Palette.Swatch> list = palette.getSwatches();

        this.setCardBackgroundColor(list.get(colorIndex).getRgb());

        findView();
        setView(list);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
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

    private void showConfirmDialog() {
        new android.support.v7.app.AlertDialog.Builder(context)
                .setTitle(R.string.confirm_dialog_title)
                .setPositiveButton(R.string.confirm_dialog_ok, (dialogInterface, i) -> deleteImage())
                .setNegativeButton(R.string.confirm_dialog_no, (dialogInterface, i) -> {
                })
                .setCancelable(false)
                .show();
    }

    private void shareImage() {
        Intent shareIntent = new Intent();
        File file = new File(imageData.getData());
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);

        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType(imageData.getType());
        context.startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_to)));
    }

    private void deleteImage() {
        File file = new File(imageData.getData());
        String[] selectionArgs = new String[]{file.getPath()};
        context.getContentResolver().delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.Media.DATA + "=?",
                selectionArgs
        );

        ((ImageDetailActivity) context).finish();
    }
}
