package com.xunqinli.verifiterm.interf;

import com.xunqinli.verifiterm.model.VerificationNotifyBean;

public interface MainInterf {
    interface MainView extends BaseInterf.MainView{

        void jump2setting();

        void jump2history(String tag);

        void verfAlert();

        void showVerfInfo(VerificationNotifyBean bean);
    }
}
