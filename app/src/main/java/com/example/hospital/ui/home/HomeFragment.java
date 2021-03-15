package com.example.hospital.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.hospital.Activity.MenZhenGuaHaoActivity;
import com.example.hospital.Activity.YiYuanJieShaoActivity;
import com.example.hospital.Adapter.BannerAdapter;
import com.example.hospital.Constant.HosptialConstant;
import com.example.hospital.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private BannerAdapter adapter;

    private ViewPager viewPager=null;

    private ImageView[] indicators = new ImageView[HosptialConstant.PAGE_COUNT];

    private int prePosition = 0;

    private static int currentItem = 0;

    private ScheduledExecutorService scheduledExecutorService = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        TextView textView1 = root.findViewById(R.id.main_head_title);
        textView1.setText("首页");

        viewPager = ((ViewPager) root.findViewById(R.id.viewPager));
        indicators[0] = ((ImageView) root.findViewById(R.id.image_1));
        indicators[1] = ((ImageView) root.findViewById(R.id.image_2));
        indicators[2] = ((ImageView) root.findViewById(R.id.image_3));
        indicators[3] = ((ImageView) root.findViewById(R.id.image_4));
        indicators[4] = ((ImageView) root.findViewById(R.id.image_5));
        indicators[5] = ((ImageView) root.findViewById(R.id.image_6));
        if (adapter == null)
            adapter = new BannerAdapter(getActivity(),this);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrollStateChanged(int state)
            {
                if (state == ViewPager.SCROLL_STATE_IDLE)
                {
                    indicators[prePosition].setImageResource(R.mipmap.news_image_unselected);
                    int i = viewPager.getCurrentItem();
                    indicators[i].setImageResource(R.mipmap.news_image_selected);
                    prePosition =i;
                }
            }

            @Override
            public void onPageScrolled(int position,float poisitionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
            }
        });

        ImageView imageView1 = root.findViewById(R.id.yiyuanjieshao);
        imageView1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), YiYuanJieShaoActivity.class);
            startActivity(intent);
        });

        ImageView imageView2 = root.findViewById(R.id.menzhenguahao);
        imageView2.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenZhenGuaHaoActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onStart()
    {
        scheduledExecutorService= Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new ViewPagerTask(), 1, 5, TimeUnit.SECONDS);
        super.onStart();
    }

    @Override
    public void onStop()
    {
        scheduledExecutorService.shutdown();
        super.onStop();
    }

    private class ViewPagerTask implements Runnable
    {
        @Override
        public void run()
        {
            currentItem = (currentItem + 1) % HosptialConstant.PAGE_COUNT;
            handler.obtainMessage().sendToTarget();
        }

    }

    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            viewPager.setCurrentItem(currentItem);
        }
    };
}