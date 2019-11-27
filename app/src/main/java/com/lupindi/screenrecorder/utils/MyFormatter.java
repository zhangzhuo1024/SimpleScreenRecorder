package com.lupindi.screenrecorder.utils;

/**
 * Created by Teacher on 2016/6/29.
 */
public class MyFormatter {
    public static String formatDuration(long durationInMs) {
        // 4509000 -> 01:15:09
        //
//        long second = durationInMs / 1000;
//        long minute = second / 60;
//        long hour = minute / 60;
//        second = second % 60;
//        minute = minute % 60;
//        return  (hour < 10 ? "0" + hour : hour)
//                + ":" +
//                (minute < 10 ? "0" + minute : minute)
//                + ":" +
//                (second < 10 ? "0" + second : second)
//                ;
        // %d，占位符 表示整数
        // %2d, 表示至少需要2位，会把数子放在右边，左边用空格补全
        // %02d, 表示至少需要2位，会把数子放在右边，左边用0补全
        return String.format("%02d:%02d:%02d", durationInMs / 1000 / 60 / 60, durationInMs / 1000 / 60 % 60, durationInMs / 1000 % 60);
    }
}
