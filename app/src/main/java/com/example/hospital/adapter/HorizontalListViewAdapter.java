package com.example.hospital.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.hospital.R;

public class HorizontalListViewAdapter extends BaseAdapter{
    private String[] mWeek, mDate;
    private Context mContext;
    private LayoutInflater mInflater;
    Bitmap iconBitmap;
    private int selectIndex = -1;

    public HorizontalListViewAdapter(Context context, String[] week, String[] date){
        this.mContext = context;
        this.mDate = date;
        this.mWeek = week;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return mDate.length;
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.date_item, null);
            holder.mWeek =(TextView)convertView.findViewById(R.id.week);
            holder.mDate =(TextView)convertView.findViewById(R.id.date);
            holder.constraintLayout = convertView.findViewById(R.id.layout);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        if (position == selectIndex) {
            holder.constraintLayout.setBackgroundColor(0xffe3ffc6);
        } else {
            holder.constraintLayout.setBackgroundColor(0xFFFFFFFF);
        }

        holder.mWeek.setText(mWeek[position]);
        holder.mDate.setText(mDate[position]);


        return convertView;
    }

    private static class ViewHolder {
        private TextView mDate, mWeek;
        private ConstraintLayout constraintLayout;
    }
//    private Bitmap getPropThumnail(int id){
//        Drawable d = mContext.getResources().getDrawable(id);
//        Bitmap b = BitmapUtil.drawableToBitmap(d);
////		Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
//        int w = mContext.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);
//        int h = mContext.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);
//
//        Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);
//
//        return thumBitmap;
//    }
    public void setSelectedPosition(int i){
        selectIndex = i;
    }
}