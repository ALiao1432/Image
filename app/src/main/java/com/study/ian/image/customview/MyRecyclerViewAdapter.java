package com.study.ian.image.customview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.study.ian.image.ImageData;
import com.study.ian.image.ImageDetailActivity;
import com.study.ian.image.OnSelectedItemCallback;
import com.study.ian.image.R;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private final String TAG = "MyRecyclerViewAdapter";

    public static final String CLICKED_IMG = "CLICKED_IMG";
    private final Context context;
    private List<ImageData> imageDataList;
    private final List<Integer> imageSelectedList = new ArrayList<>();
    private final OnSelectedItemCallback onSelectedItemCallback;

    public MyRecyclerViewAdapter(Context context, List<ImageData> list) {
        this.context = context;
        imageDataList = list;
        onSelectedItemCallback = (OnSelectedItemCallback) context;
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

                    bundle.putParcelable(CLICKED_IMG, imageDataList.get(position));
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
            imageView.setPaintColor(Color.argb(255, 0, 0, 0));
            imageView.setCurrentId(R.drawable.vd_image_selected);
        } else {
            imageView.clearPath();
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
