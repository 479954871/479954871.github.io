package com.example.hospital.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospital.R;

import java.util.List;

public class GuaHaoSecondRecyclerAdapter extends RecyclerView.Adapter<GuaHaoSecondRecyclerAdapter.VH>{
    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{
        public final TextView title;
        public VH(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.keshi_second_name);
        }
    }
    //在adapter中自定义一个接口 实现想要实现的方法
    public interface OnItemClickListener
    {
        //子条目单击事件
        void onItemClick(View view, int position);
        //子条目长按事件
        void onItemLongClick(View view,int position);

    }
    private List<String> mDatas;
    private OnItemClickListener mOnItemClickListener;

    public GuaHaoSecondRecyclerAdapter(List<String> data) {
        this.mDatas = data;
    }

    //回调方法 将接口传递进来
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }



    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.title.setText(mDatas.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //item 点击事件
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemLongClick(v, position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.keshi_second_item, parent, false);
        return new VH(v);
    }

    public void changeData(List<String> data) {
        mDatas = data;
    }
}
