package com.study.ian.image.customview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.study.ian.image.ImageData;
import com.study.ian.image.ImageDetailActivity;
import com.study.ian.image.R;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private final String TAG = "MyRecyclerViewAdapter";

    public static final String CLICKED_IMG = "CLICKED_IMG";
    private Context context;
    private List<ImageData> imageDataList;
    private List<Integer> imageSelectedList = new ArrayList<>();

    public MyRecyclerViewAdapter(Context context, List<ImageData> list) {
        this.context = context;
        imageDataList = list;
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
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_holder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MorphView imageView = holder.imageView;
        imageView.setPaintColor(Color.argb(0, 0, 0, 0));
        imageView.setCurrentId(R.drawable.vd_image_selected_init);

        // add listener for imageView
        imageView.setOnClickListener(v -> {
            if (position != RecyclerView.NO_POSITION) {
                if (imageSelectedList.size() == 0) {
                    // if there is no selected item then go to ImageDetailActivity
                    Intent intent = new Intent(context, ImageDetailActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putStringArray(CLICKED_IMG, imageDataList.get(position).toArray());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
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
            imageView.setPaintWidth(10);
            imageView.setPaintColor(Color.argb(255, 0, 0, 0));
            imageView.performAnimation(R.drawable.vd_image_selected);
        } else {
            imageView.performAnimation(R.drawable.vd_image_selected_init);
            new Thread(() -> {
                try {
                    Thread.sleep(250);
                    imageView.setPaintColor(Color.argb(0, 0, 0, 0));
                    imageView.setPaintWidth(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Glide.with(context)
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
            int index = imageSelectedList.indexOf(position);
            imageSelectedList.remove(index);

            view.performAnimation(R.drawable.vd_image_selected_init);
            new Thread(() -> {
                try {
                    Thread.sleep(250);
                    view.setPaintColor(Color.argb(0, 0, 0, 0));
                    view.setPaintWidth(0);
                    view.postInvalidate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            imageSelectedList.add(position);

            view.setPaintWidth(10);
            view.setPaintColor(Color.argb(255, 0, 0, 0));
            view.performAnimation(R.drawable.vd_image_selected);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private MorphView imageView;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageHolderView);
        }
    }
}
