package com.xunqinli.verifiterm.viewmodel;

import android.os.SystemClock;
import android.util.Log;

import com.xunqinli.verifiterm.model.DateTimeBean;
import com.xunqinli.verifiterm.rxbus.RxBus;
import com.xunqinli.verifiterm.utils.ThreadPoolTools;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//控制显示时间（网路、本地）
public class ShowDateVM {
    public static boolean isGotNetTime = false;
    public static long timeDiff = 0;

    public ShowDateVM(){
        startListening();
    }

    private void startListening() {
        ThreadPoolTools.getInstance().executorCommonThread(new Runnable() {
            @Override
            public void run() {
                while (true) {
//                    getLocalTime();
                    if (!isGotNetTime) {
                        isGotNetTime = true;
                        getNetTime();
                    }else{
                        setTime();
                    }
                    SystemClock.sleep(1000);
                }
            }
        });
    }

    private void setTime() {
        DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeDiff + System.currentTimeMillis());
        final String time = format.format(calendar.getTime());
//        Log.e("lmy", "setTime: " + time);
        RxBus.getRxBus().post(new DateTimeBean(time));
    }

    private void getNetTime() {
        URL url = null;//取得资源对象
        try {
                    url = new URL("https://www.baidu.com");
//            url = new URL("http://www.qq.com");
//            url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
//            url = new URL("http://www.bjtime.cn");
            URLConnection connection = url.openConnection();//生成连接对象
            connection.connect(); //发出连接
            long ld = connection.getDate(); //取得网站日期时间
            if(ld <= 946656001000L){  //2000-01-01 00:00:01 时间戳 946656001000
                throw new Exception();
            }
            timeDiff = ld - System.currentTimeMillis();
            DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            final String time = format.format(calendar.getTime());
//            Log.e("lmy", "getNetTime: " + time);
            RxBus.getRxBus().post(new DateTimeBean(time));

        } catch (Exception e) {
            e.printStackTrace();
            SystemClock.sleep(1000);
            getNetTime();
        }
    }
}
