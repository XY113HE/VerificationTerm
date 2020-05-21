package com.xunqinli.verifiterm.interf;

import android.os.Bundle;

import com.xunqinli.verifiterm.view.BaseActivity;

public interface RegAndLogInterf {
    interface MainView extends BaseInterf.MainView{
        void jump2main(Bundle bundle);

        void showLogin();
    }
}
