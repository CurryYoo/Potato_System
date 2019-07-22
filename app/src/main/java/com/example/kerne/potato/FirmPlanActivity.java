package com.example.kerne.potato;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kerne.potato.Fragment.InShackFragment;
import com.example.kerne.potato.Fragment.OutShackFragment;
import com.viewpagerindicator.TabPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirmPlanActivity extends AppCompatActivity {

    public static Fragment outShackFragment;
    public static Fragment inShackFragment;
    public String titles[] = new String[]{"试验田", "棚区试验田"};
    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.right_one_button)
    ImageView rightOneButton;
    @BindView(R.id.right_one_layout)
    LinearLayout rightOneLayout;
    @BindView(R.id.plan_indicator)
    TabPageIndicator planIndicator;
    @BindView(R.id.plan_viewPager)
    ViewPager planViewPager;
    private TabPageIndicatorAdapter mAdpter;
    private int currentPage = 0;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
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
        setContentView(R.layout.activity_firm_plan);
        ButterKnife.bind(this);
        initToolBar();
        initView();
    }

    private void initToolBar() {
        leftOneButton.setBackgroundResource(R.drawable.left_back);
        titleText.setText(getString(R.string.farm_plan));

        leftOneLayout.setBackgroundResource(R.drawable.selector_button);
        leftOneLayout.setOnClickListener(onClickListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            leftOneLayout.setTooltipText(getString(R.string.back_left));
        }
    }

    //加载视图
    private void initView() {
        outShackFragment = OutShackFragment.newInstance();
        inShackFragment = InShackFragment.newInstance();
        //给ViewPager设置Adapter
        mAdpter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        //与ViewPager绑在一起（核心步骤）
        planViewPager.setAdapter(mAdpter);
        planIndicator.setViewPager(planViewPager);
        planIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPage=i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        if (currentPage == 0) {
            super.onBackPressed();
        }else {
            planIndicator.setCurrentItem(0);
        }
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
            //不销毁fragment
//            super.destroyItem(container, position, object);
        }
    }
}
