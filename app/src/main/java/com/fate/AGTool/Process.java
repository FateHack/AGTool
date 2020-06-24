package com.fate.AGTool;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class Process implements Comparable {
    private String packageName;
    private String appName;
    private Drawable icon;
    private String pid;
    private ApplicationInfo applicationInfo;
    private String ppid;
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPpid() {
        return ppid;
    }

    public void setPpid(String ppid) {
        this.ppid = ppid;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Process(Process process) {
        this.setIcon(process.getIcon());
        this.setAppName(process.getAppName());
        this.setPackageName(process.getPackageName());
        this.setPid(process.getPid());
        this.setUser(process.user);
    }

    public Process() {

    }

    @Override
    public int compareTo(Object o) {
        return this.getAppName().compareTo(((Process) o).getAppName());
    }
}
