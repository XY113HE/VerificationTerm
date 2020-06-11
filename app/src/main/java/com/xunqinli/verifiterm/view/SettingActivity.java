package com.xunqinli.verifiterm.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.databinding.ActivitySettingBinding;
import com.xunqinli.verifiterm.interf.SettingInterf;
import com.xunqinli.verifiterm.model.VersionBean;
import com.xunqinli.verifiterm.rxbus.RxBus;
import com.xunqinli.verifiterm.utils.AppHook;
import com.xunqinli.verifiterm.viewmodel.SettingVM;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SettingActivity extends BaseActivity implements SettingInterf.MainView {
    private ActivitySettingBinding mMainBinding;
    private SettingVM mSettingVM;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        initRxBus();
    }

    private void initRxBus() {
        RxBus.getRxBus().toObservable(VersionBean.class)
                .compose(getActivity().<VersionBean>bindUntilEvent(ActivityEvent.DESTROY))
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
