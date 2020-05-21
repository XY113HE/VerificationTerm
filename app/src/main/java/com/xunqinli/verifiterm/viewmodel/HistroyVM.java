package com.xunqinli.verifiterm.viewmodel;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.xunqinli.verifiterm.databinding.ActivityHistoryBinding;
import com.xunqinli.verifiterm.interf.DateSelectListener;
import com.xunqinli.verifiterm.interf.HistroyInterf;
import com.xunqinli.verifiterm.model.RefreshHistroyBean;
import com.xunqinli.verifiterm.rxbus.RxBus;
import com.xunqinli.verifiterm.view.DateSelectDialog;

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
        RxView.clicks(mBinding.selectDateBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        DateSelectDialog.Builder builder = new DateSelectDialog.Builder();
                        builder.setContext(mMainView.getActivity())
                                .setBinding()
                                .setListener(new DateSelectListener() {
                                    @Override
                                    public void click(long specialCode) {
                                        mMainView.selectDate(specialCode);
                                    }

                                    @Override
                                    public void clickAll() {
                                        mMainView.selectAllDate();
                                    }
                                })
                                .show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }
}
