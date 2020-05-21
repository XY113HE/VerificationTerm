package com.xunqinli.verifiterm.utils;

import android.util.Log;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolTools {

    //线程池实例
    private static ThreadPoolTools instance;
    //普通线程池
    private ThreadPoolExecutor commonPool;
    //网络线程池
    private ThreadPoolExecutor netPool;
    //普通线程队列
    private PriorityBlockingQueue commonThreadQueue;
    //网络线程队列
    private PriorityBlockingQueue netThreadQueue;
    private final int commonThreadCount = 19;
    private final int netThreadCount = 5;

    public static ThreadPoolTools getInstance(){
        if (instance == null) {
            instance = new ThreadPoolTools();
        }
        return instance;
    }

    //获取普通线程池
    public ThreadPoolExecutor getCommonPool() {
        return commonPool;
    }
    //获取网络线程池
    public ThreadPoolExecutor getNetPool() {
        return netPool;
    }

    public void executorCommonThread(Runnable task){
        if (!commonPool.isShutdown()) {
            try {
                commonPool.execute(task);
            }catch (Exception e){
                Log.e("lmyvideo", "executorCommonThread: " + e.getMessage()+"----"+e.getCause()+"--"+e.getLocalizedMessage());
            }
        }
    }

    public void executorNetThread(Runnable task){
        if (!netPool.isShutdown()) {
            netPool.execute(task);
        }
    }

    private ThreadPoolTools(){
        int corePoolSize = Runtime.getRuntime().availableProcessors()+1;
        int maximumPoolSize = corePoolSize * 2+1;
        //打印一下最大核心线程数
//        Log.e("lmy", "maxThreadPoolTools = " + maximumPoolSize);
        final long keepAliveTime = 60L;
        commonThreadQueue = new PriorityBlockingQueue(commonThreadCount);
        netThreadQueue = new PriorityBlockingQueue(netThreadCount);

        commonPool = new ThreadPoolExecutor(commonThreadCount, Integer.MAX_VALUE, keepAliveTime, TimeUnit.MILLISECONDS, commonThreadQueue);
        netPool = new ThreadPoolExecutor(netThreadCount, netThreadCount, 0L, TimeUnit.MILLISECONDS, netThreadQueue);
    }





//    private int corePoolSize = Runtime.getRuntime().availableProcessors();
//    private int maximumPoolSize = corePoolSize * 2+1;
//    private int keepAliveTime = 1;
//    private TimeUnit unit = TimeUnit.SECONDS;
//    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
//    private ExecutorService executorService;
//
//    private static class ThreadPoolToolsHolder {
//        private static final ThreadPoolTools INSTANCE = new ThreadPoolTools();
//    }
//
//    private ThreadPoolTools() {
//        corePoolSize = Runtime.getRuntime().availableProcessors()+1;
//        maximumPoolSize = corePoolSize * 2+1;
//        keepAliveTime = 1;
//        unit = TimeUnit.SECONDS;
//        workQueue = new LinkedBlockingQueue<Runnable>();
//        executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
//                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
//    }
//
//    public static final ThreadPoolTools getInstance() {
//        return ThreadPoolToolsHolder.INSTANCE;
//    }
//
//    public ExecutorService getExecutorService() {
//        return executorService;
//    }
}
