package com.lupindi.screenrecorder.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lupindi.screenrecorder.R;
import com.lupindi.screenrecorder.utils.MyFormatter;

/**
 * Created by Teacher on 2016/6/30.
 */
public class MyMediaController extends FrameLayout {


    private ImageView nextIv;
    private ImageView prevIv;
    private ImageView exitIv;
    private ViewGroup bottomControllers;
    private ViewGroup topControllers;


    public static interface OnKeyClickListener {
        void onBackClick();

        void onNextClick();

        void onPrevClick();
    }

    private VideoView videoView;
    private TextView titleTv;
    private TextView timeTv;
    private ImageView batteryIv;
    private AudioManager audioManager;
    private SeekBar volumeSb;
    private ImageView muteIv;
    private SeekBar positionSb;
    private ImageView playPauseIv;
    private TextView totalTimeTv;
    private TextView passedTimeTv;
    private ImageView switchScreenIv;
    private OnKeyClickListener onKeyClickListener;


    public MyMediaController(Context context) {
        this(context, null);
//        init();
    }

    public MyMediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
//        init();
    }

    public MyMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.controller_layout, this);

        titleTv = (TextView) findViewById(R.id.controller_title_tv);
        timeTv = (TextView) findViewById(R.id.controller_time_tv);
        batteryIv = (ImageView) findViewById(R.id.controller_battery_iv);


        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        volumeSb = (SeekBar) findViewById(R.id.controller_volume_sb);
        muteIv = (ImageView) findViewById(R.id.controller_mute_iv);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeSb.setMax(maxVolume);

//        int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        volumeSb.setProgress(current);
//        if(current == 0){
//            muteIv.setImageResource(R.drawable.volume_off);
//        }else{
//            muteIv.setImageResource(R.drawable.volume_up);
//        }
        volumeReceiver.onReceive(null, null);
        volumeSb.setOnSeekBarChangeListener(volumeSbListener);
        muteIv.setOnClickListener(muteOcl);

        totalTimeTv = (TextView) findViewById(R.id.controller_total_time);
        passedTimeTv = (TextView) findViewById(R.id.controller_passed_time);
        positionSb = (SeekBar) findViewById(R.id.controller_position_sb);
        positionSb.setOnSeekBarChangeListener(positionSbListener);

        playPauseIv = (ImageView) findViewById(R.id.controller_play_iv);
        playPauseIv.setOnClickListener(playPauseOcl);


        switchScreenIv = (ImageView) findViewById(R.id.controller_switch_screen_iv);
        switchScreenIv.setOnClickListener(switchScreenOcl);

        nextIv = (ImageView) findViewById(R.id.controller_next_iv);
        prevIv = (ImageView) findViewById(R.id.controller_prev_iv);
        exitIv = (ImageView) findViewById(R.id.controller_exit_iv);

        nextIv.setOnClickListener(callbackOcl);
        prevIv.setOnClickListener(callbackOcl);
        exitIv.setOnClickListener(callbackOcl);

        bottomControllers = (ViewGroup) findViewById(R.id.controller_bottom);
        topControllers = (ViewGroup) findViewById(R.id.controller_top);
    }

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
        // videoView 在 setVideoURI 会异步准备， 在准备好了之后，不会调用start方法（会进行判断）,
        // 所以需要额外对VideoView设置准备好了的监听，让我们自己来控制开启播放

        // setVideoURI (1)--> setVideoURI(2) -> openVideo() -> mediaPlayer.setDataSource()
        // -> mediaPlayer.prepareAsync-> PlayActivity.this.preparedListener.onPrepared()->mediaPlayer.start()
        videoView.setOnPreparedListener(preparedListener);
        videoView.setOnCompletionListener(completionListener);
    }

    public void setHasNextAndPrev(boolean hasNext, boolean hasPrev){
        nextIv.setEnabled(hasNext);
        prevIv.setEnabled(hasPrev);
    }

    public void setOnKeyClickListener(OnKeyClickListener onKeyClickListener) {
        this.onKeyClickListener = onKeyClickListener;
    }

    public void onStart() {
        // 开启定时任务去更新时间
        handler.post(updateTimeRunnable);


        IntentFilter batteryFilter = new IntentFilter();
        // 电量变化 , 是一个粘性广播，一注册就会收到一次 最近的一次的广播内容
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        getContext().registerReceiver(batteryReceiver, batteryFilter);


        IntentFilter volumeFilter = new IntentFilter();

        volumeFilter.addAction("android.media.VOLUME_CHANGED_ACTION"); //AudioManager.VOLUME_CHANGED_ACTION
        getContext().registerReceiver(volumeReceiver, volumeFilter);


//        handler.post(updatePositionRunnable);
    }

    public void onStop() {
        // 结束更新时间的定时任务
        //removeCallbacks 移除将要执行的任务
        handler.removeCallbacks(updateTimeRunnable);

        getContext().unregisterReceiver(batteryReceiver);

        getContext().unregisterReceiver(volumeReceiver);

        handler.removeCallbacks(updatePositionRunnable);
    }


    public void setVideoURI(Uri videoURI, String title) {
        videoView.setVideoURI(videoURI);
        titleTv.setText(title);
        Log.d("setVideoURI ", "duration " + videoView.getDuration());

    }


    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoView.start();
            Log.d("onPrepared ", "duration " + videoView.getDuration());
            positionSb.setMax(videoView.getDuration());
            totalTimeTv.setText(MyFormatter.formatDuration(videoView.getDuration()));
            updatePausePauseUI();
