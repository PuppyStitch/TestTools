package com.sprd.validationtools.itemstest.camera;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;
import com.simcom.testtools.R;
import com.sprd.validationtools.utils.FileUtils;

public class ColorTemperatureTestActivty extends BaseActivity {
    private static final String TAG = "ColorTemperatureTestActivty";

    private TextView mGoldenTitleView = null;
    private TextView mGoldenxDataView = null;
    private TextView mGoldenyDataView = null;
    private TextView mGoldenzDataView = null;
    private TextView mGoldenirDataView = null;

    private TextView mUnitTitleView = null;
    private TextView mUnitxDataView = null;
    private TextView mUnityDataView = null;
    private TextView mUnitzDataView = null;
    private TextView mUnitirDataView = null;

    private Button mButtonStart = null;

    private static final String tcs3430_als_x = "/sys/devices/virtual/input/input5/tcs3430_als_x";
    private static final String tcs3430_als_y = "/sys/devices/virtual/input/input5/tcs3430_als_y";
    private static final String tcs3430_als_z = "/sys/devices/virtual/input/input5/tcs3430_als_z";
    private static final String tcs3430_als_ir1 = "/sys/devices/virtual/input/input5/tcs3430_als_ir1";

    private static final String tcs3430_calibration_file = Const.PRODUCTINFO_DIR
            + "/tcs3430_calibration.txt";
    private int mtcs3430_als_x = 0;
    private int mtcs3430_als_y = 0;
    private int mtcs3430_als_z = 0;
    private int mtcs3430_als_ir1 = 0;

    private static final String tcs3430_calibration_golden_file = Const.PRODUCTINFO_DIR
            + "/tcs3430_calibration_golden.txt";
    private int g_mtcs3430_als_x = 0;
    private int g_mtcs3430_als_y = 0;
    private int g_mtcs3430_als_z = 0;
    private int g_mtcs3430_als_ir1 = 0;

    private Handler mHandler = new Handler();

