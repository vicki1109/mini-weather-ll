package com.example.sunfiyes.myapplication;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by sunfiyes on 2016/12/25 0025.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private List<View> views;
    private Context context;

    public ViewPagerAdapter(List<View> views,Context context)
    {
        this.views = views;
        this.context = context;
    }

    public int getCount()
    {
        return views.size();
    }

    public Object instantiateItem(ViewGroup container,int position)
    {
        container.addView(views.get(position));
        return views.get(position);
    }

    public boolean isViewFromObject(View view,Object o)
    {
        return (view==o);
    }
    public void destroyItem(ViewGroup container,int position,Object object)
    {
        container.removeView(views.get(position));
    }

}
