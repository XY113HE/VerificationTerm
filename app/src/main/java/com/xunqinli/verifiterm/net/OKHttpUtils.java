package com.xunqinli.verifiterm.net;

import com.xunqinli.verifiterm.utils.Tools;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.xunqinli.verifiterm.cons.Constant.HTTP_ADDRESS;

public class OKHttpUtils {
    private static String TAG = "lmy_okhttputils";
    private static OkHttpClient client;
    public static synchronized void setClient(){
        if(client == null){
            client = new OkHttpClient();
        }
    }

    //设备激活
    public static void postActive(String activeCode, Callback callback){
        String method = "WTE/Active";
        String url = HTTP_ADDRESS + method;
        final Request request = new Request.Builder()
                .url(url)
                .header("ActiveCode", activeCode)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //用户登录
    public static void userLogin(String name, String psw, Callback callback){
        String method = "Wom/SSO";
        RequestBody formBody = new FormBody.Builder()
                .add("username", name)
                .add("password", Tools.getMD5(psw).toUpperCase())
                .build();
        Request request = new Request.Builder()
                .url(HTTP_ADDRESS + method)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //检测版本
    public static void checkVersion(Callback callback){
        String method = "WTE/New/Version";
        Request request = new Request.Builder()
                .url(HTTP_ADDRESS + method)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }
}
