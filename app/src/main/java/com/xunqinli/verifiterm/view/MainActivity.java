package com.xunqinli.verifiterm.view;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.databinding.ActivityMainBinding;
import com.xunqinli.verifiterm.interf.MainInterf;
import com.xunqinli.verifiterm.model.VerificationNotifyBean;
import com.xunqinli.verifiterm.net.MyMqttService;
import com.xunqinli.verifiterm.utils.AppHook;
import com.xunqinli.verifiterm.utils.Tools;
import com.xunqinli.verifiterm.viewmodel.HintSoundVM;
import com.xunqinli.verifiterm.viewmodel.MainVM;
import com.xunqinli.verifiterm.viewmodel.ShowDateVM;

import java.util.ArrayList;
import java.util.List;

import static com.xunqinli.verifiterm.cons.Constant.ACTIVE_CODE;
import static com.xunqinli.verifiterm.cons.Constant.ACTIVE_CODE_S;
import static com.xunqinli.verifiterm.cons.Constant.AUTO_MODE;
import static com.xunqinli.verifiterm.cons.Constant.HAND_MODE;
import static com.xunqinli.verifiterm.cons.Constant.VERF_MODE;

public class MainActivity extends BaseActivity implements MainInterf.MainView {
    private static final String TAG = "lmy_mainaty";
    private ActivityMainBinding mMainBinding;
    private MainVM mMainVM;
    private ShowDateVM mDateVM;
    private HintSoundVM mHintSoundVM;
    private Intent mqttIntent;
    private List<VerificationNotifyBean> verfInfoList = new ArrayList<>();
    private boolean wantToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserState();
        initDataBinding();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initView();
        ACTIVE_CODE = mySharedPreferences.getString(ACTIVE_CODE_S, "");
        if (!TextUtils.isEmpty(ACTIVE_CODE)) {
            startMQTTService();
        } else {
            Toast.makeText(this, "设备未激活", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void checkUserState() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!"1".equals(bundle.getString("state"))) {
                Toast.makeText(this, "该用户已被禁用", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            //TODO 测试直接打开主页的时候要去掉
            Toast.makeText(this, "bundle为空", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void startMQTTService() {
        mqttIntent = new Intent(this, MyMqttService.class);
        startService(mqttIntent);
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String name = bundle.getString("name");
            String phone = bundle.getString("phone");
            String state = bundle.getString("state");
            String code = bundle.getString("code");
            String deptCode = bundle.getString("deptCode");

            mMainBinding.nameUser.setText(name);
            mMainBinding.mainVersion.setText(Tools.getVersion(this));
        }


        //初始不显示右侧3个栏
        mMainBinding.infoLayout1.setVisibility(View.INVISIBLE);
        mMainBinding.infoLayout2.setVisibility(View.INVISIBLE);
        mMainBinding.infoLayout3.setVisibility(View.INVISIBLE);

    }

    private void initDataBinding() {
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMainVM = new MainVM(this, mMainBinding);
        mDateVM = new ShowDateVM();
        mHintSoundVM = new HintSoundVM(this);
    }

    @Override
    public BaseActivity getActivity() {
        return MainActivity.this;
    }

    @Override
    public void jump2setting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void jump2history(String tag) {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra("tag", tag);
        startActivity(intent);
    }

    @Override
    public void verfAlert() {
        mHintSoundVM.startSound();
    }

    //TODO 超过4个后的处理还未做 待确认文字
    // 暂时解决方案 后进的顶先进的
    // 自动模式，不显示是否已确认
    // 手动模式，显示是否确认状态，未确认的可以重新发起确认。
    @Override
    public void showVerfInfo(VerificationNotifyBean bean) {

        if (AUTO_MODE.equals(mySharedPreferences.getString(VERF_MODE, AUTO_MODE))) {
            bean.setConfirm(true);
        }
        //预留，可以使用数据库里的总核销数
        int count = 0;
        try {
            count = Integer.parseInt(mMainBinding.veriedCount.getText().toString());
        } catch (Exception e) {
            Log.e(TAG, "showVerfInfo: 获取已核销数量类型转换异常");
        }
        mMainBinding.veriedCount.setText((count + 1) + "");
        verfInfoList.add(0, bean);
        if (verfInfoList.size() > 1) {
            mMainBinding.mainLastVerfTime.setText(verfInfoList.get(1).getDateTime().trim().split(" ")[1].substring(0, 5));
        } else {
            mMainBinding.mainLastVerfTime.setText("- - : - -");
        }
        if (verfInfoList.size() == 1) {
            case1();
        } else if (verfInfoList.size() == 2) {
            case1();
            case2();
        } else if (verfInfoList.size() == 3) {
            case1();
            case2();
            case3();
        } else if (verfInfoList.size() == 4) {
            case1();
            case2();
            case3();
            case4();
        } else {
            case1();
            case2();
            case3();
            case4();
        }

        if (verfInfoList.size() == 2 && mMainBinding.infoLayout1.getVisibility() == View.INVISIBLE) {
            mMainBinding.infoLayout1.setVisibility(View.VISIBLE);
        }
        if (verfInfoList.size() == 3 && mMainBinding.infoLayout2.getVisibility() == View.INVISIBLE) {
            mMainBinding.infoLayout2.setVisibility(View.VISIBLE);
        }
        if (verfInfoList.size() == 4 && mMainBinding.infoLayout3.getVisibility() == View.INVISIBLE) {
            mMainBinding.infoLayout3.setVisibility(View.VISIBLE);
        }
        //Log.e(TAG, "showVerfInfo: " + new Gson().toJson(bean, VerificationNotifyBean.class));

    }

    @Override
    public void updateConfirmState(int i) {
        if (verfInfoList.size() > i-1) {
            verfInfoList.get(i).setConfirm(true);
        }
        if(i == 1){
            mMainBinding.infoLayout1.setBackgroundResource(R.drawable.main_card_info3);
            mMainBinding.mainVerfiState1.setText("已确认");
        }else if(i == 2){
            mMainBinding.infoLayout2.setBackgroundResource(R.drawable.main_card_info3);
            mMainBinding.mainVerfiState2.setText("已确认");
        }else if(i == 3){
            mMainBinding.infoLayout3.setBackgroundResource(R.drawable.main_card_info3);
            mMainBinding.mainVerfiState3.setText("已确认");
        }
    }

    private void case4() {
        if (verfInfoList.get(3).isConfirm()) {
            mMainBinding.infoLayout3.setBackgroundResource(R.drawable.main_card_info3);
            mMainBinding.mainVerfiState3.setText("已确认");
        } else {
            mMainBinding.infoLayout3.setBackgroundResource(R.drawable.main_card_info2);
            mMainBinding.mainVerfiState3.setText("待确认");
        }
        mMainBinding.info3Car.setText(getInfo("c", 3, verfInfoList));
        mMainBinding.info3Time.setText(getInfo("t", 3, verfInfoList));
        mMainBinding.info3User.setText(getInfo("n", 3, verfInfoList));
        mMainBinding.info3Phone.setText(getInfo("p", 3, verfInfoList));
        mMainBinding.mainVerfiRightCode3.setText(getInfo("d", 3, verfInfoList));
    }

    private void case3() {
        if (verfInfoList.get(2).isConfirm()) {
            mMainBinding.infoLayout2.setBackgroundResource(R.drawable.main_card_info3);
            mMainBinding.mainVerfiState2.setText("已确认");
        } else {
            mMainBinding.infoLayout2.setBackgroundResource(R.drawable.main_card_info2);
            mMainBinding.mainVerfiState2.setText("待确认");
        }
        mMainBinding.info2Car.setText(getInfo("c", 2, verfInfoList));
        mMainBinding.info2Time.setText(getInfo("t", 2, verfInfoList));
        mMainBinding.info2User.setText(getInfo("n", 2, verfInfoList));
        mMainBinding.info2Phone.setText(getInfo("p", 2, verfInfoList));
        mMainBinding.mainVerfiRightCode2.setText(getInfo("d", 2, verfInfoList));
    }

    private void case2() {
        if (verfInfoList.get(1).isConfirm()) {
            mMainBinding.infoLayout1.setBackgroundResource(R.drawable.main_card_info3);
            mMainBinding.mainVerfiState1.setText("已确认");
        } else {
            mMainBinding.infoLayout1.setBackgroundResource(R.drawable.main_card_info2);
            mMainBinding.mainVerfiState1.setText("待确认");
        }
        mMainBinding.info1Car.setText(getInfo("c", 1, verfInfoList));
        mMainBinding.info1Time.setText(getInfo("t", 1, verfInfoList));
        mMainBinding.info1User.setText(getInfo("n", 1, verfInfoList));
        mMainBinding.info1Phone.setText(getInfo("p", 1, verfInfoList));
        mMainBinding.mainVerfiRightCode1.setText(getInfo("d", 1, verfInfoList));
    }

    private void case1() {
        mMainBinding.currentInfoCar.setText(getInfo("c", 0, verfInfoList));
        mMainBinding.currentInfoTime.setText(getInfo("t", 0, verfInfoList));
        mMainBinding.currentInfoUser.setText(getInfo("n", 0, verfInfoList));
        mMainBinding.currentInfoPhone.setText(getInfo("p", 0, verfInfoList));
        mMainBinding.mainVerfiRightCode.setText(getInfo("d", 0, verfInfoList));
    }

    private String getInfo(String tag, int index, List<VerificationNotifyBean> list) {
        String result = "";
        switch (tag) {
            case "c":
                result = "车牌号码：" + list.get(index).getCarNum();
                break;
            case "t":
                result = "核销时间：" + list.get(index).getDateTime();
                break;
            case "n":
                result = "客户名称：" + list.get(index).getName();
                break;
            case "p":
                result = "手机号码：" + list.get(index).getPhone().substring(0, 3) + "****" + list.get(index).getPhone().substring(7);
                break;
            case "d":
                result = list.get(index).getNum() + "";
                break;
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private void exit() {
        if (wantToExit) {
            AppHook.get().finishAllActivity();
        } else {
            wantToExit = true;
            Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(2000);
                    wantToExit = false;
                }
            }).start();
        }
    }
}
