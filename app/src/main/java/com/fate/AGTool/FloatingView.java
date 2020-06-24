package com.fate.AGTool;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * 悬浮窗view
 */
public class FloatingView extends FrameLayout {
    public static Context mContext;

    public static View mView;


    public static WindowManager.LayoutParams mParams;

    private FloatingManager mWindowManager;

    private static final String TAG = "Fate";

    public static AlertDialog.Builder builder;

    public static AlertDialog alertDialog;

    public static View list_view;

    public static View list_files_view;

    public static AlertDialog fileDialog;

    public static AlertDialog mkDirDialog;

    public static View mkDirLayout;

    public FloatingView(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        mView = mLayoutInflater.inflate(R.layout.float_window, null);
        mWindowManager = FloatingManager.getInstance(mContext);
        builder = new AlertDialog.Builder(context);

        list_view = LayoutInflater.from(context).inflate(R.layout.list_view, null);

        list_files_view = LayoutInflater.from(context).inflate(R.layout.file_dialog, null);

        mkDirLayout = LayoutInflater.from(context).inflate(R.layout.mkdir_dialog, null);

        MainActivity.btnMkdirCancel=mkDirLayout.findViewById(R.id.btnMkdirCancel);

        MainActivity.btnMkdirConfirm=mkDirLayout.findViewById(R.id.btnMkdirConfirm);

        MainActivity.mkDirName=mkDirLayout.findViewById(R.id.mkDirName);

        MainActivity.currentPath=mkDirLayout.findViewById(R.id.currentPath);

        FloatingView.alertDialog = FloatingView.builder.create();
        fileDialog = new AlertDialog.Builder(context).create();
        mkDirDialog = new AlertDialog.Builder(context).create();
    }

    public void show() {
        mWindowManager.addView(mView, mParams);
    }

    public void showProcess() {

        FloatingView.alertDialog.setView(FloatingView.list_view);
        FloatingView.alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        FloatingView.alertDialog.show();
    }

    public void hide() {
        mWindowManager.removeView(mView);
    }

    /**
     * 显示文件夹下面的所有文件
     */
    public void showAllDirs() {
        FloatingView.fileDialog.setView(FloatingView.list_files_view);
        FloatingView.fileDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        FloatingView.fileDialog.show();
    }


    public void showAllFiles() {
        FloatingView.fileDialog.setView(FloatingView.list_files_view);
        FloatingView.fileDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        FloatingView.fileDialog.show();
    }

    public void mkDir() {
        mkDirDialog.setView(mkDirLayout);
        mkDirDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        mkDirDialog.show();
    }
}