package com.fate.AGTool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    static {
        try {
            // System.loadLibrary("nc");
        } catch (Exception e) {

        }

    }

    private static final String TAG = "Fuck";

    public Button btnCP;

    private ImageButton btnFindHook;

    private ImageButton btnExportAddr;

    private ImageButton btnDump;

    private ImageButton btnEsc;

    private ImageButton btnInject;

    private ImageButton btnAcc;

    private ImageButton btnDefault;

    private Button btnFloat;

    private List<Process> processes = new ArrayList<>();

    public static Process selectProcess;

    private Spinner processSpinner;

    public static String selectSoName;

    private ProgressBar progressBar;

    private ListView addrList;

    public static SocketClient client;

    private Process zygote;

    private Process zygote64;

    String[] addres;

    private static Map<String, Drawable> drawableMap;

    private static Map<String, String> appNameMap;

    private static Map<String, Drawable> userDrawableMap;

    private static Map<String, String> userAppNameMap;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private int mTouchStartX, mTouchStartY;//手指按下时坐标

    private WindowManager mwindow;

    private WindowManager.LayoutParams lparam;

    private ImageButton mbutton;

    private TextView text_process;

    private Integer dumpSoSize = 0;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.SYSTEM_OVERLAY_WINDOW"};

    private boolean isMove;

    private ImageButton hideBth;

    private WindowManager.LayoutParams mparam;

    private boolean isBinded = false;

    private static int lastX = 300;

    private static int lastY = 0;

    private boolean mIsLongPressed = false;

    private float mLastMotionX, mLastMotionY;

    private long lastDownTime;

    private static String dirPath;

    private List<String> alldir;

    private boolean isBindFanhui = false;

    private String saveAddrPath;

    private boolean isExport;

    public static int port = 0;

    private ImageButton btn_inject_open;

    private String inject_so_path;

    private ImageButton btnAddDir;

    public static Button btnMkdirCancel;

    public static Button btnMkdirConfirm;

    public static EditText mkDirName;

    public static TextView currentPath;

    public boolean isDumping = false;


    public native void inject(String soPath, String target);

    enum SENDTYPE {
        DUMPSO,
        EXPORTADDR,
        SELECTSO
    }

    SENDTYPE sendtype;
    TestFileObserver ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        MyService.haveRoot();
        verifyStoragePermissions(this); //读写权限
        CheckFloatViewPermission(); //悬浮窗权限
        new Thread(new Runnable() {  //获取应用列表app信息
            @Override
            public void run() {
                getAppList();
            }
        }).start();

        //随机数作为端口
        int max = 8000, min = 9000;
        port = (int) (Math.random() * (max - min) + min);

        startService(new Intent(getBaseContext(), MyService.class).putExtra(MyService.ACTION, MyService.START));

        //先启动服务 初始化mView
        Intent intent = new Intent(getApplicationContext(), FloatingService.class);
        startService(intent);

        //设置窗口参数
        showMain();

        btnFloat = (Button) findViewById(R.id.btnFloat);
        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnFloat.getText().toString().equals("开启")) {
                    showImageButton();
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                    initSocket();
                    btnFloat.setText("关闭");
                } else {
                    closeImageButton();

                    if (client != null) {
                        client.disconnect();
                    }
                    System.exit(0);
                }

            }
        });

        mparam = new WindowManager.LayoutParams();
        mparam.x = 100;
        mparam.y = 0;
        dirPath = "/sdcard";
        saveAddrPath = dirPath;

    }

    /**
     * 绑定控件
     */
    void bindView() {
        btnCP = (Button) FloatingView.mView.findViewById(R.id.btnCP);
        btnCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FloatingService.class);
                intent.putExtra(FloatingService.ACTION, FloatingService.SHOWPROCESS);
                startService(intent);
                showProcess();
            }
        });
        processSpinner = (Spinner) FloatingView.mView.findViewById(R.id.processSpinner);
        btnFindHook = (ImageButton) FloatingView.mView.findViewById(R.id.btnFindHook);
        btnDump = (ImageButton) FloatingView.mView.findViewById(R.id.btnDump);
        btnExportAddr = (ImageButton) FloatingView.mView.findViewById(R.id.btnExportAddr);
        btnAcc = (ImageButton) FloatingView.mView.findViewById(R.id.btnAcc);
        btnEsc = (ImageButton) FloatingView.mView.findViewById(R.id.btnEsc);
        btnInject = (ImageButton) FloatingView.mView.findViewById(R.id.btnInject);
        btnDefault = (ImageButton) FloatingView.mView.findViewById(R.id.btnDeault);
        addrList = (ListView) FloatingView.mView.findViewById(R.id.addrList);
        hideBth = (ImageButton) FloatingView.mView.findViewById(R.id.hideBtn);
        text_process = (TextView) FloatingView.mView.findViewById(R.id.text_process);
        progressBar = (ProgressBar) FloatingView.mView.findViewById(R.id.progress_bar_h);

        final View mid_default = FloatingView.mView.findViewById(R.id.mainmid);
        final View mid_esc = FloatingView.mView.findViewById(R.id.mainmid_esc);
        final View mid_inject = FloatingView.mView.findViewById(R.id.mainmid_inject);
        final View mid_acc = FloatingView.mView.findViewById(R.id.mainmid_acc);
        btnDefault.setBackgroundResource(R.drawable.mainbtn_pressed);
        btnFindHook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addres = null;
                if (selectProcess != null && selectSoName != null) {
                    sendFindHook(selectProcess.getPid(), selectSoName);

                } else {
                    Toast.makeText(getBaseContext(), "请选择进程及目标So", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectProcess != null && selectSoName != null) {
                    sendtype = SENDTYPE.DUMPSO;
                    showAllDirIntent();
                }
            }
        });

        btnExportAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectProcess != null && selectSoName != null) {
                    sendtype = SENDTYPE.EXPORTADDR;
                    showAllDirIntent();
                    isExport = true;
                }
            }
        });
        if (!isBindFanhui) {
            FloatingView.list_files_view.findViewById(R.id.btnFanHui).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dirPath.lastIndexOf("/") == 0) {
                        dirPath = "/";
                    } else {
                        dirPath = dirPath.substring(0, dirPath.lastIndexOf("/"));
                    }
                    showALlFile();
                }
            });
            FloatingView.list_files_view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FloatingView.fileDialog.dismiss();
                }
            });
            FloatingView.list_files_view.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isDumping) {
                        switch (sendtype) {
                            case DUMPSO:
                                sendDump(selectProcess.getPid(), dirPath + "/" + selectSoName);
                                progressBar.setProgress(0);
                                progressBar.setVisibility(View.VISIBLE);
                                isDumping = true;
                                break;
                            case EXPORTADDR: {
                                String path = dirPath + "/hook_addr.txt";
                                String content = "";
                                if (addres != null && addres.length > 0) {
                                    for (int i = 0; i < addres.length; i++) {
                                        content = content + addres[i] + "\n";
                                    }
                                    saveFile(path, content);
                                } else {
                                    Toast.makeText(getBaseContext(), "地址列表为空", Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                            case SELECTSO:
                                if (inject_so_path == null || inject_so_path.isEmpty()) {
                                    Toast.makeText(getBaseContext(), "请选择要注入的so", Toast.LENGTH_SHORT).show();
                                    FloatingView.fileDialog.dismiss();
                                    return;
                                }
                                if (selectProcess != null) {
                                    //sendInjectSo(selectProcess.getPid(), inject_so_path);
                                    copyFile(inject_so_path, "/data/data/" + selectProcess.getPackageName() + "/lib/"); //移动注入的so进lib目录
                                    String soname = "/data/data/" + selectProcess.getPackageName() + "/lib/" + selectSoName;//获取目标so的路径
                                    inject_so_path = inject_so_path.substring(inject_so_path.lastIndexOf('/') + 1); //获取注入so的名称
                                    inject(inject_so_path, soname); //注入so
                                    mvFile(soname, soname.replace(".so", "_bak.so")); //备份原来的so
                                    String mod_libPath = "/sdcard/" + soname.substring(soname.lastIndexOf('/') + 1); //获取修改后的so路径
                                    mvFile(mod_libPath, soname); //移动修改后的so到目标lib目录

                                } else {
                                    Toast.makeText(getBaseContext(), "请选择进程", Toast.LENGTH_SHORT).show();
                                    FloatingView.fileDialog.dismiss();
                                    return;
                                }
                                break;
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "正在dump，请耐心等待", Toast.LENGTH_SHORT).show();
                    }
                    FloatingView.fileDialog.dismiss();

                }
            });


        }


        hideBth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FloatingService.class);
                intent.putExtra(FloatingService.ACTION, FloatingService.HIDE);
                startService(intent);
                showImageButton();
            }
        });

        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mid_default.getVisibility() == View.GONE) {
                    mid_default.setVisibility(android.view.View.VISIBLE);
                }
                v.setBackgroundResource(R.drawable.mainbtn_pressed);
                btnAcc.setBackgroundResource(R.drawable.mainbt);
                btnEsc.setBackgroundResource(R.drawable.mainbt);
                btnInject.setBackgroundResource(R.drawable.mainbt);
                mid_esc.setVisibility(View.GONE);
                mid_acc.setVisibility(View.GONE);
                mid_inject.setVisibility(View.GONE);
            }
        });

        btnEsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mid_esc.getVisibility() == View.GONE) {
                    mid_esc.setVisibility(android.view.View.VISIBLE);
                }
                v.setBackgroundResource(R.drawable.mainbtn_pressed);
                btnAcc.setBackgroundResource(R.drawable.mainbt);
                btnDefault.setBackgroundResource(R.drawable.mainbt);
                btnInject.setBackgroundResource(R.drawable.mainbt);
                mid_default.setVisibility(View.GONE);
                mid_acc.setVisibility(View.GONE);
                mid_inject.setVisibility(View.GONE);
            }
        });
        btnInject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mid_inject.getVisibility() == View.GONE) {
                    mid_inject.setVisibility(android.view.View.VISIBLE);
                }
                v.setBackgroundResource(R.drawable.mainbtn_pressed);
                btnAcc.setBackgroundResource(R.drawable.mainbt);
                btnDefault.setBackgroundResource(R.drawable.mainbt);
                btnEsc.setBackgroundResource(R.drawable.mainbt);
                mid_esc.setVisibility(View.GONE);
                mid_acc.setVisibility(View.GONE);
                mid_default.setVisibility(View.GONE);
            }
        });
        btnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mid_acc.getVisibility() == View.GONE) {
                    mid_acc.setVisibility(android.view.View.VISIBLE);
                }
                v.setBackgroundResource(R.drawable.mainbtn_pressed);
                btnDefault.setBackgroundResource(R.drawable.mainbt);
                btnEsc.setBackgroundResource(R.drawable.mainbt);
                btnInject.setBackgroundResource(R.drawable.mainbt);
                mid_esc.setVisibility(View.GONE);
                mid_default.setVisibility(View.GONE);
                mid_inject.setVisibility(View.GONE);
            }
        });

        //注入选择的so
        btn_inject_open = FloatingView.mView.findViewById(R.id.btn_inject_open);
        btn_inject_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendtype = SENDTYPE.SELECTSO;
                inject_so_path = null;
                showAllFileIntent();
            }
        });

    }

    //监视文件
    class TestFileObserver extends FileObserver {

        // path 为 需要监听的文件或文件夹
        public TestFileObserver(String path) {
            super(path, FileObserver.ALL_EVENTS);
        }

        @Override
        public void onEvent(int event, String path) {
            // 如果文件修改了 打印出文件相对监听文件夹的位置
            if (event == FileObserver.MODIFY) {
                Log.d("Fuck", "被修改");
            }
        }
    }

    class FileThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int progress = 0;
            while (progressBar.getProgress() != 100) {
                long length = new File(dirPath + "/" + selectSoName).length();
                progress = (int) (length / dumpSoSize);
                progressBar.setProgress(progress);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void copyFile(String origPath, String targetPath) {
        MyService.execRootCmdSilent("cp " + origPath + " " + targetPath); //移动注入的so进lib目录
    }

    public void mvFile(String origPath, String targetPath) {
        MyService.execRootCmdSilent("mv " + origPath + " " + targetPath); //移动注入的so进lib目录
    }

    public void showAllDirIntent() {
        Intent intent = new Intent(getBaseContext(), FloatingService.class);
        intent.putExtra(FloatingService.ACTION, FloatingService.SHOWALLDIRS);
        startService(intent);
        isShowFiles = false;
        showALlFile();
    }

    public void showAllFileIntent() {
        Intent intent = new Intent(getBaseContext(), FloatingService.class);
        intent.putExtra(FloatingService.ACTION, FloatingService.SHOWALLFILES);
        startService(intent);
        isShowFiles = true;
        showALlFile();
    }

    /**
     * 保存文件
     *
     * @param path
     * @param content
     */
    public void saveFile(String path, String content) {
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes("utf-8"));
            fos.close();
            Toast.makeText(getBaseContext(), "地址保存在" + path, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取已安装应用列表
     */
    private void getAppList() {
        @SuppressLint("WrongConstant") UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                time - 1000 * 1000, time);
        PackageManager pm = getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        drawableMap = new HashMap<>();
        appNameMap = new HashMap<>();
        if (processes.size() > 0) {
            processes.clear();
        }
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                if (appList != null && appList.size() > 0) {
                    drawableMap.put(packageInfo.packageName, packageInfo.applicationInfo.loadIcon(getPackageManager()));
                    appNameMap.put(packageInfo.packageName, packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                }
            } else {
                // 系统应用
                drawableMap.put(packageInfo.packageName, packageInfo.applicationInfo.loadIcon(getPackageManager()));
                appNameMap.put(packageInfo.packageName, packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
            }
        }
    }

    /**
     * 显示悬浮按钮
     */
    private void showImageButton() {
        mwindow = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); //获取WindowManager对象
        lparam = new WindowManager.LayoutParams();
        mparam = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lparam.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            lparam.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        if (Settings.canDrawOverlays(this)) {
            mbutton = new ImageButton(getApplicationContext());
            mbutton.setBackgroundResource(R.drawable.icon);
            mbutton.setOnTouchListener(new View.OnTouchListener() {

                /**
                 * 监听拖动
                 * @param v
                 * @param event
                 * @return
                 */
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mIsLongPressed = false;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: //单击
                            isMove = false;
                            mTouchStartX = (int) event.getRawX();
                            mTouchStartY = (int) event.getRawY();
//                            mLastMotionX = event.getRawX();
//                            mLastMotionY = event.getRawY();
//                            lastDownTime = event.getDownTime();
                            break;
                        case MotionEvent.ACTION_MOVE: //拖动
                            //记录拖动后的坐标
                            int nowX = (int) event.getRawX();
                            int nowY = (int) event.getRawY();
                            //记录拖动前的坐标
                            int movedX = nowX - mTouchStartX;
                            int movedY = nowY - mTouchStartY;
                            if (movedX > 5 || movedY > 5) {
                                isMove = true;
                            }
                            mTouchStartX = nowX;
                            mTouchStartY = nowY;
                            lparam.x += movedX;
                            lparam.y += movedY;
                            lastX = lparam.x;
                            lastY = lparam.y;
                            mwindow.updateViewLayout(mbutton, lparam);
                            break;
                        case MotionEvent.ACTION_UP: //抬起
//                            mIsLongPressed = isLongPressed(mLastMotionX, mLastMotionY, event.getX(), event.getY(), lastDownTime, event.getEventTime(), 500);
//                            if (mIsLongPressed) {
//                                Log.d(TAG, "长按");
//                            } else {
//                                Log.d(TAG, "非长按");
//                            }
                            break;
                        case MotionEvent.ACTION_CANCEL: //取消
                            break;

                    }
                    return isMove;
                }
            });

            mbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), FloatingService.class);
                    intent.putExtra(FloatingService.ACTION, FloatingService.SHOW);
                    startService(intent);
                    if (!isBinded) {
                        bindView();
                        isBinded = true;
                    }
                    closeImageButton();
                }
            });
            //类型
            lparam.format = PixelFormat.RGBA_8888;
            lparam.gravity = Gravity.LEFT;
            lparam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            lparam.width = 120;
            //宽度
            lparam.height = 120;
            //高度
            lparam.x = lastX;
            lparam.y = lastY;
            mwindow.addView(mbutton, lparam);
        } else {
            Toast.makeText(getBaseContext(), "开启失败，请检查是否开启悬浮窗权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 关闭悬浮按钮
     */
    private void closeImageButton() {
        try {
            if (mwindow != null && mbutton != null) {
                mwindow.removeView(mbutton);
            }
        } catch (Exception e) {

        }

    }


    /**
     * 显示界面
     */
    void showMain() {
        FloatingView.mParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FloatingView.mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            FloatingView.mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        FloatingView.mParams.format = PixelFormat.RGBA_8888;
        FloatingView.mParams.gravity = Gravity.LEFT;
        FloatingView.mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        FloatingView.mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //宽度
        FloatingView.mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        //高度
        FloatingView.mParams.x = 0;
        FloatingView.mParams.y = 100;
    }

    /**
     * 获取所有进程
     */
    public void getAllProcess() {
        if (processes.size() > 0) {
            processes.clear();
        }
        String rootCmd = MyService.execRootCmd("ps -ef");
        String[] split = rootCmd.split("\n");
        userDrawableMap = new HashMap<>();
        userAppNameMap = new HashMap<>();
        List<Process> unkonowProcess = new ArrayList<>();
        for (String process : split) {
            String[] info = process.split(" ");
            List<String> infoList = new ArrayList<>();
            for (String ss : info) {
                if (!ss.equals("")) {
                    infoList.add(ss);
                }
            }
            if (infoList.get(infoList.size() - 1).equals("zygote")) {
                zygote = new Process();
                //只需要拿到zygote的pid
                zygote.setPid(infoList.get(1));
            } else if (infoList.get(infoList.size() - 1).equals("zygote64")) {
                zygote64 = new Process();
                //只需要拿到zygote64的pid
                zygote64.setPid(infoList.get(1));
            }
            if (zygote == null || zygote64 == null) {
                continue;
            }

            if (isZygoteFork(infoList.get(2))) {
                Process process1 = new Process();
                process1.setUser(infoList.get(0));
                process1.setPid(infoList.get(1));
                process1.setPackageName(infoList.get(infoList.size() - 1));
                process1.setPpid(infoList.get(2));
                try {
                    ApplicationInfo appInfo = getPackageManager().getPackageInfo(process1.getPackageName(), 0).applicationInfo;
                    if (appInfo != null) { //已安装
                        process1.setAppName(appInfo.loadLabel(getPackageManager()).toString());
                        process1.setIcon(appInfo.loadIcon(getPackageManager()));
                        process1.setApplicationInfo(appInfo);
                        processes.add(process1);
                        //根据user 查看对应的appName和图标
                        userDrawableMap.put(process1.getUser(), process1.getIcon());
                        userAppNameMap.put(process1.getUser(), process1.getAppName());
                    } else {  //未直接安装

                    }
                } catch (PackageManager.NameNotFoundException e) {
                    unkonowProcess.add(process1);
                    e.printStackTrace();
                }

            } else {

            }
        }
        for (Process process : unkonowProcess) {
            String user = process.getUser();
            if (process.getPackageName().contains(":")) {
                String packageName = process.getPackageName().split(":")[0];
                if (appNameMap.get(packageName) != null) {
                    process.setAppName(appNameMap.get(packageName) + "(" + process.getPackageName().replace(packageName + ":", "") + ")");
                    if (drawableMap.get(packageName) != null) {
                        process.setIcon(drawableMap.get(packageName));
                    }
                } else if (userAppNameMap.get(user) != null) {
                    process.setAppName(userAppNameMap.get(user) + "(" + process.getPackageName() + ")");
                    process.setIcon(userDrawableMap.get(user));
                } else {
                    process.setAppName(process.getPackageName());
                }
            } else {  //可能是虚拟机程序
                if (userAppNameMap.get(user) != null && userDrawableMap.get(user) != null) {
                    process.setAppName(userAppNameMap.get(user) + "(" + process.getPackageName() + ")");
                    process.setIcon(userDrawableMap.get(user));
                } else {
                    process.setAppName(process.getPackageName());
                }
            }
            processes.add(process);
        }
        Collections.sort(processes);
    }

    /**
     * 根据进程ID判断进程是否由zygote||zygote64孵化
     *
     * @param ppid
     * @return
     */
    public boolean isZygoteFork(String ppid) {
        if (zygote != null && zygote64 != null) {
            return ppid.equals(zygote.getPid()) || ppid.equals(zygote64.getPid());
        }
        return false;
    }

    //然后通过一个函数来申请
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.READ_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否开启上层显示权限
     */
    public void CheckFloatViewPermission() {
        if (!Settings.canDrawOverlays(this))//如果不可以绘制悬浮窗
        {
            Toast.makeText(this, "请开启悬浮窗权限", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        }
    }

    /**
     * 初始化socket
     */
    private void initSocket() {
        if (client == null) {
            SocketCallBack back = new SocketCallBack() {
                @Override
                public void Print(String info) {
                    showMsg(info);
                }
            };
            client = new SocketClient(back, "localhost", port);
            if (client != null) {
                client.start();
            }
        }
    }


    /**
     * 在信息显示区显示信息
     */
    private void showMsg(final String msg) {
        ThreadTool.RunInMainThread(new ThreadTool.ThreadPram() {
            @Override
            public void Function() {
                if (msg.contains("0x")) { //返回地址
                    addres = msg.split(",");
                    for (int i = 0; i < addres.length; i++) {
                        addres[i] = selectSoName + ":               地址:" + addres[i];
                    }
                    if (addres.length > 0) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_addr_item, addres);
                        addrList.setAdapter(adapter);
                    }
                } else if (msg.contains("Fate:")) {  //dump
                    String[] split = msg.split(":");
                    Toast.makeText(getBaseContext(), split[1], Toast.LENGTH_SHORT).show();
                    isDumping = false;
                } else if (msg.contains("So:")) {  //获取.so
                    String all_so = msg.replace("So:\n", "");//去掉消息头
                    final String[] list = all_so.split("\n");
                    final List<String> soList = new ArrayList<>();
                    for (String soName : list) {
                        if (soName.contains(selectProcess.getPackageName())) {
                            String substring = soName.substring(soName.lastIndexOf("/") + 1);
                            soList.add(substring);
                            if (msg.contains("libil2cpp.so") && !msg.contains("global-metadata")) {
                                soList.add("global-metadata.dat");
                            }
                        } else {

                        }
                    }
                    if (list != null) {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_select, soList);
                        arrayAdapter.setDropDownViewResource(R.layout.spinner_drop);
                        processSpinner.setAdapter(arrayAdapter);
                        processSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectSoName = soList.get(position);
                                addres = null;
                                addrList.setAdapter(null);
                                progressBar.setProgress(0);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        processSpinner.setAdapter(null);
                    }
                } else if (msg.contains("libsize")) {
                    String[] split = msg.split(":");
                    int progress = 0;
                    try {
                        progress = Integer.parseInt(split[1]);
                    } catch (Exception e) {
                        isDumping = false;
                    }
                    progressBar.setProgress(progress);

                }
            }
        });
    }


    /**
     * 显示进程
     */
    private void showProcess() {
        ListView listView = FloatingView.list_view.findViewById(R.id.appList);
        getAllProcess();
        final ProcessAdapter processAdapter = new ProcessAdapter();
        listView.setAdapter(processAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectProcess = new Process(processes.get(position));
                addrList.setAdapter(null);
                progressBar.setProgress(0);
                text_process.setText("[" + selectProcess.getPid() + "] " + selectProcess.getAppName());
                btnCP.setBackground(selectProcess.getIcon());
                sendGetAllSo(selectProcess.getPid());
                FloatingView.alertDialog.dismiss();
            }
        });
    }

    int fileIndex = -1;//记录文件窗口点击的索引
    boolean isHasBackround = false;

    /**
     * 显示所有文件夹
     */
    private void showALlFile() {
        inject_so_path = null;
        final ListView listView = FloatingView.list_files_view.findViewById(R.id.list_files);
        final TextView text_save_path = FloatingView.list_files_view.findViewById(R.id.text_save_path);
        btnAddDir = (ImageButton) FloatingView.list_files_view.findViewById(R.id.btnAddDir);
        btnAddDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), FloatingService.class).putExtra(FloatingService.ACTION, FloatingService.MKDIR));
                currentPath.setText(dirPath + "/");
                btnMkdirCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FloatingView.mkDirDialog.dismiss();
                    }
                });
                mkDirName.setText("");
                btnMkdirConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String dirName = mkDirName.getText().toString();
                        File dir = new File(dirPath + "/" + dirName);
                        dir.mkdir();
                        FloatingView.mkDirDialog.dismiss();
                        showALlFile(listView, text_save_path);

                    }
                });
            }
        });
        showALlFile(listView, text_save_path);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dirPath.equals("/")) {
                    String current = dirPath + alldir.get(position);
                    File file = new File(current);
                    if (file.isFile()) {
                        fileIndex = position;
                        inject_so_path = current;
                        //  Toast.makeText(getBaseContext(), "已选择" + current, Toast.LENGTH_SHORT).show();
                    } else {
                        dirPath += alldir.get(position);
                        showALlFile(listView, text_save_path);
                    }

                } else {
                    String current = dirPath + "/" + alldir.get(position);
                    File file = new File(current);
                    if (file.isFile()) {
                        fileIndex = position;
                        inject_so_path = current;

                        // Toast.makeText(getBaseContext(), "已选择" + current, Toast.LENGTH_SHORT).show();
                    } else {
                        dirPath = dirPath + "/" + alldir.get(position);
                        showALlFile(listView, text_save_path);
                    }
                }
            }
        });
    }

    boolean isShowFiles = true;

    private void showALlFile(ListView listView, TextView textView) {
        if (!isShowFiles) {
            alldir = Utils.getAllDirs(dirPath);
        } else {
            alldir = Utils.getAllFiles(dirPath);
        }
        textView.setText(dirPath);
        listView.setAdapter(new FileAdapter());
    }


    //检测用户是否对本app开启了“Apps with usage access”权限
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!hasPermission()) {
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }

    private void sendMessage(String message) {
        client.Send(message);
    }

    private void sendFindHook(String pid, String soName) {
        String message = "0\t" + pid + "\t" + soName;
        sendMessage(message);
    }

    private void sendDump(String pid, String soName) {
        String message = "1\t" + pid + "\t" + soName;
        sendMessage(message);

    }

    private void sendGetAllSo(String pid) {
        String message = "2\t" + pid;
        sendMessage(message);
    }

    private void sendInjectSo(String pid, String soname) {
        String message = "3\t" + pid + "\t" + soname;
        sendMessage(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.disconnect();
        }
        closeImageButton();
        startService(new Intent(getBaseContext(), MyService.class).putExtra(MyService.ACTION, MyService.CLOSE));
        System.exit(0);
    }

    class ProcessAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return processes.size();
        }

        @Override
        public Object getItem(int position) {
            return processes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.process_item, null);
            TextView appName = (TextView) itemView.findViewById(R.id.appName);
            ImageView appIcon = (ImageView) itemView.findViewById(R.id.appIcon);
            TextView appPid = (TextView) itemView.findViewById(R.id.appPid);
            appName.setText(processes.get(position).getAppName());
            appPid.setText("[" + processes.get(position).getPid() + "]");
            if (processes.get(position).getIcon() != null) {
                appIcon.setImageDrawable(processes.get(position).getIcon());
            } else {
                appIcon.setImageResource(R.drawable.ic_launcher_background);
            }
            return itemView;
        }
    }

    class FileAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return alldir.size();
        }

        @Override
        public Object getItem(int position) {
            return alldir.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, null);
            TextView file_name = itemView.findViewById(R.id.text_file_name);
            TextView file_time = itemView.findViewById(R.id.text_file_time);
            String fullPath = "";
            fullPath = dirPath + "/" + alldir.get(position);
            File currentFile = new File(fullPath);
            long time = currentFile.lastModified();
            String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(time));
            file_name.setText(alldir.get(position));
            file_time.setText(ctime);

            ImageView fileView = itemView.findViewById(R.id.fileIco);
            if (currentFile.isDirectory()) {
                fileView.setImageResource(R.drawable.ic_folder_outline_white_24dp);
            } else {
                fileView.setImageResource(R.drawable.ico_file);
            }
            if (position == fileIndex) {
                itemView.setBackgroundColor(Color.WHITE);

                fileIndex = -1;
            }

            return itemView;
        }
    }

}
