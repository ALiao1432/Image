package com.study.ian.image;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageData implements Parcelable{

    private String data;
    private String displayName;
    private String dateAdd;
    private String dataSize;
    private String width;
    private String height;
    private String type;

    ImageData(String data, String displayName, String dateAdd, String dataSize, String width, String height, String type) {
        this.data = data;
        this.displayName = displayName;
        this.dateAdd = dateAdd;
        this.dataSize = dataSize;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    protected ImageData(Parcel in) {
        data = in.readString();
        displayName = in.readString();
        dateAdd = in.readString();
        dataSize = in.readString();
        width = in.readString();
        height = in.readString();
        type = in.readString();
    }

    public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel in) {
            return new ImageData(in);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeString(displayName);
        dest.writeString(dateAdd);
        dest.writeString(dataSize);
        dest.writeString(width);
        dest.writeString(height);
        dest.writeString(type);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
