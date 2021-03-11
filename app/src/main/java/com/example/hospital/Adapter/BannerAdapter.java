package com.example.hospital.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.example.hospital.Constant.HosptialConstant;
import com.example.hospital.R;

/**
 * 新闻适配器
 * @version 1.0
 * @created 2014-3-25
 */
public class BannerAdapter extends PagerAdapter
{//新闻适配器
    private final static String TAG = "BannerAdapter";

    private Activity activity=null;
    private Bitmap[] bitmaps=new Bitmap[HosptialConstant.PAGE_COUNT];
    private ImageView[] images=new ImageView[HosptialConstant.PAGE_COUNT];
    private Fragment mainFragment=null;

    public BannerAdapter(Activity activity,Fragment mainFragment) {
        this.mainFragment=mainFragment;
        for(int i = 0; i < HosptialConstant.PAGE_COUNT; ++i) {
            bitmaps[i]=null;
        }

        for(int i=0; i<HosptialConstant.PAGE_COUNT;++i) {
            images[i]=null;
        }

        this.activity=activity;
        bitmaps[0]= BitmapFactory.decodeResource(activity.getResources(), R.mipmap.yiyuan_pic_1);
        bitmaps[1]=BitmapFactory.decodeResource(activity.getResources(), R.mipmap.yiyuan_pic_2);
        bitmaps[2]=BitmapFactory.decodeResource(activity.getResources(), R.mipmap.yiyuan_pic_3);
        bitmaps[3]=BitmapFactory.decodeResource(activity.getResources(), R.mipmap.yiyuan_pic_4);
        bitmaps[4]=BitmapFactory.decodeResource(activity.getResources(), R.mipmap.yiyuan_pic_5);
        bitmaps[5]=BitmapFactory.decodeResource(activity.getResources(), R.mipmap.yiyuan_pic_5);

//        class MyOnClickListener implements View.OnClickListener
//        {
//            private Fragment mainFragment=null;
//            int index=0;
//
//            public MyOnClickListener(Fragment f,int index)
//            {
//                this.index=index;
//                this.mainFragment=f;
//            }
//
//            @Override
//            public void onClick(View v)
//            {
//                int type=Constants.SHOUYE;
//                switch(index)
//                {
//                    case 0://医院简介
//                        type=Constants.JIANJIE;
//                        break;
//
//                    case 1://挂号引导
//                        type=Constants.GUAHAOYINDAO;
//                        break;
//
//                    case 2://预约挂号
//                        type=Constants.PUTONGGUAHAO;
//                        break;
//
//                    case 3://我的服务
//                        type=Constants.HOUZHENSHI;
//                        break;
//
//                    case 4://电子检验报告
//                        type=Constants.DIANZIJIANYANBAOGAO;
//                        break;
//
//                    case 5://我的账号
//                        type=Constants.WODEZHANGHAO;
//                        break;
//                }
//            }
//
//        }

        for(int i=0;i<HosptialConstant.PAGE_COUNT;i++)
        {
            images[i]=new ImageView(activity);
            images[i].setImageBitmap(bitmaps[i]);
            images[i].setScaleType(ImageView.ScaleType.FIT_XY);
//            images[i].setOnClickListener(new MyOnClickListener(mainFragment,i));
        }
    }

    @Override
    public int getCount()
    {
        return HosptialConstant.PAGE_COUNT;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj)
    {
        return view == obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View)object);
        object=null;
    }

    @Override
    public Object instantiateItem (ViewGroup container, final int position)
    {
        container.addView(images[position]);
        return images[position];
    }
}
