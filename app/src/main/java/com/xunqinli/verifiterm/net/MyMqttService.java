package com.xunqinli.verifiterm.net;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.xunqinli.verifiterm.cons.Constant;
import com.xunqinli.verifiterm.model.VerificationModeBean;
import com.xunqinli.verifiterm.model.VerificationNotifyBean;
import com.xunqinli.verifiterm.rxbus.RxBus;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyMqttService extends Service {
    //已订阅
    private boolean hasS = false;
    //已连接
    public static boolean hasConnected = false;

    public static final String TAG = "lmy_mqtt";
    private static MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
    public String HOST = Constant.MQTT_ADDRESS;//服务器地址（协议+地址+端口号）
//    public String HOST = "tcp://192.169.0.18:1884";//服务器地址（协议+地址+端口号）
    public static String USERNAME = "admin";//用户名
    public static String PASSWORD = "public";//密码
    public static String PUBLISH_TOPIC = "FromAndroid";//发布主题
    //订阅的需要是可变且唯一的，保证上位机发送的传到指定的android端
    /**
     * 应该放在授权之后
     */
    public static final String TOPIC_ALERT = "wte/"+ Constant.ACTIVE_CODE +"/writeoff";//核销通知
    public static final String TOPIC_MODE = "wte/"+ Constant.ACTIVE_CODE +"/model";//核销确认模式更改
    public static String CLIENTID = Constant.MAC;//客户端ID，一般以客户端唯一标识符表示，这里用设备序列号表示
    //暂时不需要响应其它客户端信息
    //public static String RESPONSE_TOPIC = "WindowsSg";//响应主题
    //public static String idTypes = "234";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 发布 （模拟其他客户端发布消息）
     *
     * @param message 消息
     */
    public static void publish(String message) {
        Log.e(TAG, "publish: " + message);
        String topic = PUBLISH_TOPIC;
        Integer qos = 0;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    /**
     * 响应 （收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等）
     *
     * @param message 消息
     */
//    public void response(String message) {
//        String topic = RESPONSE_TOPIC;
//        Integer qos = 2;
//        Boolean retained = false;
//        try {
//            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
//            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 初始化
     */
    private void init() {
        String serverURI = HOST; //服务器地址（协议+地址+端口号）
        Log.e(TAG, "init: " + serverURI);
        Log.e(TAG, "init: " + CLIENTID);
        mqttAndroidClient = new MqttAndroidClient(this, serverURI, CLIENTID);
        mqttAndroidClient.setCallback(mqttCallback); //设置监听订阅消息的回调
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setCleanSession(true); //设置是否清除缓存
        mMqttConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(20); //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setUserName(USERNAME); //设置用户名
        mMqttConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + CLIENTID + "\"}";
        String topic = PUBLISH_TOPIC;
        int qos = 2;
        boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                mMqttConnectOptions.setWill(topic, message.getBytes(), qos, retained);
            } catch (Exception e) {
                Log.e(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        if (doConnect) {
            doClientConnection();
        }
    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (!mqttAndroidClient.isConnected() && isConnectIsNomarl()) {
            try {
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "doClientConnection: " + e.getMessage());
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            //Log.e(TAG, "当前网络名称：" + name);
            return true;
        } else {
            //Log.e(TAG, "没有可用网络");
            /*没有可用网络的时候，延迟3秒再尝试重连*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doClientConnection();
                }
            }, 3000);
            return false;
        }
    }

    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.e(TAG, "服务连接成功");
            //RxBus.getRxBus().post(new MQTTConnectSuccessBean());
            hasConnected = true;
            //Toast.makeText(MyMqttService.this, "连接成功", Toast.LENGTH_SHORT).show();
            try {
                //Log.e("lmy", "iMqttActionListener " + SUB_TOPIC);
                mqttAndroidClient.subscribe(new String[]{TOPIC_ALERT, TOPIC_MODE}, new int[]{0, 0});//订阅主题，参数：主题、服务质量
            } catch (MqttException e) {
                e.printStackTrace();
            }
//            MQTTBean bean = new MQTTBean();
//            bean.setCmd(GET_IDWAY);
//            bean.setMonitor_id("0");
//            bean.setResult("请求可用识别方式");
//            String json = new Gson().toJson(bean, MQTTBean.class);
//            publish(json);
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            hasConnected = false;
            if(!hasS) {
                hasS = true;
                Log.e(TAG, "服务连接失败");
            }
            //Toast.makeText(MyMqttService.this, "连接失败", Toast.LENGTH_SHORT).show();
            doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
        }
    };

    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String json = new String(message.getPayload());
            //TODO 暂时注销AES解密
            //String dataJson = Tools.decryptByAES(json);
            Log.e(TAG, "messageArrived: " + json);
            if(topic.equals(TOPIC_ALERT)){//核销通知
                RxBus.getRxBus().post(new Gson().fromJson(json, VerificationNotifyBean.class));
            }else if(topic.equals(TOPIC_MODE)){//更改核销确认模式
                RxBus.getRxBus().post(new Gson().fromJson(json, VerificationModeBean.class));
            }

//            MQTTBean bean = new Gson().fromJson(json, MQTTBean.class);
//            if (bean.getCmd().equals(GET_IDWAY)) {
//                idTypes = bean.getIdtype();
//            }
//            RxBus.getRxBus().post(bean);
            //收到其他客户端的消息后，响0应给对方告知消息已到达或者消息有问题等
            //response("message arrived");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            hasConnected = false;
            Log.e(TAG, "连接断开 ");
//            if(!MainActivity.activityDestroy) {
//                doClientConnection();//连接断开，重连
//            }
        }
    };

    @Override
    public void onDestroy() {
        try {
            mqttAndroidClient.disconnect(); //断开连接
            Log.e(TAG, "onDestroy: -------");
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}