package com.sprd.validationtools.modules;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.text.TextUtils;

public class TestItem {

    private String testName;
    private String testPackageName;
    private String testClassName;
    private int testResult;
    private int labelResId = 0;

    public TestItem(String testName_, String testPackageName_,
            String testClassName_, int testResult_, int labelResId_) {
        testName = testName_;
        testPackageName = testPackageName_;
        testClassName = testClassName_;
        testResult = testResult_;
        labelResId = labelResId_;
    }

    public String getTestName() {
        return testName;
    }

    public String getTestPackageName() {
        return testPackageName;
    }

    public String getTestClassName() {
        return testClassName;
    }

    public int getTestResult() {
        return testResult;
    }

    public void setTestResult(int result) {
        testResult = result;
    }

    public int getTestLabelResId() {
        return labelResId;
    }

    public void setTestLabelResId(int labelRes) {
        labelResId = labelRes;
    }

    public String getDisplayName(Context context){
        String displayName = "";
        try {
            if(labelResId > 0){
                displayName = context.getString(labelResId);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        if(TextUtils.isEmpty(displayName)){
            displayName = testName;
        }
        return displayName;
    }
}
