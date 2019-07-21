package com.example.kerne.potato;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.kerne.potato.Fragment.FirmSurveyFragment;
import com.example.kerne.potato.Fragment.HomepageFragment;
import com.viewpagerindicator.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements HomepageFragment.updateData {

    public static Fragment homepageFragment;
    public static Fragment firmSurveyFragment;
    @BindView(R.id.main_indicator)
    TabPageIndicator mainIndicator;
    @BindView(R.id.main_viewpager)
    ViewPager mainViewpager;

    private TabPageIndicatorAdapter mAdpter;
    private int currentPage = 0;
    public String titles[] = new String[]{"首页", "田间试验调查"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void updateData(Boolean update_flag) {
        ((FirmSurveyFragment) firmSurveyFragment).setUpdate_flag(update_flag);
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
            super.destroyItem(container, position, object);//不销毁fragment
        }
    }
}
