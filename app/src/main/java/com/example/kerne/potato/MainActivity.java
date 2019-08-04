package com.example.kerne.potato;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.billy.android.swipe.SmartSwipeBack;
import com.example.kerne.potato.Fragment.FirmSurveyFragment;
import com.example.kerne.potato.Fragment.HomepageFragment;
import com.viewpagerindicator.TabPageIndicator;

import static com.example.kerne.potato.Util.ChangeStatusBar.setStatusBarColor;

public class MainActivity extends AppCompatActivity implements HomepageFragment.selectFarm {

    public static Fragment homepageFragment;
    public static Fragment firmSurveyFragment;
    public String titles[] = new String[]{"试验田", "试验调查"};
    TabPageIndicator mainIndicator;
    ViewPager mainViewpager;
    private TabPageIndicatorAdapter mAdpter;
    private int currentPage = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarColor(this, R.color.primary_background);
        mainIndicator = findViewById(R.id.main_indicator);
        mainViewpager = findViewById(R.id.main_viewpager);
        initView();
    }

    //加载视图
    private void initView() {
        homepageFragment = HomepageFragment.newInstance();
        firmSurveyFragment = FirmSurveyFragment.newInstance();
        //给ViewPager设置Adapter
        mAdpter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        //与ViewPager绑在一起（核心步骤）
        mainViewpager.setAdapter(mAdpter);
        mainIndicator.setViewPager(mainViewpager);
        mainIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mainIndicator.setOnTabReselectedListener(new TabPageIndicator.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
            }
        });
    }

    @Override
    public void selectFarm(String bigFarmId) {
        ((FirmSurveyFragment) firmSurveyFragment).selectFarm(bigFarmId);
    }

    //adapter
    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        Bundle args = new Bundle();

        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    args.putString("arg", titles[position]);
                    homepageFragment.setArguments(args);
                    return homepageFragment;
                case 1:
                    args.putString("arg", titles[position]);
                    firmSurveyFragment.setArguments(args);
                    return firmSurveyFragment;
            }
            return homepageFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position % titles.length];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            //不销毁fragment
//            super.destroyItem(container, position, object);
        }
    }
}
