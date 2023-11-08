package com.example.assign_mobilicis;

import android.widget.ImageView;

public class Tests {
    int icon,insetIcon;
    String testName,testDesc;
    private boolean isExpanded;

    public Tests(int icon,int insetIcon, String testName, String testDesc) {
        this.icon = icon;
        this.testName = testName;
        this.testDesc = testDesc;
        this.isExpanded=false;
        this.insetIcon=insetIcon;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestDesc() {
        return testDesc;
    }

    public void setTestDesc(String testDesc) {
        this.testDesc = testDesc;
    }
}
