package com.study.ian.image;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;

import com.study.ian.image.customview.MyCardView;
import com.study.ian.image.customview.MyRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements OnSelectedItemCallback {

    private final String TAG = "MainActivity";
    private final int MY_WRITE_EXTERNAL_REQUEST_CODE = 999;

    private List<ImageData> imgPathList = new ArrayList<>();
    private List<Integer> selectedList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ScaleGestureDetector scaleGestureDetector;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private LayoutAnimationController controller;
    private MyCardView actionCardView;
    private Button shareButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (needToAskPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                || needToAskPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showPermissionDialog();
        } else {
            getAllImgFile();

            findView();
            initDetector();
            initView();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (getAllImgFile()) {
            myRecyclerViewAdapter.updateData(imgPathList);
            recyclerView.scheduleLayoutAnimation();
        }
    }

    private void findView() {
        recyclerView = findViewById(R.id.recyclerView);
        actionCardView = findViewById(R.id.actionCardView);
        shareButton = actionCardView.findViewById(R.id.shareButton);
        deleteButton = actionCardView.findViewById(R.id.deleteButton);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        // set Layout Manager
        gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        // set Adapter
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, imgPathList);
        recyclerView.setAdapter(myRecyclerViewAdapter);

        // set ItemDecoration
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getPointerCount() == 2) {
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }
            return false;
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // scroll down
                    myRecyclerViewAdapter.setViewScrollDown(true);
                } else {
                    // scroll up
                    myRecyclerViewAdapter.setViewScrollDown(false);
                }
            }
        });

        // set animation
        setRecyclerViewAnimation();

        actionCardView.setVisibility(View.INVISIBLE);
        actionCardView.ANIMATOR_DURATION = 200;
        actionCardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                actionCardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                actionCardView.cx = actionCardView.getWidth();
                actionCardView.cy = actionCardView.getHeight();
            }
        });

        shareButton.setOnClickListener(v -> shareImage());

        deleteButton.setOnClickListener(v -> showConfirmDialog());
    }

    private void setRecyclerViewAnimation() {
        controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_up_2);
        recyclerView.setLayoutAnimation(controller);
        myRecyclerViewAdapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
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
                recreate();
            } else {
                finish();
            }
        }
    }

    private boolean getAllImgFile() {
        List<ImageData> tempList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projections = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.MIME_TYPE
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
                            imageCursor.getString(5),
                            imageCursor.getString(6)
                    ));
                }
                imageCursor.close();
            }

            imgPathList = tempList.stream()
                    .filter(data -> !data.getDisplayName().endsWith(".gif")) // filter out .gif file
                    .collect(Collectors.toList());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (myRecyclerViewAdapter.isAnySelected()) {
            myRecyclerViewAdapter.clearSelected();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSelectedListener(List<Integer> selectedList) {
        this.selectedList = selectedList;

        if (selectedList.size() == 0) {
            actionCardView.closeCardView();
        } else if (!actionCardView.isOpen()) {
            actionCardView.openCardView();
        }
    }

    private void shareImage() {
        Intent shareIntent = new Intent();
        ArrayList<Uri> uriArrayList = new ArrayList<>();
        String type = imgPathList.get(selectedList.get(0)).getType();

        selectedList.forEach(i -> {
            File file = new File(imgPathList.get(i).getData());
            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
            uriArrayList.add(uri);
        });

        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriArrayList);
        shareIntent.setType(type);
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_to)));

        myRecyclerViewAdapter.clearSelected();
    }

    private void deleteImage() {
        selectedList.forEach(i -> {
            File file = new File(imgPathList.get(i).getData());
            String[] selectionArgs = new String[]{file.getPath()};
            getContentResolver().delete(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.Media.DATA + "=?",
                    selectionArgs
            );
        });

        if (getAllImgFile()) {
            myRecyclerViewAdapter.updateData(imgPathList);
        }
        myRecyclerViewAdapter.clearSelected();
    }

    private void showConfirmDialog() {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(R.string.confirm_dialog_title)
                .setPositiveButton(R.string.confirm_dialog_ok, (dialogInterface, i) -> deleteImage())
                .setNegativeButton(R.string.confirm_dialog_no, (dialogInterface, i) -> {
                })
                .setCancelable(false)
                .show();
    }

    private void setLayoutAnimationResource(int spanCount) {
        switch (spanCount) {
            case 1:
                controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_up_1);
                break;
            case 2:
                controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_up_2);
                break;
            case 3:
                controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_up_3);
                break;
            case 4:
                controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_up_4);
                break;
        }
        recyclerView.setLayoutAnimation(controller);
    }

    private class RecyclerViewScaleDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private final int maxSpanCount = 4;
        private float currentScale;
        private boolean canScale = true;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            currentScale = detector.getScaleFactor();

            if (canScale && currentScale != 1) {
                if (currentScale > 1 && gridLayoutManager.getSpanCount() != 1) {
                    gridLayoutManager.setSpanCount(Math.max(gridLayoutManager.getSpanCount() - 1, 1));
                    setLayoutAnimationResource(gridLayoutManager.getSpanCount());
                    myRecyclerViewAdapter.notifyItemChanged(0); // i don't know why...
                } else if ((currentScale < 1 && gridLayoutManager.getSpanCount() != maxSpanCount)) {
                    gridLayoutManager.setSpanCount(Math.min(gridLayoutManager.getSpanCount() + 1, maxSpanCount));
                    setLayoutAnimationResource(gridLayoutManager.getSpanCount());
                    myRecyclerViewAdapter.notifyItemChanged(0); // i don't know why...
                }
                canScale = false;
            }
            return super.onScale(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            canScale = true;
        }
    }
}
