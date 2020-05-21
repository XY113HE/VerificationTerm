package com.xunqinli.verifiterm.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xunqinli.verifiterm.cons.Constant;
import com.xunqinli.verifiterm.utils.Tools;

public class BaseActivity extends RxAppCompatActivity implements LifecycleOwner {
    public SharedPreferences mySharedPreferences;
    public SharedPreferences.Editor editor;
    //创建Lifecycle对象
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.hideBottomUIMenu(getWindow());
        mySharedPreferences = getSharedPreferences(Constant.SHARE_PREFERENCE_SIGN, Activity.MODE_PRIVATE);
        editor = mySharedPreferences.edit();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //标记状态
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //标记状态
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //标记状态
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //标记状态
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        if(mLifecycleRegistry == null) {
            mLifecycleRegistry = new LifecycleRegistry(this);
            //标记状态
            mLifecycleRegistry.markState(Lifecycle.State.CREATED);
        }
        return mLifecycleRegistry;
    }

//屏蔽返回键
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//
//        }
//        return false;
//    }
}
