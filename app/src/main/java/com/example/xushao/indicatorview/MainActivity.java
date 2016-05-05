package com.example.xushao.indicatorview;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private IndicatorView mIndicatorView;
    private List<View> mPagerList;

    private int mOldPagePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View pager0 = LayoutInflater.from(this).inflate(R.layout.viewpager_item0, null);
        View pager1 = LayoutInflater.from(this).inflate(R.layout.viewpager_item1, null);
        View pager2 = LayoutInflater.from(this).inflate(R.layout.viewpager_item2, null);
        View pager3 = LayoutInflater.from(this).inflate(R.layout.viewpager_item3, null);

        mPagerList = new ArrayList<>();// 将要分页显示的View装入数组中
        mPagerList.add(pager0);
        mPagerList.add(pager1);
        mPagerList.add(pager2);
        mPagerList.add(pager3);

        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return mPagerList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mPagerList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mPagerList.get(position));
                return mPagerList.get(position);
            }
        };

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);
        mIndicatorView = (IndicatorView) findViewById(R.id.indicatorView);
        mIndicatorView.setCount(mPagerList.size());
        mIndicatorView.onPageChanged(0, 0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mIndicatorView.onPageChanged(position, mOldPagePosition);
        mOldPagePosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
