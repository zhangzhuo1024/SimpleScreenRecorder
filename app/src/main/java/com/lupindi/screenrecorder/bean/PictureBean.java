package com.lupindi.screenrecorder.bean;

import android.graphics.Bitmap;

public class PictureBean {
    private String name;
    private Bitmap bitmap;
    private String path;
    private String size;
    private String duration;

    public void setBitmap(Bitmap videoThumbnail) {
        this.bitmap = videoThumbnail;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getPath() {
        return path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