    private void saveaGoldendataToProdNV(Context context) {
        try {
            Resources res = context.getResources();
            String[] goldenDatas = res
                    .getStringArray(R.array.color_templeture_golden_data);
            int length = goldenDatas.length;
            Log.d(TAG, "saveaGoldendataToProdNV length=" + length);
            if (length < 4) {
                return;
            }
            int tg_mtcs3430_als_x = -1, tg_mtcs3430_als_y = -1, tg_mtcs3430_als_z = -1, tg_mtcs3430_als_ir1 = -1;
            for (int i = 0; i < length; i++) {
                Log.d(TAG, "i=" + i + ",goldenDatas[i]=" + goldenDatas[i]);
                if (i == 0)
                    tg_mtcs3430_als_x = Integer.parseInt(goldenDatas[i]);
                if (i == 1)
                    tg_mtcs3430_als_y = Integer.parseInt(goldenDatas[i]);
                if (i == 2)
                    tg_mtcs3430_als_z = Integer.parseInt(goldenDatas[i]);
                if (i == 3)
                    tg_mtcs3430_als_ir1 = Integer.parseInt(goldenDatas[i]);
            }
            StringBuffer buffer = new StringBuffer();
            buffer.append(tg_mtcs3430_als_x);
            buffer.append("\n");
            buffer.append(tg_mtcs3430_als_y);
            buffer.append("\n");
            buffer.append(tg_mtcs3430_als_z);
            buffer.append("\n");
            buffer.append(tg_mtcs3430_als_ir1);

            String content = buffer.toString();
            Log.d(TAG, "saveaGoldendataToProdNV content=" + content + "\n");
            Log.d(TAG, "saveaGoldendataToProdNV tcs3430_calibration_golden_file="
                    + tcs3430_calibration_golden_file + "\n");
            FileUtils.writeFile(tcs3430_calibration_golden_file, content);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void loadGoldenDataFromResources(Context context) {
        if (context == null)
            return;
        try {
            Resources res = context.getResources();
            String[] goldenDatas = res
                    .getStringArray(R.array.color_templeture_golden_data);
            int length = goldenDatas.length;
            Log.d(TAG, "loadGoldenDataFromResources length=" + length);
            if (length < 4) {
                return;
            }
            for (int i = 0; i < length; i++) {
                Log.d(TAG, "i=" + i + ",goldenDatas[i]=" + goldenDatas[i]);
                if (i == 0)
                    g_mtcs3430_als_x = Integer.parseInt(goldenDatas[i]);
                if (i == 1)
                    g_mtcs3430_als_y = Integer.parseInt(goldenDatas[i]);
                if (i == 2)
                    g_mtcs3430_als_z = Integer.parseInt(goldenDatas[i]);
                if (i == 3)
                    g_mtcs3430_als_ir1 = Integer.parseInt(goldenDatas[i]);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void loadGoldenDataFromProdNV() {
        InputStream fIn = null;
        BufferedReader buffreader = null;
        try {
            ArrayList<String> goldenDatas = new ArrayList<String>();
            File file = new File(tcs3430_calibration_golden_file);
            fIn = new FileInputStream(file);
            InputStreamReader inputreader = new InputStreamReader(fIn,
                    Charset.defaultCharset());
            String line;
            buffreader = new BufferedReader(inputreader);
            while ((line = buffreader.readLine()) != null) {
                goldenDatas.add(line);
            }

            int length = goldenDatas.size();
            Log.d(TAG, "loadGoldenDataFromProdNV length=" + length);
            if (length < 4) {
                return;
            }
            for (int i = 0; i < length; i++) {
                if (i == 0)
                    g_mtcs3430_als_x = Integer.parseInt(goldenDatas.get(i));
                if (i == 1)
                    g_mtcs3430_als_y = Integer.parseInt(goldenDatas.get(i));
                if (i == 2)
                    g_mtcs3430_als_z = Integer.parseInt(goldenDatas.get(i));
                if (i == 3)
                    g_mtcs3430_als_ir1 = Integer.parseInt(goldenDatas.get(i));
            }
            Log.d(TAG, "loadGoldenDataFromProdNV g_mtcs3430_als_x="
                    + g_mtcs3430_als_x + ",g_mtcs3430_als_y="
                    + g_mtcs3430_als_y + ",g_mtcs3430_als_z="
                    + g_mtcs3430_als_z + ",g_mtcs3430_als_ir1="
                    + g_mtcs3430_als_ir1);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(fIn != null){
                    fIn.close();
                }
                if(buffreader != null){
                    buffreader.close();
                }
            } catch (IOException e2) {
            	e2.printStackTrace();
            }
        }
    }

    private void saveColorTemperatureInfo(String g_mtcs3430_als_x,
            String g_mtcs3430_als_y, String g_mtcs3430_als_z,
            String g_mtcs3430_als_ir1, String mtcs3430_als_x,
            String mtcs3430_als_y, String mtcs3430_als_z,
            String mtcs3430_als_ir1) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(g_mtcs3430_als_x);
        buffer.append("\n");
        buffer.append(g_mtcs3430_als_y);
        buffer.append("\n");
        buffer.append(g_mtcs3430_als_z);
        buffer.append("\n");
        buffer.append(g_mtcs3430_als_ir1);
        buffer.append("\n");

        // unit data
        buffer.append(mtcs3430_als_x);
        buffer.append("\n");
        buffer.append(mtcs3430_als_y);
        buffer.append("\n");
        buffer.append(mtcs3430_als_z);
        buffer.append("\n");
        buffer.append(mtcs3430_als_ir1);
        // buffer.append("\n");
        String content = buffer.toString();
        Log.d(TAG, "saveColorTemperatureInfo content=" + content + "\n");
        Log.d(TAG, "saveColorTemperatureInfo tcs3430_calibration_file="
                + tcs3430_calibration_file + "\n");
        FileUtils.writeFile(tcs3430_calibration_file, content);
    }

    private void showColorTemperatureInfo(String g_mtcs3430_als_x,
            String g_mtcs3430_als_y, String g_mtcs3430_als_z,
            String g_mtcs3430_als_ir1, String mtcs3430_als_x,
            String mtcs3430_als_y, String mtcs3430_als_z,
            String mtcs3430_als_ir1) {
        final String tcs3430_als_x = mtcs3430_als_x;
        final String tcs3430_als_y = mtcs3430_als_y;
        final String tcs3430_als_z = mtcs3430_als_z;
        final String tcs3430_als_ir1 = mtcs3430_als_ir1;

        final String g_tcs3430_als_x = g_mtcs3430_als_x;
        final String g_tcs3430_als_y = g_mtcs3430_als_y;
        final String g_tcs3430_als_z = g_mtcs3430_als_z;
        final String g_tcs3430_als_ir1 = g_mtcs3430_als_ir1;

        if (mHandler != null) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    // Golden data
                    if (mGoldenxDataView != null) {
                        mGoldenxDataView
                                .setText("golden_x: " + g_tcs3430_als_x);
                    }
                    if (mGoldenyDataView != null) {
                        mGoldenyDataView
                                .setText("golden_y: " + g_tcs3430_als_y);
                    }
                    if (mGoldenzDataView != null) {
                        mGoldenzDataView
                                .setText("golden_z: " + g_tcs3430_als_z);
                    }
                    if (mGoldenirDataView != null) {
                        mGoldenirDataView.setText("golden_ir: "
                                + g_tcs3430_als_ir1);
                    }
                    // Unit data
                    if (mUnitxDataView != null) {
                        mUnitxDataView.setText("unit_x: " + tcs3430_als_x);
                    }
                    if (mUnityDataView != null) {
                        mUnityDataView.setText("unit_y: " + tcs3430_als_y);
                    }
                    if (mUnitzDataView != null) {
                        mUnitzDataView.setText("unit_z: " + tcs3430_als_z);
                    }
                    if (mUnitirDataView != null) {
                        mUnitirDataView.setText("unit_ir: " + tcs3430_als_ir1);
                    }
                }
            });
        }
    }

    private AsyncTask<Void, Void, Void> mColorTemperatureAsyncTask = null;

    class ColorTemperatureAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            // 1.load golden data
            //saveaGoldendataToProdNV(getApplicationContext());
            // 2.read unit data
            readColorTemperature();
            // load golden data
            loadGoldenDataFromProdNV();
            if (g_mtcs3430_als_x <= 0) {
                g_mtcs3430_als_x = mtcs3430_als_x;
            }
            if (g_mtcs3430_als_y <= 0) {
                g_mtcs3430_als_y = mtcs3430_als_y;
            }
            if (g_mtcs3430_als_z <= 0) {
                g_mtcs3430_als_z = mtcs3430_als_z;
            }
            if (g_mtcs3430_als_ir1 <= 0) {
                g_mtcs3430_als_ir1 = mtcs3430_als_ir1;
            }
            showColorTemperatureInfo(
                    // Golden data
                    String.valueOf(g_mtcs3430_als_x),
                    String.valueOf(g_mtcs3430_als_y),
                    String.valueOf(g_mtcs3430_als_z),
                    String.valueOf(g_mtcs3430_als_ir1),

                    // Unit data
                    String.valueOf(mtcs3430_als_x),
                    String.valueOf(mtcs3430_als_y),
                    String.valueOf(mtcs3430_als_z),
                    String.valueOf(mtcs3430_als_ir1));
            saveColorTemperatureInfo(
                    // Golden data
                    String.valueOf(g_mtcs3430_als_x),
                    String.valueOf(g_mtcs3430_als_y),
                    String.valueOf(g_mtcs3430_als_z),
                    String.valueOf(g_mtcs3430_als_ir1),

                    // Unit data
                    String.valueOf(mtcs3430_als_x),
                    String.valueOf(mtcs3430_als_y),
                    String.valueOf(mtcs3430_als_z),
                    String.valueOf(mtcs3430_als_ir1));
            return null;
        }

    }

    private void readColorTemperature() {
        mtcs3430_als_x = FileUtils.getIntFromFile(tcs3430_als_x);
        mtcs3430_als_y = FileUtils.getIntFromFile(tcs3430_als_y);
        mtcs3430_als_z = FileUtils.getIntFromFile(tcs3430_als_z);
        mtcs3430_als_ir1 = FileUtils.getIntFromFile(tcs3430_als_ir1);
        Log.d(TAG, "readColorTemperature mtcs3430_als_x=" + mtcs3430_als_x
                + ",mtcs3430_als_y=" + mtcs3430_als_y + ",mtcs3430_als_z="
                + mtcs3430_als_z + ",mtcs3430_als_ir1=" + mtcs3430_als_ir1);
    }

    private void initView() {
        mGoldenTitleView = (TextView) findViewById(R.id.golden_title);
        mGoldenxDataView = (TextView) findViewById(R.id.x_data_golden);
        mGoldenyDataView = (TextView) findViewById(R.id.y_data_golden);
        mGoldenzDataView = (TextView) findViewById(R.id.z_data_golden);
        mGoldenirDataView = (TextView) findViewById(R.id.ir_data_golden);

        mUnitTitleView = (TextView) findViewById(R.id.unit_title);
        mUnitxDataView = (TextView) findViewById(R.id.x_data_unit);
        mUnityDataView = (TextView) findViewById(R.id.y_data_unit);
        mUnitzDataView = (TextView) findViewById(R.id.z_data_unit);
        mUnitirDataView = (TextView) findViewById(R.id.ir_data_unit);

        if (mUnitTitleView != null) {
            mUnitTitleView.setText("Unit Data");
        }
        if (mGoldenTitleView != null) {
            mGoldenTitleView.setText("Golden Data");
        }

        mButtonStart = (Button) findViewById(R.id.color_temperature_start_btn);
        mButtonStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mColorTemperatureAsyncTask = new ColorTemperatureAsyncTask();
                mColorTemperatureAsyncTask.executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.color_temperature);
        initView();
    }
}
