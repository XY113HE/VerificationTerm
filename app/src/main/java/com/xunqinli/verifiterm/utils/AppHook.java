package com.xunqinli.verifiterm.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.fragment.app.DialogFragment;

import com.gun0912.tedpermission.TedPermissionActivity;
import com.xunqinli.verifiterm.BuildConfig;

import java.util.Iterator;
import java.util.Stack;

public final class AppHook {
    private AppHook() {
    }

    private int appCount = 0;

    public static AppHook get() {
        return AppHolder.IMPL;
    }

    private static class AppHolder {
        private final static AppHook IMPL = new AppHook();
    }

    private Application mApplication;

    private final Stack<Activity> activityStack = new Stack<>();
    private final Stack<DialogFragment> fragmentStack = new Stack<>();

    private Application.ActivityLifecycleCallbacks callbacksCompat;

    public void ensureApplication(Application application) {
        if (mApplication == null) {
            mApplication = application;
        }
    }

    public void hookApplicationWatcher(Application application) {
        ensureApplication(application);
        if (BuildConfig.DEBUG) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .penaltyDeath()
                    .detectActivityLeaks()   //检测Activity泄漏
                    .detectLeakedSqlLiteObjects()//检测数据库泄漏
                    .detectLeakedClosableObjects();//检测Closeable对象泄漏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                builder.detectLeakedRegistrationObjects();//检测注册对象的泄漏
            }

            StrictMode.setVmPolicy(builder.penaltyLog().build());
        }
        application.registerActivityLifecycleCallbacks(callbacksCompat = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if ( activity instanceof TedPermissionActivity) {
//                    return;
                } else {
                    joinActivity(activity);
                }


            }

            @Override
            public void onActivityStarted(Activity activity) {
                appCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                appCount--;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if ( activity instanceof TedPermissionActivity) {
//                    return;
                } else {
                    popActivity(activity);
                }
            }
        });
    }

    //通过ActivityLifecycleCallbacks来批量统计Activity的生命周期，来做判断，此方法在API 14以上均有效，但是需要在Application中注册此回调接口
    public boolean getApplicationValue() {

        return appCount > 0;

    }

    //    public void registerSupportFragmentLifecycleCallback(FragmentLifecycleCallback callback){
//        FragmentLifecycleDispatcher.getDefault().registerLifecycleCallback(callback);
//    }
//
//    public void unRegisterSupportFragmentLifecycleCallback(FragmentLifecycleCallback callback){
//        FragmentLifecycleDispatcher.getDefault().unRegisterLifecyclerCallback(callback);
//    }
    public int getStackCount() {
        return activityStack.size();
    }

    public void onTerminate(Application application) {
        try {
            finishAllActivity();
            if (callbacksCompat != null) {
                application.unregisterActivityLifecycleCallbacks(callbacksCompat);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        } catch (Exception ignored) {
            Log.e("AppHook", "onTerminate " + ignored.getMessage());
        }
    }

    public void appExit(boolean killProcess) {
        try {
            finishAllActivity();
            if (killProcess) {
                if (callbacksCompat != null) {
                    mApplication.unregisterActivityLifecycleCallbacks(callbacksCompat);
                    callbacksCompat = null;
                }
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        } catch (Exception ignored) {
            Log.e("AlgorithmModule", "get_md5_value " + ignored.getMessage());
        }
    }

    public static void joinActivity(Activity activity) {
        get().activityStack.add(activity);
            Log.e("add activity:" , activity.getClass().getName());
    }

    public Activity currentActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        return activityStack.peek();
    }

    public void finishActivity() {
        if (activityStack.isEmpty()) {
            return;
        }
        Activity activity = activityStack.pop();
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            this.activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            this.activityStack.remove(activity);
                Log.e("remove activity:" , activity.getClass().getName());
        }
    }

    public void finishActivity(Class<? extends Activity> cls) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().equals(cls)) {
                iterator.remove();
                if (!activity.isFinishing()) {
                    activity.finish();
                    return;
                }
            }
        }
    }

    public void finishOtherActivity(Class<? extends Activity> cls) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
                if (!activity.getClass().equals(cls)) {
                    activity.finish();
                    iterator.remove();
                }
        }
    }

    public void finishOtherActivity(Class<? extends Activity> cls, Class<? extends Activity> cls2) {
        Iterator<Activity> iterator = activityStack.iterator();

        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if ((activity.getClass().equals(cls)) || (activity.getClass().equals(cls2))) {
            } else {
                iterator.remove();
                activity.finish();
            }
//            }
        }
    }

    public void finishAllActivity() {
        while (!activityStack.isEmpty()) {
            Activity activity = activityStack.pop();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public Activity getActivity(Class<? extends Activity> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    public boolean checkApplication() {
        if (mApplication == null) {
            return false;
        }
        return true;
    }

    public static <App extends Application> App getApp() {
        Application app = get().mApplication;
        if (app == null) {
            Activity activity = get().currentActivity();
            if (activity == null) {
                return null;
            }
            app = activity.getApplication();
            get().ensureApplication(app);
        }
        return (App) app;
    }

    public String dumpStackInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Activity activity : activityStack) {
            stringBuilder.append(activity.getClass().getName()).append("\n");
        }
        return stringBuilder.toString();
    }

}

