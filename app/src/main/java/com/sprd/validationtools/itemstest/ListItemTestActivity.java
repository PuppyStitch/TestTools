package com.sprd.validationtools.itemstest;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sprd.validationtools.Const;
import com.simcom.testtools.R;
import com.sprd.validationtools.modules.TestItem;
import com.sprd.validationtools.modules.UnitTestItemList;
import com.sprd.validationtools.sqlite.EngSqlite;

public class ListItemTestActivity extends Activity {
    private static final String TAG = "ListItemTestActivity";

    private ItemListViewAdapter mItemListViewAdapter;
    private ArrayList<TestItem> mItemsListView  = new ArrayList<TestItem>();
    private ListView mListViewItem;
    private EngSqlite mEngSqlite;
    private int mLastTestItemIndex = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEngSqlite = EngSqlite.getInstance(this);
        setContentView(R.layout.activity_validation_tools_main);
        mListViewItem = (ListView) findViewById(R.id.ValidationToolsList);
        initAdapter();
        mListViewItem.setAdapter(mItemListViewAdapter);
        mListViewItem.setOnItemClickListener(new ListItemClickListener());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    @Override
    protected void onDestroy() {
        mItemsListView.clear();
        super.onDestroy();
    }

    private void initAdapter() {
        TestItem item = null;
        EngSqlite engSqlite = EngSqlite.getInstance(ListItemTestActivity.this);
        if (engSqlite == null) {
            return;
        }

        ArrayList<TestItem> list = null;
        if (Const.TEST_VALUE == Const.MMI2_VALUE) {
            list = UnitTestItemList.getInstance(
                    ListItemTestActivity.this).getMMI2ItemList();
        } else if (Const.TEST_VALUE == Const.MMI1_VALUE) {
            list = UnitTestItemList.getInstance(
                    ListItemTestActivity.this).getTestItemList();
        } else {
            list = UnitTestItemList.getInstance(
                    ListItemTestActivity.this).getSMTItemList();
        }

        mItemsListView.addAll(list);
        TestItem testResult = new TestItem("TestResult",
                TestResultActivity.class.getPackage().getName(),
                TestResultActivity.class.getName(), -2, R.string.test_result_title);
        mItemsListView.add(testResult);

        for (int i = 0; i < mItemsListView.size() - 1; i++) {
            item = mItemsListView.get(i);
            item.setTestResult(engSqlite.getTestListItemStatus(mItemsListView
                    .get(i).getTestClassName()));
        }
        mItemListViewAdapter = new ItemListViewAdapter(this, mItemsListView);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Const.TEST_ITEM_DONE) {
            int position = mLastTestItemIndex;

            mItemsListView.get(position).setTestResult(
                    mEngSqlite.getTestListItemStatus(mItemsListView.get(
                            position).getTestClassName()));
            mItemListViewAdapter.notifyDataSetChanged();
        }
    }

    private class ListItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {

            mLastTestItemIndex = position;
            Intent intent = new Intent();
            intent.setClassName(ListItemTestActivity.this,
                    mItemsListView.get(position).getTestClassName());
            intent.putExtra(Const.INTENT_PARA_TEST_NAME,
                    mItemsListView.get(position).getTestName());

            intent.putExtra(Const.INTENT_PARA_TEST_INDEX, position);
            startActivityForResult(intent, 0);
        }
    }

    private class ItemListViewAdapter extends BaseAdapter {

        private ArrayList<TestItem> mItemList;
        private LayoutInflater mInflater;

        public ItemListViewAdapter(Context c, ArrayList<TestItem> mItemsListView) {
            mItemList = mItemsListView;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (mItemList != null) {
                return mItemList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            TestItem item = mItemList.get(position);
            if (convertView == null) {
                view = mInflater.inflate(R.layout.listview_item, parent, false);
            } else {
                view = convertView;
            }
            TextView textView = (TextView) view
                    .findViewById(R.id.listitem_text);
            textView.setText(item.getDisplayName(getApplicationContext()));

            if (item.getTestResult() == Const.SUCCESS) {
                textView.setTextColor(Color.GREEN);
            } else if (item.getTestResult() == Const.FAIL) {
                textView.setTextColor(Color.RED);
            } else {
                textView.setTextColor(Color.WHITE);
            }
            return view;
        }
    }
}
