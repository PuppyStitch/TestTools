package com.sprd.validationtools.modules;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import com.sprd.validationtools.Const;

public abstract class TestItemList {

    private static final String TAG = "TestItemList";

    abstract public String[] getfilterClassName();

    private ArrayList<TestItem> mTestItemList = new ArrayList<TestItem>();

    public TestItemList(Context context) {
        addAndFilterTestItemList(context, getfilterClassName());
    }

    public ArrayList<TestItem> getTestItemList() {
        synchronized (mTestItemList) {
            return mTestItemList;
        }
    }

    public TestItem getTestItemByClassName(String className) {
        synchronized (mTestItemList) {
            for(TestItem testItem : mTestItemList){
                if(className != null && className.equals(testItem.getTestClassName())){
                    Log.d(TAG, "getTestItemByClassName className="+className);
                    return testItem;
                }
            }
            Log.d(TAG, "getTestItemByClassName NOT FOUND!");
            return null;
        }
    }

    private ArrayList<TestItem> addAndFilterTestItemList(Context context,
            final String[] filterClassName) {
        if (filterClassName == null) {
            return null;
        }
        for (String testItemClassName : filterClassName) {
            ComponentName comName = new ComponentName(context,
                    testItemClassName);
            ActivityInfo actInfo = null;
            try {
                actInfo = context.getPackageManager().getActivityInfo(comName,
                        PackageManager.GET_META_DATA);
            } catch (NameNotFoundException e1) {
                e1.printStackTrace();
            }

            if(actInfo == null) return null;
            String testName_ = context.getString(actInfo.labelRes);
            if (TextUtils.isEmpty(testName_)) {
                testName_ = comName.getShortClassName();
            }
            int labelRes = actInfo.labelRes;
            String testPackageName_ = comName.getPackageName();
            String testClassName_ = comName.getClassName();
            int testResult_ = Const.DEFAULT;
            TestItem iTestItem = new TestItem(testName_, testPackageName_,
                    testClassName_, testResult_, labelRes);
            boolean isSupport = Const.isSupport(testItemClassName, context);
            //Log.d(TAG, "filterTestItemList isSupport=" + isSupport+",testClassName_="+testClassName_);
            if (isSupport) {
                mTestItemList.add(iTestItem);
            }
        }
        return mTestItemList;
    }

}