//
        }
    };
    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            updatePausePauseUI();
        }
    };

    //-------------------更新时间-----------------------
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
    // 复用日期对象，减少垃圾
    Date date = new Date();
    static Handler handler = new Handler();
    Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("Runnable", "updateTimeRunnable");
            long time = System.currentTimeMillis();

            date.setTime(time);
            timeTv.setText(simpleDateFormat.format(date));
            handler.postDelayed(this, 1000);
        }
    };
    //-------------------更新电量-----------------------

    int[] batteryPicRes = new int[]{R.drawable.ic_battery_0, R.drawable.ic_battery_10, R.drawable.ic_battery_20,
            R.drawable.ic_battery_20, R.drawable.ic_battery_40, R.drawable.ic_battery_40, R.drawable.ic_battery_60,
            R.drawable.ic_battery_60, R.drawable.ic_battery_80, R.drawable.ic_battery_80, R.drawable.ic_battery_100,};
    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int current = intent.getIntExtra("level", -1);//获得当前电量
            int total = intent.getIntExtra("scale", -1); //获得总电量
            int picIndex = (int) (1.0f * current / total * 10); // 得到0-10直接的数据
            int picRes = batteryPicRes[picIndex];
            batteryIv.setImageResource(picRes);
            Log.d("batteryReceiver", "onReceive " + current + " / " + total);


        }
    };
    //-------------------音量相关-----------------------
    private SeekBar.OnSeekBarChangeListener volumeSbListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) { // 如果是用户手指触摸造成的改变，才去改变音量

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    BroadcastReceiver volumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("volumeReceiver", "onReceive ");
            int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            volumeSb.setProgress(current);
            if (current == 0) {
                muteIv.setImageResource(R.drawable.volume_off);
            } else {
                muteIv.setImageResource(R.drawable.volume_up);
            }

        }
    };

    private OnClickListener muteOcl = new OnClickListener() {
        int volumeBeforeMute = 7;

        @Override
        public void onClick(View v) {
            int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            if (current > 0) { // 当前音量大于0，就设置为0
                volumeBeforeMute = current;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            } else {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeBeforeMute, 0);
            }
        }
    };
    //-------------------更新进度---------------------
    Runnable updatePositionRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("updatePositionRunnable", " run ");
            int currentPosition = videoView.getCurrentPosition();
            if (!isTouchingPositionSb) {
                positionSb.setProgress(currentPosition);
            }
            handler.postDelayed(this, 500);

        }
    };

    boolean isTouchingPositionSb = false;
    //-------------------控制进度---------------------
    private SeekBar.OnSeekBarChangeListener positionSbListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoView.seekTo(progress);
            }

            passedTimeTv.setText(MyFormatter.formatDuration(progress));
        }

        boolean isPlayingOnDown = false;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isTouchingPositionSb = true;
            isPlayingOnDown = videoView.isPlaying();
            if (isPlayingOnDown) {
                videoView.pause();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isTouchingPositionSb = false;
            if (isPlayingOnDown) {
                videoView.start();
            }

        }
    };
    //----------------------------------------
    boolean isFullScreen = false;
    private OnClickListener switchScreenOcl = new OnClickListener() {
        @Override
        public void onClick(View v) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            if (isFullScreen) { //
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
//                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                videoView.setLayoutParams(lp);
            } else {
//                RelativeLayouat.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
//                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                videoView.setLayoutParams(lp);
            }
            isFullScreen = !isFullScreen;
        }
    };
    //-------------------播放暂停---------------------
    private OnClickListener playPauseOcl = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (videoView.isPlaying()) {
                videoView.pause();
            } else {
                videoView.start();
            }

            updatePausePauseUI();
        }
    };

    private void updatePausePauseUI() {
        if (videoView.isPlaying()) {
            playPauseIv.setImageResource(R.drawable.btn_pause_selector);
            handler.post(updatePositionRunnable);
        } else {
            playPauseIv.setImageResource(R.drawable.btn_play_selector);
            handler.removeCallbacks(updatePositionRunnable);
        }
    }

    // call back

    private OnClickListener callbackOcl = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onKeyClickListener == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.controller_next_iv:
                    onKeyClickListener.onNextClick();
                    break;
                case R.id.controller_prev_iv:
                    onKeyClickListener.onPrevClick();
                    break;
                case R.id.controller_exit_iv:
                    onKeyClickListener.onBackClick();
                    break;
            }
        }
    };

    //------------------show and hide----------------------

