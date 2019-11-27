package com.lupindi.screenrecorder.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.lupindi.screenrecorder.MainActivity;
import com.lupindi.screenrecorder.R;
import com.lupindi.screenrecorder.bean.VideoBean;

/**
 * 使用集合封装从Cursor中查询的数据
 */
public class VideoAdapter1 extends RecyclerView.Adapter {
    List<VideoBean> videoBeans = new ArrayList<>();
    public VideoAdapter1(Context context) {

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        int count = cursor.getCount();

        while (cursor.moveToNext()) {
            VideoBean videoBean = new VideoBean();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

            videoBean.setPath(path);
            videoBean.setName(name);
            videoBean.setSize(size);
            videoBean.setDuration(duration);
            videoBeans.add(videoBean);
        }
        cursor.close();



        Log.d("count", "" + count);

    }

    // 返回RecyclerView的条目数量 ,相当于BaseAdapter的getCount方法
    @Override
    public int getItemCount() {
        return videoBeans == null ? 0 : videoBeans.size();
    }

    // 相当于BaseAdapter的getView方法的前半部分，用来创建出一个 ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.item, null);
        VideoViewHolder videoViewHolder = new VideoViewHolder(itemView);
        return videoViewHolder;
    }

    // 相当于BaseAdapter的getView方法的后半部分，用来把数据关联到 ViewHolder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
        VideoBean videoBean = videoBeans.get(position);
        videoViewHolder.titleTv.setText(videoBean.getName());

    }

    // 因为RecyclerView.ViewHolder是抽象的，不能直接创建对象，必须要继承才行
    private class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView titleTv;
        TextView sizeTv;
        TextView durationTv;

        public VideoViewHolder(View itemView) {
            super(itemView);
            // 把原来在BaseAdapter中的getView方法中写的FVBI 写在这里了
            iv = (ImageView) itemView.findViewById(R.id.item_iv);
            titleTv = (TextView) itemView.findViewById(R.id.item_title_tv);
            sizeTv = (TextView) itemView.findViewById(R.id.item_size_tv);
            durationTv = (TextView) itemView.findViewById(R.id.item_duration_tv);
        }
    }

}
