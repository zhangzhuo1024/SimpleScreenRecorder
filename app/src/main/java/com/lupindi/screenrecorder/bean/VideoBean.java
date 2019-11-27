package com.lupindi.screenrecorder.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by Teacher on 2016/6/29.
 */
public class VideoBean implements Serializable{
    private int id;
    private String path;
    private String name;
    private long size;
    private long duration;

    public void reUse(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
        size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
        duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

    }

    public String getPath() {
        return path;
    }


    public String getName() {
        return name;
    }


    public long getSize() {
        return size;
    }


    public long getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "VideoBean{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                '}';
    }
}
