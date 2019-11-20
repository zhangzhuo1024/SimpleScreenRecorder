package com.lupindi.screenrecorder.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.lupindi.screenrecorder.R;

/**
 * @author zhangzhuo
 * @version 2019/11/20
 */
public class CircleWaveButton extends View {

    //这是默认颜色
    private int paintColor= R.color.circle_bule_bbd4e7;
    //画圆的画笔
    private Paint paint = new Paint();
    //画字的画笔
    private Paint textPaint = new Paint();
    private int radiusInt = 0;
    //这是默认文字内容
    private static String text="开始";
    private Boolean isStart = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
            if (isStart) {
                radiusInt++;
                if (radiusInt > 50) {
                    radiusInt = 0;
                }
                sendEmptyMessageDelayed(0, 20);
            }
        }
    };
    public CircleWaveButton(Context context) {
        super(context);
        init();
    }


    public CircleWaveButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public CircleWaveButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setBackgroundColor(getResources().getColor(R.color.running));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centre = getWidth() / 2;
        int radius = centre;
        //绘制圆的画笔
        Paint newPaint = new Paint();
        newPaint.setAntiAlias(true);        // 去除画笔锯齿
        newPaint.setStyle(Paint.Style.FILL);// 设置风格为实线

        //用来进行环形渲染
        Shader shader = new RadialGradient(centre, centre, radius, getResources().getColor(paintColor), getResources().getColor(R.color.running), Shader.TileMode.CLAMP);
        //设置图像效果，使用Shader可以绘制出各种渐变效果
        newPaint.setShader(shader);
        //绘制圆形（圆心的x坐标，圆心的y坐标，圆的半径，绘制时所使用的画笔）
        canvas.drawCircle(centre, centre, radius, newPaint);

        paint.setColor(getResources().getColor(paintColor));
        paint.setStyle(Paint.Style.FILL);   //设置风格为实线
        paint.setAntiAlias(true);           // 去除画笔锯齿

        for(int i=1;i<=5;i++){
            paint.setAlpha(20*i);//设置绘制图形的透明度
            //绘制圆形（圆心的x坐标，圆心的y坐标，圆的半径，绘制时所使用的画笔）
            canvas.drawCircle(centre, centre, radius * (10- i)/ 10, paint);
        }

        paint.setColor(getResources().getColor(paintColor));
        paint.setStyle(Paint.Style.STROKE);     //设置风格为空心
        paint.setStrokeWidth(radius * 2 / 12);  //设置线宽

        for(int i=0;i<3;i++) {
            paint.setAlpha(60-i*20);//设置绘制图形的透明度
            //绘制圆形（圆心的x坐标，圆心的y坐标，圆的半径，绘制时所使用的画笔）
            canvas.drawCircle(centre, centre, radius * (14-i*2 + radiusInt / 50.0f) / 16, paint);
        }


        textPaint.setTextSize(getTextSize());
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(getResources().getColor(R.color.textcolor));

        //获取文字的宽度值
        float length = textPaint.measureText(text);
        //绘制文字
        canvas.drawText(text, centre - length / 2, centre + getTextSize() / 3, textPaint);
    }

    private float getTextSize() {
        return 30f;
    }

    public void start() {
        isStart = true;
        handler.sendEmptyMessage(0);
    }

    public void stop() {
        isStart = false;
        handler.removeMessages(0);
    }

    public void setText(String string) {
        text = string;
        invalidate();
    }
}

