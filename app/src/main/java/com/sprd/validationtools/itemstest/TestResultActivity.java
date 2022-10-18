package com.sprd.validationtools.itemstest;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sprd.validationtools.Const;
import com.simcom.testtools.R;
import com.sprd.validationtools.modules.TestItem;
import com.sprd.validationtools.modules.UnitTestItemList;
import com.sprd.validationtools.sqlite.EngSqlite;
import com.sprd.validationtools.utils.IATUtils;

public class TestResultActivity extends Activity {
    private static final String TAG = "TestResultActivity";
    private Context mContext;
    public TextView mTestResultView;
    private EngSqlite mEngSqlite;
    private String mBit;
    public Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_result);
        mContext = this;
        mEngSqlite = EngSqlite.getInstance(mContext);
        ListView listView = (ListView) findViewById(R.id.listview_layout);
        // go to change
        ArrayList<TestItem> list;
        if (Const.TEST_VALUE == Const.MMI1_VALUE) {
            list = UnitTestItemList.getInstance(
                    mContext).getTestItemList();
        } else if (Const.TEST_VALUE == Const.MMI2_VALUE) {
            list = UnitTestItemList.getInstance(
                    mContext).getMMI2ItemList();
        } else {
            list = UnitTestItemList.getInstance(
                    mContext).getSMTItemList();
        }
        ListAdapter listAdapter = new ListAdapter(mContext,
                mEngSqlite.queryData(list));
        listView.setAdapter(listAdapter);
        mTestResultView = (TextView) findViewById(R.id.test_result_text);
        final long time = getIntent().getLongExtra("start_time", 0);
        if (time != 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("APK_MMI", "*********** System Full Test Time: "
                            + (System.currentTimeMillis() - time) / 1000
                            + "s ***********");
                }
            }, 600);
        }
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        int failCount = mEngSqlite.querySystemFailCount();
        if (failCount >= 1) {
            mTestResultView.setText(getResources().getString(
                    R.string.TestResultTitleStringFail));
            mTestResultView.setTextColor(Color.RED);
        } else {
            setSuccess();
        }
        super.onResume();
    }

    static class ViewHolder {
        public TextView textID;
        public TextView textCase;
        public TextView textResult;
    }

    private class ListAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private ViewHolder mViewHolder;
        private ArrayList<TestItem> mItems;

        public ListAdapter(Context context, ArrayList<TestItem> items) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mViewHolder = new ViewHolder();
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(
                        R.layout.table_row_layout, parent, false);
            }
            mViewHolder.textID = (TextView) convertView
                    .findViewById(R.id.id_text);
            mViewHolder.textCase = (TextView) convertView
                    .findViewById(R.id.test_case);
            mViewHolder.textResult = (TextView) convertView
                    .findViewById(R.id.test_result);
            mViewHolder.textID.setText(String.valueOf(position + 1));
            mViewHolder.textCase.setText(mItems.get(position).getDisplayName(getApplicationContext()));
            if (mItems.get(position).getTestResult() == Const.FAIL) {
                mViewHolder.textResult.setText(getResources().getString(
                        R.string.text_fail));
                mViewHolder.textResult.setTextColor(Color.RED);
                mTestResultView.setText(getResources().getString(
                        R.string.TestResultTitleStringFail));
                mTestResultView.setTextColor(Color.RED);
            } else if (mItems.get(position).getTestResult() == Const.SUCCESS) {
                mViewHolder.textResult.setText(getResources().getString(
                        R.string.text_pass));
                mViewHolder.textResult.setTextColor(Color.GREEN);
            } else {
                mViewHolder.textResult.setText(getResources().getString(
                        R.string.text_na));
                mViewHolder.textResult.setTextColor(Color.BLACK);
            }
            return convertView;
        }
    }

    private void setSuccess() {
        new Thread(new Runnable() {
            public void run() {
                String str = IATUtils.sendATCmd("AT+SGMR=0,0,4", "atchannel0");
                Log.d(TAG, "setSuccess get result str = " + str);
                if (str.contains(IATUtils.AT_OK)) {
                    String[] paser = str.split("\n");
                    String[] paser1 = paser[0].split(":");
                    mBit = getBitStr(paser1[1].trim());
                    Log.d(TAG, "mBit: " + mBit);

                    long cc = Long.parseLong(mBit, 16)
                            | Long.parseLong("08000000", 16);
                    str = IATUtils.sendATCmd("AT+SGMR=0,1,4,\""
                            + get8Bit("" + Long.toHexString(cc)) + "\"",
                            "atchannel0");
                    Log.d(TAG,
                            "setSuccess set result cc = "
                                    + Long.toHexString(cc) + ", str = " + str);
                }
            }

            private String getBitStr(String str) {
                String result = null;
                int ind = str.indexOf("0x");
                result = str.substring(ind + 2);
                return result.trim();
            }

            private String get8Bit(String src) {
                return (src.length() < 8) ? "0" + src : src;
            }
        }).start();
    }
}
