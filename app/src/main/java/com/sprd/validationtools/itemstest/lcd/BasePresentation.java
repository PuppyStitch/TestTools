package com.sprd.validationtools.itemstest.lcd;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.simcom.testtools.R;

public class BasePresentation extends Presentation implements View.OnClickListener {

    protected Button mPassButton;
    protected Button mFailButton;
    private static final int TEXT_SIZE = 30;
    protected WindowManager mWindowManager;

    public BasePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public BasePresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        createButton(true);
//        createButton(false);

        setContentView(R.layout.activity_print_test_layout);
    }

//    public void createButton(boolean isPassButton) {
//        int buttonSize = getResources().getDimensionPixelSize(
//                R.dimen.pass_fail_button_size);
//        if (isPassButton) {
//            mPassButton = new Button(getContext());
//            mPassButton.setText(R.string.text_pass);
//            mPassButton.setTextColor(Color.WHITE);
//            mPassButton.setTextSize(TEXT_SIZE);
//            mPassButton.setBackgroundColor(Color.GREEN);
//            mPassButton.setOnClickListener(this);
//        } else {
//            mFailButton = new Button(getContext());
//            mFailButton.setText(R.string.text_fail);
//            mFailButton.setTextColor(Color.WHITE);
//            mFailButton.setTextSize(TEXT_SIZE);
//            mFailButton.setBackgroundColor(Color.RED);
//            mFailButton.setOnClickListener(this);
//        }
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION,
//                // WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                PixelFormat.TRANSLUCENT);
//        lp.gravity = isPassButton ? Gravity.LEFT | Gravity.BOTTOM
//                : Gravity.RIGHT | Gravity.BOTTOM;
//        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        lp.width = buttonSize;
//        lp.height = buttonSize;
//        mWindowManager = p
//        mWindowManager.addView(isPassButton ? mPassButton : mFailButton, lp);
//    }

    @Override
    public void onClick(View v) {

    }
}
