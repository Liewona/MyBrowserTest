package com.example.myviabrowser.utils;

import android.os.Handler;

public class ThreadUtils {


    /**
     * 主线程中的一个 handler
     */
    public static Handler handler = new Handler();

    /**
     * 子线程执行 task
     * @param task
     */
    public static void setInThread(Runnable task) {
        new Thread(task).start();
    }


    /**
     * UI线程执行 task
     * @param task
     */
    public static void setInUIThread(Runnable task) {
        handler.post(task);
    }


    public static void setPostDelayed(Runnable task, long timeout) {
        handler.postDelayed(task, timeout);
    }


}
