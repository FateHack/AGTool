package com.fate.AGTool;

import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
/** 线程辅助处理类，用于在主线程和其他线程中执行逻辑 */
public class ThreadTool
{
    // 调用示例
    public static void Example()
    {
        ThreadTool.RunInMainThread(new ThreadPram()
        {
            @Override
            public void Function()
            {
                // TODO Auto-generated method stub
                // 在主线程执行逻辑
            }
        });
    }

    // ---------------------------------------------

    /** 线程辅助处理类对象参数 */
    public static abstract class ThreadPram
    {
        /** 需要在线程中执行的逻辑 */
        public abstract void Function();
    }

    /** 在主线程执行Function —— UI界面相关控件逻辑需在主线程中执行 */
    public static void RunInMainThread(final ThreadPram param)
    {
        getMainHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                param.Function();
            }
        });
    }

    /** 在主线程中延时delayMillis毫秒，执行Function —— UI界面相关控件逻辑需在主线程中执行 */
    public static void RunInMainThread(final ThreadPram param, long delayMillis)
    {
        getMainHandler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                param.Function();
            }
        }, delayMillis);
    }

    /** 在其他线程执行Function —— 网络请求需在主线程之外的其他线程执行 */
    public static void RunInCachedThread(final ThreadPram param)
    {
        Executors.newCachedThreadPool().execute(new Runnable()
        {
            @Override
            public void run()
            {
                param.Function();
            }
        });
    }

    /** 当前线程是否为主线程 */
    public static boolean isUiThread()
    {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /** 获取主线程Handler */
    public static Handler getMainHandler()
    {
        return new Handler(Looper.getMainLooper());
    }

    /** 获取当前线程Handler */
    public static Handler getCurrentHandler()
    {
        return new Handler(Looper.myLooper());
    }

}
