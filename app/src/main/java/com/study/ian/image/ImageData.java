package com.study.ian.image;

public class ImageData {

    private String data;
    private String displayName;
    private String dataAdd;
    private String dateModified;
    private String width;
    private String height;

    ImageData(String data, String displayName, String dataAdd, String dateModified, String width, String height) {
        this.data = data;
        this.displayName = displayName;
        this.dataAdd = dataAdd;
        this.dateModified = dateModified;
        this.width = width;
        this.height = height;
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

    public String getDataAdd() {
        return dataAdd;
    }

    public void setDataAdd(String dataAdd) {
        this.dataAdd = dataAdd;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
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

    @Override
    public String toString() {
        return "ImageData{" +
                "data='" + data + '\'' +
                ", displayName='" + displayName + '\'' +
                ", dataAdd='" + dataAdd + '\'' +
                ", dateModified='" + dateModified + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                '}';
    }

    public String[] toArray() {
        return new String[]{
                getData(),
                getDisplayName(),
                getDataAdd(),
                getDateModified(),
                getWidth(),
                getHeight()
        };
    }
}
