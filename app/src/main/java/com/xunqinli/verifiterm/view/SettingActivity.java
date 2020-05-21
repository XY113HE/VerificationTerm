package com.xunqinli.verifiterm.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.databinding.ActivitySettingBinding;
import com.xunqinli.verifiterm.interf.SettingInterf;
import com.xunqinli.verifiterm.viewmodel.SettingVM;

public class SettingActivity extends BaseActivity implements SettingInterf.MainView {
    private ActivitySettingBinding mMainBinding;
    private SettingVM mSettingVM;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
    }

    private void initDataBinding() {
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        mSettingVM = new SettingVM(mMainBinding, this);
    }

    @Override
    public BaseActivity getActivity() {
        return SettingActivity.this;
    }

    @Override
    public void jump2controlDetail() {
        Intent intent = new Intent(this, ControlDetailActivity.class);
        startActivity(intent);
    }
}
