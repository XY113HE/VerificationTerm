package com.xunqinli.verifiterm.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.cons.Constant;
import com.xunqinli.verifiterm.databinding.ActivityRegandlogBinding;
import com.xunqinli.verifiterm.interf.RegAndLogInterf;
import com.xunqinli.verifiterm.model.ShowLoginBean;
import com.xunqinli.verifiterm.model.VerificationNotifyBean;
import com.xunqinli.verifiterm.rxbus.RxBus;
import com.xunqinli.verifiterm.utils.Tools;
import com.xunqinli.verifiterm.viewmodel.RegisterAndLoginVM;


import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.xunqinli.verifiterm.cons.Constant.ACTIVE_CODE_S;

public class RegisterAndLoginActivity extends BaseActivity implements RegAndLogInterf.MainView{
    private static final String TAG = "lmy_register";
    private ActivityRegandlogBinding mBinding;
    private RegisterAndLoginVM mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        initView();
        //初始化mac值
        Constant.MAC = Tools.getMac(getApplicationContext());
        initRxBus();
        Log.e(TAG, "onCreate: " + Tools.getMD5("123456z").toUpperCase());
    }

    private void initRxBus() {
        RxBus.getRxBus().toObservable(ShowLoginBean.class)
                .compose(RegisterAndLoginActivity.this.<ShowLoginBean>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ShowLoginBean>() {
                    @Override
                    public void call(ShowLoginBean bean) {
                        showLogin();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void initView() {
        //TODO 测试代码
        //editor.putString(ACTIVE_CODE_S, "2019058120001").commit();
        String s = mySharedPreferences.getString(ACTIVE_CODE_S, "");
        if(TextUtils.isEmpty(s)){
            showRegister();
        }else{
            showLogin();
        }
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_regandlog);
        mViewModel = new RegisterAndLoginVM(this, mBinding);
    }

    @Override
    public BaseActivity getActivity() {
        return RegisterAndLoginActivity.this;
    }

    @Override
    public void jump2main(Bundle bundle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLogin() {
        mBinding.username.setText("");
        mBinding.psw.setText("");
        mBinding.activeCode.setText("");
        mBinding.box1Blank.setVisibility(View.VISIBLE);
        mBinding.box1Layout.setVisibility(View.VISIBLE);
        mBinding.box2Blank.setVisibility(View.VISIBLE);
        mBinding.box2Layout.setVisibility(View.VISIBLE);
        mBinding.box3Blank.setVisibility(View.GONE);
        mBinding.box3Layout.setVisibility(View.GONE);
        mBinding.registerBtn.setVisibility(View.GONE);
        mBinding.loginBtn.setVisibility(View.VISIBLE);
    }

    public void showRegister(){
        mBinding.username.setText("");
        mBinding.psw.setText("");
        mBinding.activeCode.setText("");
        mBinding.box1Blank.setVisibility(View.GONE);
        mBinding.box1Layout.setVisibility(View.GONE);
        mBinding.box2Blank.setVisibility(View.GONE);
        mBinding.box2Layout.setVisibility(View.GONE);
        mBinding.box3Blank.setVisibility(View.VISIBLE);
        mBinding.box3Layout.setVisibility(View.VISIBLE);
        mBinding.registerBtn.setVisibility(View.VISIBLE);
        mBinding.loginBtn.setVisibility(View.GONE);
    }
}
