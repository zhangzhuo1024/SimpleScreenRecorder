package com.lupindi.screenrecorder.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.lupindi.screenrecorder.R;
import com.lupindi.screenrecorder.bean.VideoBean;

/**
 * 使用 AsyncTask 异步加载数据 Cursor查询数据，并封装集合
 */
public class VideoAdapter2 extends RecyclerView.Adapter {
    List<VideoBean> videoBeans = new ArrayList<>();

    public VideoAdapter2(Context context) {

        new VideoTask().execute(context.getContentResolver());
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
        List<Object> lists = new ArrayList<>();


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
    // AsyncTask 使用方式:

    //1 继承 AsyncTask  ,定义三个泛型， 如果不需要可以用Void代替
//2 实现 doInBackground  和 onPostExecute;
//3 [可以实现] onPublishProgress 或者 onPreExecute
//4 在需要的地方创建出 自定义的 Task对象，调用 execute 方法，传递参数(第一个泛型)
    private class VideoTask extends AsyncTask<ContentResolver, Void, List<VideoBean>> {

        @Override
        protected List<VideoBean> doInBackground(ContentResolver... params) {
            ContentResolver contentResolver = params[0];
            Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            int count = cursor.getCount();
            List<VideoBean> taskList = new ArrayList<>();
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
                taskList.add(videoBean);
            }
            cursor.close();

            return taskList;
        }

        @Override
        protected void onPostExecute(List<VideoBean> taskList) {
            videoBeans = taskList;
            // 刷新界面
            notifyDataSetChanged();
        }
    }
}
