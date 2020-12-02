package com.fate.AGTool;

public class ModItem {
    private String selectSoName;
    private String offset;
    private String code;
    private String pid;
    private String backupCode;

    public ModItem(String selectSoName, String offset, String code, String pid) {
        this.selectSoName = selectSoName;
        this.offset = offset;
        this.code = code;
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getBackupCode() {
        return backupCode;
    }

    public void setBackupCode(String backupCode) {
        this.backupCode = backupCode;
    }



    public ModItem(String selectSoName, String offset, String code) {
        this.selectSoName = selectSoName;
        this.offset = offset;
        this.code = code;
    }

    public String getSelectSoName() {
        return selectSoName;
    }

    public void setSelectSoName(String selectSoName) {
        this.selectSoName = selectSoName;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
