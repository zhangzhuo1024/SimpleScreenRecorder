package com.lupindi.screenrecorder.adapter;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lupindi.screenrecorder.bean.PictureBean;
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


    public void updateData() {
        getPictureList();
        notifyDataSetChanged();
    }

    public static interface OnItemClickListener {
        void onItemClick(String path);
    }

    //    List<VideoBean> videoBeans = new ArrayList<>();
    Cursor videoCursor;
    Context context;
    List<PictureBean> listPictures = new ArrayList<>();

    public VideoAdapter(Context context) {
        this.context = context;
    }

    // 返回RecyclerView的条目数量 ,相当于BaseAdapter的getCount方法
    @Override
    public int getItemCount() {
        return listPictures == null ? 0 : listPictures.size();
//        return videoCursor == null ? 0 : videoCursor.getCount();
    }

    Map<Integer, String> thumbsMap = new HashMap<>();

    // 相当于BaseAdapter的getView方法的前半部分，用来创建出一个 ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
//        videoCursor.moveToPosition(position);
//        VideoBean videoBean = videoViewHolder.videoBean;
//        videoBean.reUse(videoCursor);
//        videoViewHolder.bind();

        videoViewHolder.iv.setImageBitmap(listPictures.get(position).getBitmap());
        videoViewHolder.titleTv.setText(listPictures.get(position).getName());
    }

    // 对资源进行初始化
    public void init() {
        // 相当于开启了子线程，所以不会阻塞，代码很快就执行完了
//        new VideoTask().execute(context.getContentResolver());
        new VideoMessageTask().execute();
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



    private class VideoMessageTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            getPictureList();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            notifyDataSetChanged();
        }
    }

    private void getPictureList() {
        listPictures.clear();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "LuPinDi");
        //判断文件夹是否存在，如果不存在就创建一个
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            long length = files[i].length();
            PictureBean picture = new PictureBean();
            picture.setBitmap(getVideoThumbnail(files[i].getPath(), 200, 200, MediaStore.Images.Thumbnails.MICRO_KIND));
            picture.setPath(files[i].getPath());
            picture.setName(files[i].getName());
            listPictures.add(picture);
        }
    }

    //获取视频的缩略图
    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
//            System.out.println("w"+bitmap.getWidth());
//            System.out.println("h"+bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
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
                onItemClickListener.onItemClick(listPictures.get(position).getPath());
            }
        }
    };


}
