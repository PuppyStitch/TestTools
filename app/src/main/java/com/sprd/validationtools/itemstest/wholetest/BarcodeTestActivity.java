package com.sprd.validationtools.itemstest.wholetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.simcom.testtools.R;
import android.app.ActionBar.LayoutParams;
import com.sprd.validationtools.BaseActivity;

public class BarcodeTestActivity extends BaseActivity {

    private static final String TAG = "BarcodeTestActivity";

    private Button mButton;
    private final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout barcodeLayout = new LinearLayout(this);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        barcodeLayout.setLayoutParams(params);
        barcodeLayout.setOrientation(1);
        barcodeLayout.setGravity(Gravity.CENTER);
        mButton = new Button(this);
        mButton.setTextSize(35);
        barcodeLayout.addView(mButton);
        setContentView(barcodeLayout);
        setTitle(R.string.barcode_test);
        mButton.setText(getResources().getText(R.string.barcode_test));
        mButton.setOnClickListener(view -> start());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            storeRusult(requestCode == RESULT_OK);
        }
        finish();
    }

    public void start() {
        Intent intent = new Intent();
        intent.setClassName("com.example.myprinterdemo", "com.example.myprinterdemo.validation.ScannerActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUEST_CODE);
    }

}

