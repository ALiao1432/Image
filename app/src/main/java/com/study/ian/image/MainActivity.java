package com.study.ian.image;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.ScaleGestureDetector;

import com.study.ian.image.customview.MyRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: 2018-07-26 add floating button if selected any img

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private final int MY_WRITE_EXTERNAL_REQUEST_CODE = 999;
    private List<ImageData> imgPathList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ScaleGestureDetector scaleGestureDetector;
    private int spanCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (needToAskPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                || needToAskPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showPermissionDialog();
        } else {
//            long startTime = System.nanoTime();
            imgPathList = getAllImgFile();
//            long totalTime = System.nanoTime() - startTime;
//            imgPathList.forEach(s -> Log.d(TAG, "imgData : " + s));
//            Log.d(TAG, "totalTime : " + totalTime / 1000000 + " ms");
//            Log.d(TAG, "total size : " + imgPathList.size());

            findView();
            initDetector();
            setRecyclerView();
        }
    }

    private void findView() {
        recyclerView = findViewById(R.id.recyclerView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setRecyclerView() {
        // set Layout Manager
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        // set Adapter
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, imgPathList);
        recyclerView.setAdapter(myRecyclerViewAdapter);

        // set ItemDecoration
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        // add floating button
        // TODO: 2018-07-27 study FloatingActionButton

        // TODO: 2018-07-30 ViewPager

        // set onTouchListener for recyclerView
        recyclerView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return false;
        });
    }

    private void initDetector() {
        scaleGestureDetector = new ScaleGestureDetector(this, new RecyclerViewScaleDetector());
    }

    private boolean needToAskPermission(String p) {
        return this.checkPermission(p, Process.myPid(), Process.myUid())
                == PackageManager.PERMISSION_DENIED;
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.need_permission)
                .setMessage(R.string.permission_dialog_content)
                .setPositiveButton(R.string.permission_dialog_accept, (dialogInterface, i) -> askPermission())
                .setNegativeButton(R.string.permission_dialog_cancel, (dialogInterface, i) -> finish())
                .setCancelable(false)
                .show();
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_WRITE_EXTERNAL_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_WRITE_EXTERNAL_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                long startTime = System.nanoTime();
                imgPathList = getAllImgFile();
//                long totalTime = System.nanoTime() - startTime;
//                imgPathList.forEach(s -> Log.d(TAG, "imgData : " + s));
//                Log.d(TAG, "totalTime : " + totalTime / 1000000 + " ms");
//                Log.d(TAG, "total size : " + imgPathList.size());

                findView();
                setRecyclerView();
            } else {
                finish();
            }
        }
    }

    private List<ImageData> getAllImgFile() {
        List<ImageData> tempList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projections = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
        };
        String order = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

        try {
            Cursor imageCursor = getContentResolver().query(
                    uri,
                    projections,
                    null,
                    null,
                    order
            );
            if (imageCursor != null) {
                while (imageCursor.moveToNext()) {
                    tempList.add(new ImageData(
                            imageCursor.getString(0),
                            imageCursor.getString(1),
                            imageCursor.getString(2),
                            imageCursor.getString(3),
                            imageCursor.getString(4),
                            imageCursor.getString(5)
                    ));
                }
                imageCursor.close();
            }

            tempList = tempList.stream()
                    .filter(data -> !data.getDisplayName().endsWith(".gif")) // filter out .gif file
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempList;
    }

    private class RecyclerViewScaleDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private final int maxSpanCount = 5;
        private float currentScale;
        private boolean canScale = true;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            currentScale = detector.getScaleFactor();

            if (canScale) {
                if (currentScale < 1f) {
                    if (spanCount < maxSpanCount) {
                        spanCount++;
                        canScale = false;
                    }
                } else if (currentScale > 1f){
                    spanCount--;
                    if (spanCount == 0) {
                        spanCount = 1;
                    }
                    canScale = false;
                }
                gridLayoutManager.setSpanCount(spanCount);
            }
            return super.onScale(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            canScale = true;
        }
    }
}
