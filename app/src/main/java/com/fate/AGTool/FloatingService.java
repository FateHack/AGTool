package com.fate.AGTool;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FloatingService extends Service {
    public static final String ACTION = "action";
    public static final String SHOW = "show";
    public static final String HIDE = "hide";
    public static final String SHOWPROCESS = "showProcess";
    public static final String SHOWALLDIRS = "showAllDirs";
    public static final String SHOWALLFILES = "showAllFiles";
    public static final String MKDIR = "mkdir";
    private FloatingView mFloatingView;

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = new FloatingView(this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getStringExtra(ACTION);
            if (SHOW.equals(action)) {
                mFloatingView.show();
            } else if (HIDE.equals(action)) {
                mFloatingView.hide();
            } else if (SHOWPROCESS.equals(action)) {
                mFloatingView.showProcess();
            } else if (SHOWALLDIRS.equals(action)) {
                mFloatingView.showAllDirs();
            } else if (SHOWALLFILES.equals(action)) {
                mFloatingView.showAllFiles();
            }else if(MKDIR.equals(action)){
                mFloatingView.mkDir();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
