package com.study.ian.image.customview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.study.ian.image.ImageData;
import com.study.ian.image.ImageDetailActivity;
import com.study.ian.image.MainActivity;
import com.study.ian.image.OnSelectedItemCallback;
import com.study.ian.image.R;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private final String TAG = "MyRecyclerViewAdapter";

    public static final String CLICKED_IMG = "CLICKED_IMG";
    private final Activity activity;
    private final List<Integer> imageSelectedList = new ArrayList<>();
    private final OnSelectedItemCallback onSelectedItemCallback;
    private List<ImageData> imageDataList;
    private boolean isViewScrollDown = true;

    public MyRecyclerViewAdapter(Activity activity, List<ImageData> list) {
        this.activity = activity;
        imageDataList = list;
        onSelectedItemCallback = (OnSelectedItemCallback) activity;
    }

    public void updateData(List<ImageData> imageDataList) {
        this.imageDataList = imageDataList;
        notifyDataSetChanged();
    }

    public boolean isAnySelected() {
        return imageSelectedList.size() != 0;
    }

    public void clearSelected() {
        imageSelectedList.clear();
        onSelectedItemCallback.onSelectedListener(imageSelectedList);
        notifyDataSetChanged();
    }

    public void setViewScrollDown(boolean scrollDown) {
        this.isViewScrollDown = scrollDown;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.recyclerview_holder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MorphView imageView = holder.imageView;
        imageView.setPaintColor(Color.argb(0, 0, 0, 0));
        imageView.setCurrentId(R.drawable.vd_image_selected_init);
        if (isViewScrollDown) {
            imageView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.item_animation_up_2));
        }

        // add listener for imageView
        imageView.setOnClickListener(v -> {
            if (position != RecyclerView.NO_POSITION) {
                if (imageSelectedList.size() == 0) {
                    // if there is no selected item then go to ImageDetailActivity
                    Intent intent = new Intent(activity, ImageDetailActivity.class);
                    Bundle bundle = new Bundle();
                    ActivityOptionsCompat activityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, "MyImage");

                    bundle.putParcelable(CLICKED_IMG, imageDataList.get(position));
                    intent.putExtras(bundle);
                    activity.startActivity(intent, activityOptionsCompat.toBundle());
                } else {
                    // if selected one item then user will be able to start quick select
                    setSelectedImage(imageView, position);
                }
            }
        });
        imageView.setOnLongClickListener(v -> {
            setSelectedImage(imageView, position);
            return true;
        });

        if (imageSelectedList.contains(position)) {
            imageView.setPaintColor(Color.argb(255, 0, 0, 0));
            imageView.setCurrentId(R.drawable.vd_image_selected);
        } else {
            imageView.clearPath();
        }

        Glide.with(activity)
                .load(imageDataList.get(position).getData())
                .apply(new RequestOptions().centerCrop())
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    private void setSelectedImage(MorphView view, int position) {
        if (imageSelectedList.contains(position)) {
            imageSelectedList.remove(imageSelectedList.indexOf(position));

            view.clearPath();
        } else {
            imageSelectedList.add(position);

            view.setPaintColor(Color.argb(255, 0, 0, 0));
            view.setCurrentId(R.drawable.vd_image_selected);
        }
        onSelectedItemCallback.onSelectedListener(imageSelectedList);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final MorphView imageView;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageHolderView);
        }
    }
}
