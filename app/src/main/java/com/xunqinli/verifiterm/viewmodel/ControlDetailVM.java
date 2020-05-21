package com.xunqinli.verifiterm.viewmodel;

import android.Manifest;
import android.net.wifi.ScanResult;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jakewharton.rxbinding.view.RxView;
import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.adapter.WifiListAdapter;
import com.xunqinli.verifiterm.databinding.ActivityControlDetailBinding;
import com.xunqinli.verifiterm.interf.ControlDetailInterf;
import com.xunqinli.verifiterm.net.UdpClient;
import com.xunqinli.verifiterm.utils.Tools;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.xunqinli.verifiterm.cons.Constant.UDP_IP;

public class ControlDetailVM {
    private ControlDetailInterf.MainView mMainView;
    private ActivityControlDetailBinding mBinding;
    private boolean wifiState = false;//false 未下拉  true 下拉
    private boolean pswState = true;//true 不可见  false 可见
    private int currentSelectionIndex = 0;//光标当前位置
    private boolean[] hasInputed = new boolean[]{false, false, false};//记录确认按钮的可点击状态
    private WifiListAdapter adapter;
    private List<ScanResult> data;

    public ControlDetailVM(ControlDetailInterf.MainView mMainView, ActivityControlDetailBinding mBinding) {
        this.mMainView = mMainView;
        this.mBinding = mBinding;
        initClicks();
        initEvents();
        initWifiListAdapter();
    }

    private void initWifiListAdapter() {
        adapter = new WifiListAdapter(null, mMainView.getActivity());
        mBinding.wifiListView.setAdapter(adapter);
        mBinding.wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBinding.wifiName.setText(data.get(position).SSID);
                hasInputed[0] = true;
                mBinding.wifiListBtn.performClick();
            }
        });
    }

    private void updateWifiList(){
        requestPermission();
        data = Tools.getWifiList(mMainView.getActivity().getApplicationContext());
        adapter.setWifiInfo(data);
    }

    public void requestPermission() {
        TedPermission.with(mMainView.getActivity())
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        //Log.e("lmy", "onPermissionGranted: ");
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(mMainView.getActivity(), "请开启定位权限", Toast.LENGTH_SHORT).show();
                        mMainView.getActivity().finish();
                    }
                })
                .check();
    }

    private void initEvents() {

        /**
         * 密码的显隐。并记录光标位置。
         */
        mBinding.wifiPswLook.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        currentSelectionIndex = mBinding.etWifiPsw.getSelectionEnd();
                        mBinding.etWifiPsw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        mBinding.etWifiPsw.setSelection(currentSelectionIndex);
                        break;
                    case MotionEvent.ACTION_UP:
                        currentSelectionIndex = mBinding.etWifiPsw.getSelectionEnd();
                        mBinding.etWifiPsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mBinding.etWifiPsw.setSelection(currentSelectionIndex);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
                return true;
            }


        });

        mBinding.etWifiPsw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hasInputed[1] = s.toString().length() > 0;
                if(hasInputed[0] && hasInputed[1] && hasInputed[2]){
                    mBinding.confirmBtn.setSelected(true);
                }else{
                    mBinding.confirmBtn.setSelected(false);
                }
            }
        });
        mBinding.etIpaddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hasInputed[2] = s.toString().length() > 0;
                if(hasInputed[0] && hasInputed[1] && hasInputed[2]){
                    mBinding.confirmBtn.setSelected(true);
                }else{
                    mBinding.confirmBtn.setSelected(false);
                }
            }
        });
//        RxView.clicks(mBinding.wifiPswLook)
//                .compose(mMainView.getActivity().<Void>bindToLifecycle())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        if(pswState) {
//
//                        }else{
//
//                        }
//                        pswState = !pswState;
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//
//                    }
//                });
    }

    private void initClicks() {


        RxView.clicks(mBinding.wifiListBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        updateWifiList();
                        if(wifiState) {
                            Glide.with(mMainView.getActivity())
                                    .load(R.drawable.wifi_down)
                                    .into(mBinding.wifiListBtn);
                            mBinding.wifiListView.setVisibility(View.GONE);
                        }else{
                            Glide.with(mMainView.getActivity())
                                    .load(R.drawable.wifi_up)
                                    .into(mBinding.wifiListBtn);
                            mBinding.wifiListView.setVisibility(View.VISIBLE);
                        }
                        wifiState = !wifiState;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxView.clicks(mBinding.confirmBtn)
                .compose(mMainView.getActivity().<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //如果按钮处于不可点击时候，可以返回，但是没必要
                        //if(!mBinding.confirmBtn.isSelected())return;
                        //TODO 给中间控制器发送数据
                        final String wifiname = mBinding.wifiName.getText().toString().trim();
                        final String wifipsw = mBinding.etWifiPsw.getText().toString().trim();
                        final String ip = mBinding.etIpaddress.getText().toString().trim();
                        if(TextUtils.isEmpty(wifiname) || TextUtils.isEmpty(wifipsw) || TextUtils.isEmpty(ip)){
                            Toast.makeText(mMainView.getActivity(), "请输入全部参数", Toast.LENGTH_SHORT).show();
                        }else {
                            String result = Tools.checkIpAddress(ip);
                            if(!result.equals("OK")){
                                Toast.makeText(mMainView.getActivity(), result, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    UdpClient udp = new UdpClient();
                                    udp.InitSocket("192.168.4.1");
                                    String flag = udp.SetWiFI(wifiname, wifipsw, ip);
                                    mMainView.getActivity().mySharedPreferences.edit().putString("control_ip", ip).commit();
                                    Log.e("lmy", "call: " + wifiname);
                                    Log.e("lmy", "call: " + wifipsw);
                                    Log.e("lmy", "call: " + ip);
                                    Log.e("lmy", "call: " + flag);
                                }
                            }).start();

//                            SendUtils.sendSetting(wifiname, wifipsw, ip);
                            Toast.makeText(mMainView.getActivity(), "设置参数发送成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });

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
