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

import com.example.hospital.account.AccountManager;
import com.example.hospital.activity.GuaHaoGuanLiActivity;
import com.example.hospital.activity.JiaoFeiGuanLiActivity;
import com.example.hospital.activity.JiuZhenRenGuanLiActivity;
import com.example.hospital.activity.MyAccountActivity;
import com.example.hospital.activity.SignInActivity;
import com.example.hospital.R;

public class MineFragment extends Fragment {

    private MineViewModel mineViewModel;
    TextView textView2;
    @Override
    public void onStart() {
        super.onStart();
        textView2.setText(AccountManager.getInstance().getNowAccount());
    }

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


        textView2 = root.findViewById(R.id.text_my_account1);
        textView2.setText(AccountManager.getInstance().getNowAccount());
        if (AccountManager.getInstance().getNowAccount().equals("")) {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
        }


        ConstraintLayout constraintLayout = root.findViewById(R.id.my_account);
        constraintLayout.setOnClickListener(v -> {
            if (AccountManager.getInstance().getNowAccount().equals("")) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), MyAccountActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout linearLayout = root.findViewById(R.id.linear);
        linearLayout.setOnClickListener(v -> {
            if (AccountManager.getInstance().getNowAccount().equals("")) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), JiuZhenRenGuanLiActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout linearLayout1 = root.findViewById(R.id.linear1);
        linearLayout1.setOnClickListener(v -> {
            if (AccountManager.getInstance().getNowAccount().equals("")) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), GuaHaoGuanLiActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout linearLayout2 = root.findViewById(R.id.linear2);
        linearLayout2.setOnClickListener(v -> {
            if (AccountManager.getInstance().getNowAccount().equals("")) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), JiaoFeiGuanLiActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}