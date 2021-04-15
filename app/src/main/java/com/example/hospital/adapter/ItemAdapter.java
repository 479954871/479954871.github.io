package com.example.hospital.adapter;

import android.content.Context;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hospital.R;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    public static int LEFT = 1;
    public static int RIGHT = 2;
    public static int URI = 3;
    public static int BITMAP = 4;
    List<Object> list;
    List<Integer> list1;
    Context context;


    public ItemAdapter( Context context) {
        this.context = context;
        list=new ArrayList<>();
        list1=new ArrayList<>();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_layout, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,  int position) {
        if(list1.get(position)==LEFT){
            holder.tvLeft.setVisibility(View.VISIBLE);
            holder.tvRight.setVisibility(View.GONE);
            holder.tvLeft.setText((String) list.get(position));
            holder.pic.setVisibility(View.GONE);
        }else if(list1.get(position)==RIGHT){
            holder.tvLeft.setVisibility(View.GONE);
            holder.tvRight.setVisibility(View.VISIBLE);
            holder.tvRight.setText((String) list.get(position));
            holder.pic.setVisibility(View.GONE);
        } else if (list1.get(position) == URI) {
            holder.tvLeft.setVisibility(View.GONE);
            holder.tvRight.setVisibility(View.GONE);
            holder.pic.setVisibility(View.VISIBLE);
            holder.pic.setImageURI((Uri) list.get(position));
        } else if (list1.get(position) == BITMAP) {
            holder.tvLeft.setVisibility(View.GONE);
            holder.tvRight.setVisibility(View.GONE);
            holder.pic.setVisibility(View.VISIBLE);
            holder.pic.setImageBitmap((Bitmap) list.get(position));
        }
    }

    //添加子项
    public void addItem(String str, int leftOrRight, Uri uri, Bitmap bitmap) {
        list1.add(leftOrRight);
        if (leftOrRight == URI) {
            list.add(uri);
        } else if (leftOrRight == BITMAP) {
            list.add(bitmap);
        } else{
            list.add(str);
        }
        notifyItemInserted(list.size()-1);
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLeft,tvRight;
        ImageView pic;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvLeft = itemView.findViewById(R.id.leftTv);
            tvRight = itemView.findViewById(R.id.rightTv);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}