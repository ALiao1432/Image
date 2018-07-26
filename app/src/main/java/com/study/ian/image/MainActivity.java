package com.study.ian.image;

import android.Manifest;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.study.ian.image.view.MyAdapter;

import java.util.ArrayList;
import java.util.List;

// TODO: 2018-07-26 add floating button if selected any img

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private final int MY_WRITE_EXTERNAL_REQUEST_CODE = 999;
    private List<ImageData> imgPathList = new ArrayList<>();
    private RecyclerView recyclerView;

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
            setRecyclerView();
        }
    }

    private void findView() {
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setRecyclerView() {
        // set Layout Manager
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        // set Adapter
        MyAdapter myAdapter = new MyAdapter(this, imgPathList);
        recyclerView.setAdapter(myAdapter);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempList;
    }
}
