package com.lupindi.screenrecorder.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lupindi.screenrecorder.MainActivity;
import com.lupindi.screenrecorder.R;
import com.lupindi.screenrecorder.bean.PictureBean;
import com.lupindi.screenrecorder.bean.VideoBean;

/**
 * 使用集合封装从Cursor中查询的数据
 */
public class VideoAdapter1 extends RecyclerView.Adapter {

    public static interface OnItemClickListener {
        void onItemClick(View v, int position, VideoBean videoBean);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    List<VideoBean> videoBeans = new ArrayList<>();

    List<PictureBean> listPictures = new ArrayList<>();
    public VideoAdapter1(Context context) {


        String video_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/LuPinDi";
        Log.i("@@@", video_path);
        File file = new File(video_path);
        //判断文件夹是否存在，如果不存在就创建一个
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            PictureBean picture = new PictureBean();
            picture.setBitmap(getVideoThumbnail(files[i].getPath(), 200, 200, MediaStore.Images.Thumbnails.MICRO_KIND));
            picture.setPath(files[i].getPath());
            picture.setName(files[i].getName());
            listPictures.add(picture);
        }
        // listView = (ListView) findViewById(R.id.lv_show);




//        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
//        int count = cursor.getCount();
//        while (cursor.moveToNext()) {
//            VideoBean videoBean = new VideoBean();
//            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
//            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
//            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
//            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
//
//            videoBean.setPath(path);
//            videoBean.setName(name);
//            videoBean.setSize(size);
//            videoBean.setDuration(duration);
//            videoBeans.add(videoBean);
//        }
//        cursor.close();



//        Log.d("count", "" + count);

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

    // 返回RecyclerView的条目数量 ,相当于BaseAdapter的getCount方法
    @Override
    public int getItemCount() {
//        return videoBeans == null ? 0 : videoBeans.size();
        return listPictures == null? 0:listPictures.size();
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
//        VideoBean videoBean = videoBeans.get(position);
//        videoViewHolder.titleTv.setText(videoBean.getName());


        PictureBean pictureBean = listPictures.get(position);
        videoViewHolder.titleTv.setText(pictureBean.getName());
        videoViewHolder.iv.setImageBitmap(pictureBean.getBitmap());
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
