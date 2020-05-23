package com.xunqinli.verifiterm.interf;

import android.os.Bundle;

public interface RegAndLogInterf {
    interface MainView extends BaseInterf.MainView{
        void jump2main(Bundle bundle);

        void showLogin();

        void showInfo(String msg);
    }
}
