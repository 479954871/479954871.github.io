package com.example.hospital.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospital.R;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

public class GuaHaoFirstRecyclerAdapter extends RecyclerView.Adapter<GuaHaoFirstRecyclerAdapter.VH>{
    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{
        public final TextView title;
        public final ConstraintLayout constraintLayout;
        public VH(View v) {
            super(v);
            title = v.findViewById(R.id.keshi_first_name);
            constraintLayout = v.findViewById(R.id.keshi_first_background);
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

    private int nowPosition = 0;

    private TextView textView;

    private ConstraintLayout constraintLayout;

    public GuaHaoFirstRecyclerAdapter(List<String> data) {
        this.mDatas = data;

    }

    //回调方法 将接口传递进来
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.title.setText(mDatas.get(position));
        if (position == nowPosition) {
            holder.title.setTextColor(0xFF03DAC5);
            holder.constraintLayout.setBackgroundColor(0xFFFFFFFF);
            textView = holder.title;
            constraintLayout = holder.constraintLayout;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != nowPosition) {
                    textView.setTextColor(0xFF000000);
                    constraintLayout.setBackgroundColor(0xFFDDDDDD);
                    nowPosition = position;
                    holder.title.setTextColor(0xFF03DAC5);
                    holder.constraintLayout.setBackgroundColor(0xFFFFFFFF);
                    textView = holder.title;
                    constraintLayout = holder.constraintLayout;
                }
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

    @NotNull
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.keshi_first_item, parent, false);
        return new VH(v);
    }
}
