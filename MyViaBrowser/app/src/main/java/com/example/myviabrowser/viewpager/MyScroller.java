package com.example.myviabrowser.viewpager;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

import androidx.viewpager.widget.ViewPager;

public class MyScroller extends Scroller {

    private int scrollerDuration = 0;  //设置viewpager滚动时间

    public MyScroller(Context context) {
        super(context);
    }

    public MyScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public MyScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, duration);
    }

    public void initScroller(ViewPager pager) {
        try {
            Field mScroller = pager.getClass().getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(pager, this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
