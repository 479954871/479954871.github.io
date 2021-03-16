package com.example.hospital.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.hospital.Activity.JiuZhenRenGuanLiActivity;
import com.example.hospital.Activity.SignInActivity;
import com.example.hospital.R;

public class MineFragment extends Fragment {

    private MineViewModel mineViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mineViewModel =
                new ViewModelProvider(this).get(MineViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mine, container, false);
        mineViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        TextView textView1 = root.findViewById(R.id.main_head_title);
        textView1.setText("我的");
        ConstraintLayout constraintLayout = root.findViewById(R.id.my_account);
        constraintLayout.setOnClickListener(v -> {
            //TODO 一开始直接进登录页面，一定要登录才能使用app
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
        });
        LinearLayout linearLayout = root.findViewById(R.id.linear);
        linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), JiuZhenRenGuanLiActivity.class);
            startActivity(intent);
        });
        return root;
    }
}