package com.lupindi.screenrecorder.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.lupindi.screenrecorder.R;
import com.lupindi.screenrecorder.bean.VideoBean;
import com.lupindi.screenrecorder.utils.MyFormatter;

/**
 *
 * 复用VideoBean
 * // AsyncTask 使用方式:
 * <p/>
 * //1 继承 AsyncTask  ,定义三个泛型， 如果不需要可以用Void代替
 * //2 实现 doInBackground  和 onPostExecute;
 * //3 [可以实现] onPublishProgress 或者 onPreExecute
 * //4 在需要的地方创建出 自定义的 Task对象，调用 execute 方法，传递参数(第一个泛型)
 */
public class VideoAdapter4 extends RecyclerView.Adapter {
    //    List<VideoBean> videoBeans = new ArrayList<>();
    Cursor videoCursor;
    Context context;

    public VideoAdapter4(Context context) {
        this.context = context;

        new VideoTask().execute(context.getContentResolver());
    }

    // 返回RecyclerView的条目数量 ,相当于BaseAdapter的getCount方法
    @Override
    public int getItemCount() {
//        return videoBeans == null ? 0 : videoBeans.size();
        return videoCursor == null ? 0 : videoCursor.getCount();
    }

    // 相当于BaseAdapter的getView方法的前半部分，用来创建出一个 ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.item, null);
        VideoBean videoBean = new VideoBean();
        // 把 VideoBean 放到 VideoViewHolder 中，保证 不重复创建 VideoBean， 复用VideoBean
        VideoViewHolder videoViewHolder = new VideoViewHolder(itemView, videoBean);
        return videoViewHolder;
    }

    // 相当于BaseAdapter的getView方法的后半部分，用来把数据关联到 ViewHolder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

        // 移动cursor到指定行
        videoCursor.moveToPosition(position);
        VideoBean videoBean = videoViewHolder.videoBean;

        videoBean.reUse(videoCursor);

        videoViewHolder.titleTv.setText(videoBean.getName());


    }

    // 因为RecyclerView.ViewHolder是抽象的，不能直接创建对象，必须要继承才行
    private class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView titleTv;
        TextView sizeTv;
        TextView durationTv;
        VideoBean videoBean;

        public VideoViewHolder(View itemView, VideoBean videoBean) {
            super(itemView);
            this.videoBean = videoBean;
            // 把原来在BaseAdapter中的getView方法中写的FVBI 写在这里了
            iv = (ImageView) itemView.findViewById(R.id.item_iv);
            titleTv = (TextView) itemView.findViewById(R.id.item_title_tv);
            sizeTv = (TextView) itemView.findViewById(R.id.item_size_tv);
            durationTv = (TextView) itemView.findViewById(R.id.item_duration_tv);
        }
    }

    private class VideoTask extends AsyncTask<ContentResolver, Void, Cursor> {

        @Override
        protected Cursor doInBackground(ContentResolver... params) {
            ContentResolver contentResolver = params[0];
            Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            int count = cursor.getCount();
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            videoCursor = cursor;
            // 刷新界面
            notifyDataSetChanged();
        }
    }


}
