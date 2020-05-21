package com.xunqinli.verifiterm.view;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.databinding.ActivityControlDetailBinding;
import com.xunqinli.verifiterm.interf.ControlDetailInterf;
import com.xunqinli.verifiterm.viewmodel.ControlDetailVM;

import java.util.List;

public class ControlDetailActivity extends BaseActivity implements ControlDetailInterf.MainView {
    private ActivityControlDetailBinding mBinding;
    private ControlDetailVM mControlDetailVM;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
    }



    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_control_detail);
        mControlDetailVM = new ControlDetailVM(this, mBinding);
    }

    @Override
    public BaseActivity getActivity() {
        return ControlDetailActivity.this;
    }
}
