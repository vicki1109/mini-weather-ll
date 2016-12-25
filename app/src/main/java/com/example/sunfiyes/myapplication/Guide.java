package com.example.sunfiyes.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Guide extends Activity implements OnPageChangeListener{
    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.iv1,R.id.iv2,R.id.iv3,R.id.iv4};
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initViews();
        initDots();

        btn = (Button)views.get(3).findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(Guide.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void initViews()
    {
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.guide_one,null));
        views.add(inflater.inflate(R.layout.guide_two,null));
        views.add(inflater.inflate(R.layout.guide_three,null));
        views.add(inflater.inflate(R.layout.guide_four,null));

        vpAdapter = new ViewPagerAdapter(views,this);

        vp = (ViewPager)findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);

        vp.setOnPageChangeListener(this);

    }

    void initDots()
    {
        dots = new ImageView[views.size()];
        for(int i =0;i<views.size();i++)
        {
            dots[i] = (ImageView)findViewById(ids[i]);
        }
    }

    public void onPageScrolled(int i,float v,int i2)
    {

    }

    public void onPageSelected(int i)
    {
        for(int a = 0;a<ids.length;a++)
        {
            if(a==i)
            {
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }
            else
            {
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
