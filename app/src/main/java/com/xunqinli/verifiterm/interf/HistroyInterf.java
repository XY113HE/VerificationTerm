package com.xunqinli.verifiterm.interf;

import com.xunqinli.verifiterm.model.VerificationNotifyBean;

import java.util.List;

public interface HistroyInterf {
    interface MainView extends BaseInterf.MainView{

        void refreshData(List<VerificationNotifyBean> data);
    }
}
