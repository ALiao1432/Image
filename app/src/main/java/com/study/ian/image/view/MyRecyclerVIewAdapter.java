package com.study.ian.image.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.study.ian.image.ImageData;
import com.study.ian.image.ImageDetailActivity;
import com.study.ian.image.R;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerVIewAdapter extends RecyclerView.Adapter<MyRecyclerVIewAdapter.MyViewHolder> {

    private final String TAG = "MyRecyclerVIewAdapter";

    public static final String CLICKED_IMG = "CLICKED_IMG";
    private Context context;
    private List<ImageData> imageDataList;
    private List<Integer> imageSelectedList = new ArrayList<>();

    public MyRecyclerVIewAdapter(Context context, List<ImageData> list) {
        this.context = context;
        imageDataList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_holder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageView imageView = holder.imageView;

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
                    if (imageSelectedList.contains(position)) {
                        int index = imageSelectedList.indexOf(position);
                        imageSelectedList.remove(index);
                        imageView.setAlpha(1f);
                    } else {
                        imageSelectedList.add(position);
                        imageView.setAlpha(0.5f);
                    }
                }
            }
        });
        imageView.setOnLongClickListener(v -> {
            if (imageSelectedList.contains(position)) {
                int index = imageSelectedList.indexOf(position);
                imageSelectedList.remove(index);
                imageView.setAlpha(1f);
            } else {
                imageSelectedList.add(position);
                imageView.setAlpha(0.5f);
            }
            return true;
        });

        if (imageSelectedList.contains(position)) {
            imageView.setAlpha(0.5f);
        } else {
            imageView.setAlpha(1f);
        }

        Glide.with(context)
                .load(imageDataList.get(position).getData())
                .placeholder(R.color.colorNavigationBar)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageHolderView);
        }
    }
}