// 所有孩子的触摸都是由MyMediaControl分发的，所以在 dispatchTouchEvent 方法中 判断触摸事件类型，进行显示和隐藏的处理
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                show();
                break;
            case MotionEvent.ACTION_UP:
                hide();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    // 如果点击的位置不在子空间上，那么down的触摸事件不会被消费，那么 MyMediaControl的父控件发现不消费，就不会把后续的触摸事件继续分发了
    // 所以要返回true，把孩子不要的触摸事件给消费掉
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private void show() {
        getShowAnimator().start();
//        Toast.makeText(getContext(),"show",Toast.LENGTH_SHORT).show();
    }
    private void hide() {
//        Toast.makeText(getContext(),"hide",Toast.LENGTH_SHORT).show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getHideAnimator().start();
            }
        }, 2000);


    }
    private Animator getShowAnimator(){
        ObjectAnimator showTopAnimator = ObjectAnimator.ofFloat(topControllers,"translationY",-topControllers.getHeight(),0);
        showTopAnimator.setDuration(1000);
        ObjectAnimator showBottomAnimator = ObjectAnimator.ofFloat(bottomControllers,"translationY",bottomControllers.getHeight(),0);
        showBottomAnimator.setDuration(1000);

        AnimatorSet animatorSet =new AnimatorSet();
        animatorSet.playTogether(showTopAnimator,showBottomAnimator);
        return animatorSet;
    }
    private Animator getHideAnimator(){
        ObjectAnimator hideTopAnimator = ObjectAnimator.ofFloat(topControllers,"translationY",0,-topControllers.getHeight());
        hideTopAnimator.setDuration(1000);
        ObjectAnimator hideBottomAnimator = ObjectAnimator.ofFloat(bottomControllers,"translationY",0,bottomControllers.getHeight());
        hideBottomAnimator.setDuration(1000);

        AnimatorSet animatorSet =new AnimatorSet();
        animatorSet.playTogether(hideTopAnimator,hideBottomAnimator);
        return animatorSet;
    }


}

