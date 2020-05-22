package com.xunqinli.verifiterm.viewmodel;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.xunqinli.verifiterm.databinding.ActivitySettingBinding;
import com.xunqinli.verifiterm.interf.SettingInterf;
import com.xunqinli.verifiterm.utils.Tools;
import com.xunqinli.verifiterm.view.UpdateDialog;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class SettingVM {
    private static final String TAG = "lmy_setting";
    private ActivitySettingBinding mBinding;
    private SettingInterf.MainView mMainView;
    public SettingVM(ActivitySettingBinding mBinding, SettingInterf.MainView mMainView){
        this.mBinding = mBinding;
        this.mMainView = mMainView;
        initClicks();
    }

    private void initClicks() {
        RxView.clicks(mBinding.returnBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //TODO TEST
                        mMainView.getActivity().finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });

        RxView.clicks(mBinding.enterControlDetail)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mMainView.jump2controlDetail();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });

        RxView.clicks(mBinding.checkUpdate)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //TODO 检查更新
                        //TODO 获取当前最新版本号的接口和本地版本号对比
                        String netVersion = Tools.getVersion(mMainView.getActivity());
                        if(Float.parseFloat(Tools.getVersion(mMainView.getActivity())) < Float.parseFloat(netVersion)){
                            //TODO 显示确认弹窗
                            final UpdateDialog.Builder builder = new UpdateDialog.Builder();
                            builder.setContext(mMainView.getActivity())
                                    .setUpdateBinding()
                                    .setListener(new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            if(which == DialogInterface.BUTTON_POSITIVE){
                                                //TODO 从下载地址更新apk
                                                Intent intent = new Intent();
                                                intent.setAction("android.intent.action.VIEW");
                                                Uri downloadUrl = Uri.parse("https://www.appdy8.com/native/platform/dy/cp.apk");
                                                intent.setData(downloadUrl);
                                                mMainView.getActivity().startActivity(intent);
                                            }
                                        }
                                    })
                                    .show();
                        }else{
                            Toast.makeText(mMainView.getActivity(), "当前版本v"+Tools.getVersion(mMainView.getActivity())+"已是最新版本", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }
}
