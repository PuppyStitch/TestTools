package com.sprd.validationtools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.simcom.testtools.R;
import com.sprd.validationtools.sqlite.EngSqlite;

public class ChoseTestModeActivity extends AppCompatActivity {

    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("测试选择");
        setContentView(R.layout.activity_chose_test_mode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIntent = new Intent(this, ValidationToolsMainActivity.class);
    }

    public void SMT(View view) {
        mIntent.putExtra(Const.KEY, Const.SMT_VALUE);
        Const.TEST_VALUE = Const.SMT_VALUE;
        EngSqlite.getInstance(this).setCurrentTable(EngSqlite.ENG_SMT_TABLE);
        start();
    }

    public void MM1(View view) {
        mIntent.putExtra(Const.KEY, Const.MMI1_VALUE);
        Const.TEST_VALUE = Const.MMI1_VALUE;
        EngSqlite.getInstance(this).setCurrentTable(EngSqlite.ENG_STRING2INT_TABLE);
        start();
    }

    public void oldTest(View view) {

    }

    public void MM2(View view) {
        mIntent.putExtra(Const.KEY, Const.MMI2_VALUE);
        Const.TEST_VALUE = Const.MMI2_VALUE;
        EngSqlite.getInstance(this).setCurrentTable(EngSqlite.ENG_MMI2_TABLE);
        start();
    }

    private void start() {
        startActivity(mIntent);
    }
}