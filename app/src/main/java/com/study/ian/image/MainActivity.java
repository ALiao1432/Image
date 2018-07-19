package com.study.ian.image;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private final int MY_WRITE_EXTERNAL_REQUEST_CODE = 999;
    private List<String> imgPathList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (needToAskPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                || needToAskPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showPermissionDialog();
        } else {
            long startTime = System.nanoTime();
            getAllImgFile();
            long totalTime = System.nanoTime() - startTime;
            imgPathList.forEach(s -> Log.d(TAG, "imgPath : " + s));
            Log.d(TAG, "totalTime : " + totalTime / 1000000 + " ms");
            Log.d(TAG, "total size : " + imgPathList.size());
        }
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
                getAllImgFile();
            } else {
                finish();
            }
        }
    }

    private void getAllImgFile() {
        List<File> sdFilePathList = Arrays.asList(Environment.getExternalStorageDirectory().listFiles());
        sdFilePathList.forEach(this::getChildDirFile);
    }

    private void getChildDirFile(File file) {
        if (!file.isHidden()) {
            if (file.isDirectory() && hasChildDirectory(file)) {
                Arrays.stream(file.listFiles()).forEach(this::getChildDirFile);
            } else if (file.isFile() && isJPGorPNG(file)) {
                imgPathList.add(file.getPath());
            }
        }
    }

    private boolean isJPGorPNG(File file) {
        String s = file.getPath();
        return s.endsWith(".jpg") || s.endsWith(".png");
    }

    private boolean hasChildDirectory(File file) {
        return Arrays.asList(file.listFiles()).size() != 0;
    }
}
