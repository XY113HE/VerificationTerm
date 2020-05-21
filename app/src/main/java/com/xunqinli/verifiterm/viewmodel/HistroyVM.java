package com.xunqinli.verifiterm.viewmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.xunqinli.verifiterm.databinding.ActivityHistoryBinding;
import com.xunqinli.verifiterm.interf.HistroyInterf;
import com.xunqinli.verifiterm.model.RefreshHistroyBean;
import com.xunqinli.verifiterm.model.VerificationNotifyBean;
import com.xunqinli.verifiterm.rxbus.RxBus;
import com.xunqinli.verifiterm.sql.MySQLiteHelper;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class HistroyVM {
    private HistroyInterf.MainView mMainView;
    private ActivityHistoryBinding mBinding;
    public HistroyVM(HistroyInterf.MainView mMainView, ActivityHistoryBinding mBinding){
        this.mMainView = mMainView;
        this.mBinding = mBinding;
        initClicks();
        initRxBus();
    }

    private void initRxBus() {
        RxBus.getRxBus().toObservable(RefreshHistroyBean.class)
                .compose((mMainView.getActivity()).<RefreshHistroyBean>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RefreshHistroyBean>() {
                    @Override
                    public void call(RefreshHistroyBean bean) {
                        mMainView.refreshData(bean.getList());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

    }


    private void initClicks() {
        RxView.clicks(mBinding.returnBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
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
    }
}
