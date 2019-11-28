package com.lupindi.screenrecorder.bean;

import android.graphics.Bitmap;

public class PictureBean {
    private String name;
    private Bitmap bitmap;
    private String path;

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
}
