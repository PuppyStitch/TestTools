package com.sprd.validationtools.background;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;

public class BackgroundTestActivity extends BaseActivity {
    private static final String TAG = "BackgroundTestActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_test);

        TextView resultView = (TextView) findViewById(R.id.bg_test_result);
        resultView.setText(getIntent().getStringExtra(
                Const.INTENT_BACKGROUND_TEST_RESULT));
        Button btn = (Button) findViewById(R.id.btn_retest);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        super.removeButton();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
