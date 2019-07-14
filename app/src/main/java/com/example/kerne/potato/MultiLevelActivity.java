package com.example.kerne.potato;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewpagerindicator.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultiLevelActivity extends AppCompatActivity {

    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;
    @BindView(R.id.title_text)
    TextView titleText;

    public Fragment outShackFragment;
    public Fragment inShackFragment;
    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private TabPageIndicatorAdapter mAdpter;
    private int currentPage = 0;
    public String titles[] = new String[]{"棚外", "棚内"};

    View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutlilevel);
        ButterKnife.bind(this);
        initToolBar();
        initView();
    }

    private void initView() {
        outShackFragment = OutShackFragment.newInstance();
        inShackFragment = InShackFragment.newInstance();
        //给ViewPager设置Adapter
        mAdpter=new TabPageIndicatorAdapter(getSupportFragmentManager());
        //与ViewPager绑在一起（核心步骤）
        viewpager.setAdapter(mAdpter);
        indicator.setViewPager(viewpager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    private void initToolBar() {
        titleText.setText(getText(R.string.farm_tree));
        leftOneButton.setBackgroundResource(R.drawable.left_back);

        leftOneLayout.setOnClickListener(toolBarOnClickListener);
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
                    outShackFragment.setArguments(args);
                    return outShackFragment;
                case 1:
                    args.putString("arg", titles[position]);
                    inShackFragment.setArguments(args);
                    return inShackFragment;
            }
            return outShackFragment;
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
//            super.destroyItem(container, position, object);//不销毁fragment
        }
    }
}
