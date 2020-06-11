package com.xunqinli.verifiterm.viewmodel;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.xunqinli.verifiterm.databinding.ActivityRegandlogBinding;
import com.xunqinli.verifiterm.interf.RegAndLogInterf;
import com.xunqinli.verifiterm.model.BaseBean;
import com.xunqinli.verifiterm.model.RegisterBean;
import com.xunqinli.verifiterm.model.ShowLoginBean;
import com.xunqinli.verifiterm.model.UserLoginBean;
import com.xunqinli.verifiterm.model.VersionBean;
import com.xunqinli.verifiterm.net.OKHttpUtils;
import com.xunqinli.verifiterm.rxbus.RxBus;
import com.xunqinli.verifiterm.utils.AppHook;
import com.xunqinli.verifiterm.utils.Tools;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RegisterAndLoginVM {
    private static final String TAG = "lmy_randl";
    private RegAndLogInterf.MainView mMainView;
    private ActivityRegandlogBinding mBinding;

    public RegisterAndLoginVM(RegAndLogInterf.MainView mainView, ActivityRegandlogBinding binding) {
        this.mMainView = mainView;
        this.mBinding = binding;
        OKHttpUtils.setClient();
        checkVersion();
        initClicks();
    }

    private void checkVersion() {
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

    private void initClicks() {
        RxView.clicks(mBinding.registerBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        final String code = mBinding.activeCode.getText().toString().trim();
                        if (TextUtils.isEmpty(code)) {
                            Toast.makeText(mMainView.getActivity(), "请输入激活码", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        OKHttpUtils.postActive(code, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e(TAG, "onFailure: " + e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (!response.isSuccessful()) {
                                    mMainView.showInfo("服务器错误1");
                                } else {
                                    //Log.e(TAG, "onResponse2: " + response.body().string());
                                    /**
                                     * {
                                     * code:0, //code：响应码 0成功 4001设备激活失败 5002服务端响应异常 4002没有找到用户
                                     * msg:ok, //msg：响应描述
                                     * data:{} //data：接口返回的业务数据
                                     * }
                                     */
                                    final RegisterBean b = new Gson().fromJson(response.body().string(), RegisterBean.class);
                                    boolean result = "ok".equals(b.getMsg());
                                    //UI变更
                                    if (result) {
                                        RxBus.getRxBus().post(new ShowLoginBean());
                                        //mMainView.showLogin();
                                        // 激活码的存储(sp)
                                        mMainView.getActivity().editor.putString("active_code", code).commit();
                                    } else {
                                        mMainView.showInfo(b.getMsg());
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

        RxView.clicks(mBinding.loginBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String username = mBinding.username.getText().toString().trim();
                        String psw = mBinding.psw.getText().toString().trim();
                        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(psw)) {
                            Toast.makeText(mMainView.getActivity(), "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        OKHttpUtils.userLogin(username, psw, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e(TAG, "onFailure: " + e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (!response.isSuccessful()) {
                                    mMainView.showInfo("服务器错误2");
                                } else {
                                    //Log.e(TAG, "onResponse2: " + response.body().string());
                                    //RxBus.getRxBus().post(new Gson().fromJson(response.body().string(), UserLoginBean.class));
                                    /**
                                     * {
                                     * code:0, //code：响应码 0成功 4001设备激活失败 5002服务端响应异常 4002没有找到用户
                                     * msg:ok, //msg：响应描述
                                     * data:{} //data：接口返回的业务数据
                                     * }
                                     */
                                    try {

                                            final UserLoginBean bean = new Gson().fromJson(response.body().string(), UserLoginBean.class);
                                            if(bean.getCode() == 0){
                                                Bundle bundle = new Bundle();
                                                bundle.putString("name", bean.getData().getName());
                                                bundle.putString("phone", bean.getData().getPhone());
                                                bundle.putString("state", bean.getData().getState());
                                                bundle.putString("code", bean.getData().getCode());
                                                bundle.putString("deptCode", bean.getData().getDeptCode());
                                                mMainView.jump2main(bundle);
                                            }else{
                                                mMainView.showInfo(bean.getMsg());
                                            }


                                    } catch (Exception e) {
                                        try {
                                            final BaseBean bean = new Gson().fromJson(response.body().string(), BaseBean.class);
                                            mMainView.showInfo(bean.getMsg());
                                        }catch (Exception eInner){
                                            Log.e(TAG, "onResponse: " + eInner.getMessage());
                                        }
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
