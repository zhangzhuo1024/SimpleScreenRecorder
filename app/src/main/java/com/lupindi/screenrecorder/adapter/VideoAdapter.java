package com.lupindi.screenrecorder.adapter;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lupindi.screenrecorder.R;
import com.lupindi.screenrecorder.bean.VideoBean;
import com.lupindi.screenrecorder.utils.MyFormatter;

/**
 * // AsyncTask 使用方式:
 * <p/>
 * //1 继承 AsyncTask  ,定义三个泛型， 如果不需要可以用Void代替
 * //2 实现 doInBackground  和 onPostExecute;
 * //3 [可以实现] onPublishProgress 或者 onPreExecute
 * //4 在需要的地方创建出 自定义的 Task对象，调用 execute 方法，传递参数(第一个泛型)
 */
public class VideoAdapter extends RecyclerView.Adapter {


    public static interface OnItemClickListener {
        void onItemClick(View v, int position, VideoBean videoBean);
    }

    //    List<VideoBean> videoBeans = new ArrayList<>();
    Cursor videoCursor;
    Context context;


    public VideoAdapter(Context context) {
        this.context = context;
    }

    // 返回RecyclerView的条目数量 ,相当于BaseAdapter的getCount方法
    @Override
    public int getItemCount() {

        return videoCursor == null ? 0 : videoCursor.getCount();
    }

    Map<Integer, String> thumbsMap = new HashMap<>();

    // 相当于BaseAdapter的getView方法的前半部分，用来创建出一个 ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = View.inflate(parent.getContext(), R.layout.item, null);


        // 把 R.layout.item 布局资源把变成一个 View，然后填充的 parent 中
//        View.inflate(parent.getContext(), R.layout.item, parent);
//        等价的
//        layoutInflater.inflate(R.layout.item, parent);
//        等价的
//        layoutInflater.inflate(R.layout.item, parent,true);

// // 把 R.layout.item 布局资源把变成一个 View ，这个View是孤立的（没有parent），后续会自行把填充好的View添加到某个parent中
//        View.inflate(parent.getContext(), R.layout.item, null);
//        等价的
//        layoutInflater.inflate(R.layout.item, null);
//        等价的
//        layoutInflater.inflate(R.layout.item, null,true/false);

        // 问题：在R.layout.item 最外层控件中写了一些 layout_ 开头的属性就丢失了
        // 原因是 解析布局xml时，会把 控件变成View对象，会把 layout_ 开头的属性 变成LayoutParams，但LayoutParams有很多种
        // 如果不确定父控件是什么，就不知道转成什么类型的 LayoutParams ，所以会把这些 layout_ 开头的属性  丢弃掉

        // 解决： 使用layoutInflater.inflate 三个参数的方法，给定parent，但第三个参数传递false，表示不添加到容器中，
        // 但需要 把 layout_ 开头的属性 变成 parent 对应的类型的 LayoutParams

        // 第三个参数 一般是false，比如在ListView的adapter RecyclerView的adapter 、Fragment（onCreateView）
        // 如果传true的话我们直接使用2个参数的方法了

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.item, parent, false);

        VideoBean videoBean = new VideoBean();
        // 把 VideoBean 放到 VideoViewHolder 中，保证 不重复创建 VideoBean， 复用VideoBean
        VideoViewHolder videoViewHolder = new VideoViewHolder(itemView, videoBean);

        // 把videoViewHolder 作为tag设置给itemView，便于在点击后通过view获得到 videoViewHolder，进而去拿到position及VideoBean
        itemView.setOnClickListener(itemOcl);
        itemView.setTag(videoViewHolder);

        return videoViewHolder;
    }

    // 相当于BaseAdapter的getView方法的后半部分，用来把数据关联到 ViewHolder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

        // 移动cursor到指定行
        videoCursor.moveToPosition(position);
        VideoBean videoBean = videoViewHolder.videoBean;
        videoBean.reUse(videoCursor);

        videoViewHolder.bind();

//      这样做会有多个点击监听对象，不好！
//        videoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(onItemClickListener!=null){
//
//                    onItemClickListener.onItemClick(v,position,videoViewHolder.videoBean);
//                }
//            }
//        });
    }

    // 对资源进行初始化
    public void init() {
        // 相当于开启了子线程，所以不会阻塞，代码很快就执行完了
        new VideoTask().execute(context.getContentResolver());
    }

    // 对资源进行释放
    public void release() {
        if (videoCursor != null) {
            videoCursor.close();
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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

        public void bind() {
            titleTv.setText(videoBean.getName());
            sizeTv.setText(Formatter.formatFileSize(context, videoBean.getSize()));
            durationTv.setText(MyFormatter.formatDuration(videoBean.getDuration()));


            String thumbPath = thumbsMap.get(videoBean.getId());


//            if (thumbPath == null) {
//                iv.setImageResource(R.mipmap.ic_launcher);
//            } else {
////            Bitmap bitmap = BitmapFactory.decodeFile(thumbPath);
////            videoViewHolder.iv.setImageBitmap(bitmap);
//
//                Picasso.with(context).load(new File(thumbPath)).into(iv);
//            }
            // 通用的，通过Thumbnails去解析视频文件， 实时获得缩略图

            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                    context.getContentResolver(),
                    videoBean.getId(),          // 视频的id
                    MediaStore.Video.Thumbnails.MINI_KIND, // 缩略图的类型
                    new BitmapFactory.Options()         // 图片选项
            );

            iv.setImageBitmap(bitmap);

        }
    }

    public VideoBean getVideoBean(int position) {
        videoCursor.moveToPosition(position);
        VideoBean  videoBean = new VideoBean();
        videoBean.reUse(videoCursor);
        return videoBean;
    }

    private class VideoTask extends AsyncTask<ContentResolver, Void, Cursor> {

        @Override
        protected Cursor doInBackground(ContentResolver... params) {
            ContentResolver contentResolver = params[0];
            Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

            Cursor thumbCursor = contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, null, null, null, null);
            while (thumbCursor.moveToNext()) {
                int id = thumbCursor.getInt(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.VIDEO_ID));
                String thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                thumbsMap.put(id, thumbPath);
            }
            thumbCursor.close();

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            videoCursor = cursor;
            // 刷新界面
            notifyDataSetChanged();
        }
    }

    private View.OnClickListener itemOcl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                VideoViewHolder videoViewHolder = (VideoViewHolder) v.getTag();
                // AdapterPosition 获取adapter中的角标
                // LayoutPosition  获得被显示的角标
                int position = videoViewHolder.getAdapterPosition();
                onItemClickListener.onItemClick(v, position, videoViewHolder.videoBean);
            }
        }
    };


}
