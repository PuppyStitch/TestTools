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

    abstract public String[] getFilterClassName();

    private ArrayList<TestItem> mTestItemList = new ArrayList<TestItem>();
    private ArrayList<TestItem> mMMI2ItemList = new ArrayList<TestItem>();
    private ArrayList<TestItem> mSMTItemList = new ArrayList<TestItem>();

    public TestItemList(Context context) {
        addAndFilterTestItemList(context, MMI1TestItems.FILTER_CLASS_NAMES);
        addAndFilterMMI2ItemList(context, MMI2TestItems.FILTER_CLASS_NAMES);
        addAndFilterSMTItemList(context, SMTTestItems.FILTER_CLASS_NAMES);

    }

    public ArrayList<TestItem> getTestItemList() {
        synchronized (mTestItemList) {
            return mTestItemList;
        }
    }

    public ArrayList<TestItem> getMMI2ItemList() {
        synchronized (mMMI2ItemList) {
            return mMMI2ItemList;
        }
    }

    public ArrayList<TestItem> getSMTItemList() {
        synchronized (mSMTItemList) {
            return mSMTItemList;
        }
    }

    public TestItem getTestItemByClassName(String className) {
        synchronized (mTestItemList) {
            for (TestItem testItem : mTestItemList) {
                if (className != null && className.equals(testItem.getTestClassName())) {
                    Log.d(TAG, "getTestItemByClassName className=" + className);
                    return testItem;
                }
            }
            Log.e(TAG, "getTestItemByClassName NOT FOUND!");
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

            if (actInfo == null) {
                Log.e(TAG, "actInfo is null: " + testItemClassName);
                return null;
            }
            String testName_ = context.getString(actInfo.labelRes);
            if (TextUtils.isEmpty(testName_)) {
                Log.e(TAG, "testName is null: " + testItemClassName);
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
                Log.e(TAG, "add item: " + testItemClassName);
                mTestItemList.add(iTestItem);
            } else {
                Log.e(TAG, "do not add item: " + testItemClassName);
            }
        }
        return mTestItemList;
    }

    private ArrayList<TestItem> addAndFilterMMI2ItemList(Context context,
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

            if (actInfo == null) {
                Log.e(TAG, "actInfo is null: " + testItemClassName);
                return null;
            }
            String testName_ = context.getString(actInfo.labelRes);
            if (TextUtils.isEmpty(testName_)) {
                Log.e(TAG, "testName is null: " + testItemClassName);
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
                Log.e(TAG, "add item: " + testItemClassName);
                mMMI2ItemList.add(iTestItem);
            } else {
                Log.e(TAG, "do not add item: " + testItemClassName);
            }
        }
        return mMMI2ItemList;
    }

    private ArrayList<TestItem> addAndFilterSMTItemList(Context context,
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

            if (actInfo == null) {
                Log.e(TAG, "actInfo is null: " + testItemClassName);
                return null;
            }
            String testName_ = context.getString(actInfo.labelRes);
            if (TextUtils.isEmpty(testName_)) {
                Log.e(TAG, "testName is null: " + testItemClassName);
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
                Log.e(TAG, "add item: " + testItemClassName);
                mSMTItemList.add(iTestItem);
            } else {
                Log.e(TAG, "do not add item: " + testItemClassName);
            }
        }
        return mSMTItemList;
    }

}
