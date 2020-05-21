package com.xunqinli.verifiterm.viewmodel;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.xunqinli.verifiterm.databinding.ActivityMainBinding;
import com.xunqinli.verifiterm.interf.MainInterf;
import com.xunqinli.verifiterm.model.DateTimeBean;
import com.xunqinli.verifiterm.model.VerificationModeBean;
import com.xunqinli.verifiterm.model.VerificationNotifyBean;
import com.xunqinli.verifiterm.net.UdpClient;
import com.xunqinli.verifiterm.rxbus.RxBus;
import com.xunqinli.verifiterm.sql.MySQLiteHelper;
import com.xunqinli.verifiterm.utils.ThreadPoolTools;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.xunqinli.verifiterm.cons.Constant.AUTO_MODE;
import static com.xunqinli.verifiterm.cons.Constant.HAND_MODE;
import static com.xunqinli.verifiterm.cons.Constant.VERF_MODE;

public class MainVM {
    private MainInterf.MainView mMainView;
    private ActivityMainBinding mBinding;
    private boolean isOpen = true;


    public MainVM(MainInterf.MainView mMainView, ActivityMainBinding mBinding){
        this.mMainView = mMainView;
        this.mBinding = mBinding;
        initClicks();
        initRxBus();
        //TODO test初始化数据库
        //initTestSqlData();
    }

    private void initTestSqlData() {
        ThreadPoolTools.getInstance().executorCommonThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    SystemClock.sleep(5000);
                    RxBus.getRxBus().post(new VerificationNotifyBean(i, "赵"+i, "辽A3333"+i, "1358765345"+i, "2020-05-20 20:20:2"+i, "洗车员"+i));
                }
            }
        });

    }


    private void initRxBus() {
        //核销记录
        RxBus.getRxBus().toObservable(VerificationNotifyBean.class)
                .compose((mMainView.getActivity()).<VerificationNotifyBean>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VerificationNotifyBean>() {
                    @Override
                    public void call(VerificationNotifyBean bean) {
                        //TODO 插入到数据库
                        MySQLiteHelper helper = new MySQLiteHelper(mMainView.getActivity());
                        long result = helper.insertRecord(bean);
                        Log.e("lmy", "call: " + result);
                        //TODO 显示数据到UI
                        mMainView.showVerfInfo(bean);
                        Log.e("lmy", "call: " +new Gson().toJson(bean, VerificationNotifyBean.class));
                        //TODO 响起提示音
                        mMainView.verfAlert();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

        //核销模式
        RxBus.getRxBus().toObservable(VerificationModeBean.class)
                .compose((mMainView.getActivity()).<VerificationModeBean>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VerificationModeBean>() {
                    @Override
                    public void call(VerificationModeBean bean) {
                        mMainView.getActivity().editor.putString(VERF_MODE, bean.getModel()).commit();
                        if(AUTO_MODE.equals(bean.getModel())){
                            mBinding.verfBtn.setVisibility(View.GONE);
                        }else if(HAND_MODE.equals(bean.getModel())){
                            mBinding.verfBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

        //实时时间
        RxBus.getRxBus().toObservable(DateTimeBean.class)
                .compose((mMainView.getActivity()).<DateTimeBean>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DateTimeBean>() {
                    @Override
                    public void call(DateTimeBean bean) {
                        mBinding.mainCurrentTime.setText(bean.getTime());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void initClicks() {
        RxView.clicks(mBinding.mainSettingBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mMainView.jump2setting();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxView.clicks(mBinding.verfHistoryBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //TODO TEST
                        mMainView.jump2history("history");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxView.clicks(mBinding.verfTodayBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //TODO TEST
                        mMainView.jump2history("today");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });

        RxView.clicks(mBinding.verfBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UdpClient udp = new UdpClient();
                                String ip = mMainView.getActivity().mySharedPreferences.getString("control_ip", "");
                                if(!TextUtils.isEmpty(ip)) {
                                    udp.InitSocket(ip);
                                    if(isOpen) {
                                        if ("关闭".equals(udp.SendCmd("查询"))) {
                                            udp.SendCmd("启动");
                                        } else {
                                            Log.e("lmy", "run: 已启动");
                                        }
                                    }else{
                                        if ("启动".equals(udp.SendCmd("查询"))) {
                                            udp.SendCmd("关闭");
                                        } else {
                                            Log.e("lmy", "run: 已关闭");
                                        }
                                    }
                                    isOpen = !isOpen;
                                }
                            }
                        }).start();

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }
}
