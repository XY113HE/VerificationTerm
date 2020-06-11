package com.xunqinli.verifiterm.viewmodel;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.xunqinli.verifiterm.databinding.ActivitySettingBinding;
import com.xunqinli.verifiterm.interf.SettingInterf;
import com.xunqinli.verifiterm.model.VersionBean;
import com.xunqinli.verifiterm.net.OKHttpUtils;
import com.xunqinli.verifiterm.rxbus.RxBus;
import com.xunqinli.verifiterm.utils.Tools;
import com.xunqinli.verifiterm.view.UpdateDialog;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
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
                        OKHttpUtils.checkVersion(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String json = response.body().string();
                                VersionBean b = new Gson().fromJson(json, VersionBean.class);
                                if(b.getCode() == 0 && b.getData() != null){
                                    float lastestVersion = Float.parseFloat(b.getData().getVersion());
                                    float currentVersion = Float.parseFloat(Tools.getVersion(mMainView.getActivity()));
                                    if(lastestVersion > currentVersion){
                                        RxBus.getRxBus().post(b);
                                    }
                                }
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }
}
