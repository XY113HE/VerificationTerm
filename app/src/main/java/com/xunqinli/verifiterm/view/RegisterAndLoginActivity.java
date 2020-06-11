package com.xunqinli.verifiterm.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.cons.Constant;
import com.xunqinli.verifiterm.databinding.ActivityRegandlogBinding;
import com.xunqinli.verifiterm.interf.RegAndLogInterf;
import com.xunqinli.verifiterm.model.ShowLoginBean;
import com.xunqinli.verifiterm.model.VersionBean;
import com.xunqinli.verifiterm.rxbus.RxBus;
import com.xunqinli.verifiterm.utils.AppHook;
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

        RxBus.getRxBus().toObservable(VersionBean.class)
                .compose(RegisterAndLoginActivity.this.<VersionBean>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VersionBean>() {
                    @Override
                    public void call(final VersionBean bean) {
                        if(!AppHook.get().currentActivity().getLocalClassName().equals(getActivity().getLocalClassName())){
                            return;
                        }
                        //TODO 显示确认弹窗
                        final UpdateDialog.Builder builder = new UpdateDialog.Builder();
                        builder.setContext(getActivity())
                                .setUpdateBinding()
                                .setListener(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        if(which == DialogInterface.BUTTON_POSITIVE){
                                            //TODO 从下载地址更新apk
                                            Intent intent = new Intent();
                                            intent.setAction("android.intent.action.VIEW");
                                            Uri downloadUrl = Uri.parse(bean.getData().getLink());
                                            intent.setData(downloadUrl);
                                            getActivity().startActivity(intent);
                                        }
                                    }
                                })
                                .show();
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
//        Log.e(TAG, "initView: 0001");
//        editor.putString(ACTIVE_CODE_S, "2060058143002").commit();
        String s = mySharedPreferences.getString(ACTIVE_CODE_S, "");
        if(TextUtils.isEmpty(s)){
            showRegister();
            Log.e(TAG, "initView: 0002");
        }else{
            showLogin();
            Log.e(TAG, "initView: 0003");
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

    @Override
    public void showInfo(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterAndLoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
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
