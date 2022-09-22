
package com.sprd.validationtools;

import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.simcom.testtools.R;
import com.sprd.validationtools.background.BackgroundBtTest;
import com.sprd.validationtools.background.BackgroundGpsTest;
import com.sprd.validationtools.background.BackgroundSdTest;
import com.sprd.validationtools.background.BackgroundSimTest;
import com.sprd.validationtools.background.BackgroundTest;
import com.sprd.validationtools.background.BackgroundTestActivity;
import com.sprd.validationtools.background.BackgroundWifiTest;
import com.sprd.validationtools.itemstest.ListItemTestActivity;
import com.sprd.validationtools.itemstest.TestResultActivity;
import com.sprd.validationtools.modules.AutoTestItemList;
import com.sprd.validationtools.modules.MMI1TestItems;
import com.sprd.validationtools.modules.MMI2TestItems;
import com.sprd.validationtools.modules.SMTTestItems;
import com.sprd.validationtools.modules.TestItem;
import com.sprd.validationtools.sqlite.EngSqlite;
import com.sprd.validationtools.testinfo.TestInfoMainActivity;
import com.sprd.validationtools.utils.ValidationToolsUtils;

public class ValidationToolsMainActivity extends Activity implements
        AdapterView.OnItemClickListener {
    private final static String TAG = "ValidationToolsMainActivity";
    private final static int FULL_TEST = 0;
    private final static int UNIT_TEST = 1;
    private final static int TEST_INFO = 2;
    private final static int CAMERA_CALI_VERIFY = 3;
    private final static int RESET = 4;
    private final static boolean SUPPORT_CAMERA_FEATURE = true;
    private String[] mListItemString;
    private ListView mListView;
    private ArrayList<TestItem> mAutoTestArray = null;
    private int mAutoTestCur = 0;
    private int mUserId;

    private ArrayList<BackgroundTest> mBgTest = null;

    private boolean mIsTested = false;
    public final static String IS_SYSTEM_TESTED = "is_system_tested";
    private SharedPreferences mPrefs;
    private long time = 0;
    //Save full test used time
    public final static String FULL_TEST_USED_TIME = "fulltest_used_time";
    private long mFullTestUsedtime = 0;
    private PhaseCheckParse mPhaseCheckParse = null;

    Intent mIntent;
    String mode;

    String[] testList;

    public final static String ACTION_CAMERA_CALI_VERUFY = "com.sprd.cameracalibration.START_CAMERACALIBRATION";
    public final static String EXTRA_GET_PHASECHECK = "phasecheck_result";

    private ArrayAdapter<String> mArrayAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "oncreate start!");
        super.onCreate(savedInstanceState);
        mIntent = getIntent();

        mode = mIntent.getStringExtra(Const.KEY);
        if (Const.MMI1_VALUE.equals(mode)) {
            testList = MMI1TestItems.FILTER_CLASS_NAMES;
        } else if (Const.MMI2_VALUE.equals(mode)) {
            testList = MMI2TestItems.FILTER_CLASS_NAMES;
        } else {
            testList = SMTTestItems.FILTER_CLASS_NAMES;
        }

        setContentView(R.layout.activity_validation_tools_main);
        if (SUPPORT_CAMERA_FEATURE && Const.isSupportCameraCaliVeri()) {
            mListItemString = new String[]{
                    this.getString(R.string.full_test),
                    this.getString(R.string.item_test),
                    this.getString(R.string.test_info),
                    this.getString(R.string.camera_cali_verify),
                    this.getString(R.string.reset)
            };
        } else {
            mListItemString = new String[]{
                    this.getString(R.string.full_test),
                    this.getString(R.string.item_test),
                    this.getString(R.string.test_info),
                    this.getString(R.string.reset)
            };
        }
        mListView = (ListView) findViewById(R.id.ValidationToolsList);
        mArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_list_item, mListItemString);

        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(this);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mIsTested = mPrefs.getBoolean(IS_SYSTEM_TESTED, false);
        mUserId = UserHandle.myUserId();
        mPhaseCheckParse = PhaseCheckParse.getInstance();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        ValidationToolsUtils.parsePCBAConf();
        startValidationToolsService(this, true);
        verifyStoragePermissions(this);
    }

    @Override
    public void onPause() {
        if (mUserId == 0) {
            saveTestInfo();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //stop service
        startValidationToolsService(this, false);
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Const.TEST_ITEM_DONE) {
            Log.d(TAG, "auto test because requesting");
            autoTest();
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void startValidationToolsService(Context context, boolean startService) {
        if (context == null) return;
        Intent intent = new Intent(context, ValidationToolsService.class);
        if (startService) {
            intent.setFlags(ValidationToolsService.FLAG_START_FOREGROUND);
            context.startService(intent);
        } else {
            intent.setFlags(ValidationToolsService.FLAG_STOP_FOREGROUND);
            context.stopService(intent);
        }
    }

    private void storePhaseCheck() {
        try {
            String station = BaseActivity.STATION_MMIT_VALUE;
            if (mPhaseCheckParse == null) {
                return;
            }
            EngSqlite engSqlite = EngSqlite.getInstance(this);
            if (engSqlite == null) {
                return;
            }
            Log.d(TAG, "storePhaseCheck: fail = " + engSqlite.queryFailCount() + ", NotTest = " + engSqlite.queryNotTestCount());
            mPhaseCheckParse.writeStationTested(station);
            if (engSqlite.queryFailCount() == 0 && engSqlite.queryNotTestCount() == 0) {
                mPhaseCheckParse.writeStationPass(station);
            } else {
                mPhaseCheckParse.writeStationFail(station);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void autoTest() {
        if (mAutoTestArray != null && mAutoTestCur < mAutoTestArray.size()) {
            Intent intent = new Intent();
            intent.setClassName(this, mAutoTestArray.get(mAutoTestCur).getTestClassName());
            intent.putExtra(Const.INTENT_PARA_TEST_NAME,
                    mAutoTestArray.get(mAutoTestCur).getTestName());
            intent.putExtra(Const.INTENT_PARA_TEST_INDEX, mAutoTestCur);
            startActivityForResult(intent, 0);

            mAutoTestCur++;
        } else if (mBgTest != null && mAutoTestArray != null) {
            EngSqlite engSqlite = EngSqlite.getInstance(this);
            addFailedBgTestToTestlist();
            StringBuffer buffer = new StringBuffer("");
            buffer.append(getResources().getString(R.string.bg_test_notice) + "\n\n");
            for (BackgroundTest bgTest : mBgTest) {
                bgTest.stopTest();
                engSqlite.updateDB(bgTest.getTestItem(getApplicationContext()).getTestClassName(),
                        bgTest.getResult() == BackgroundTest.RESULT_PASS ? Const.SUCCESS
                                : Const.FAIL);
                buffer.append(bgTest.getResultStr());
                buffer.append("\n\n");
            }

            //Restore pharsecheck.
            storePhaseCheck();

            Intent intent = new Intent(ValidationToolsMainActivity.this,
                    BackgroundTestActivity.class);
            intent.putExtra(Const.INTENT_BACKGROUND_TEST_RESULT, buffer.toString());
            startActivityForResult(intent, 0);
            mBgTest = null;
        } else {
            mFullTestUsedtime = System.currentTimeMillis() - time;
            saveFullTestUsedTime();
            Intent intent = new Intent(ValidationToolsMainActivity.this,
                    TestResultActivity.class);
            intent.putExtra("start_time", mFullTestUsedtime);
            startActivity(intent);
        }
    }

    private void addFailedBgTestToTestlist() {
        for (BackgroundTest bgTest : mBgTest) {
            if (bgTest.getResult() != BackgroundTest.RESULT_PASS) {
                TestItem item = bgTest.getTestItem(getApplicationContext());
                mAutoTestArray.add(item);
            }
        }
    }

    private void startBackgroundTest() {
        mBgTest = new ArrayList<BackgroundTest>();
        mBgTest.add(new BackgroundBtTest(this));
        mBgTest.add(new BackgroundWifiTest(this));
        mBgTest.add(new BackgroundGpsTest(this));
        mBgTest.add(new BackgroundSimTest(this));
        mBgTest.add(new BackgroundSdTest(this));
        for (BackgroundTest bgTest : mBgTest) {
            bgTest.startTest();
        }
    }

    public void saveTestInfo() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(IS_SYSTEM_TESTED, mIsTested);
        editor.apply();
    }

    public void saveFullTestUsedTime() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong(FULL_TEST_USED_TIME, mFullTestUsedtime);
        editor.apply();
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView l, View v, int position, long id) {
        /* SPRD: bug453083 ,Multi user mode, set button is not click. {@ */
        if (mUserId != 0) {
            Toast.makeText(getApplicationContext(), R.string.multi_user_hint, Toast.LENGTH_LONG).show();
            return;
        }
        /* @} */
        Log.d(TAG, "position:" + position + ",id=" + id);
        if (mArrayAdapter != null) {
            String clickItem = mArrayAdapter.getItem(position);
            Log.d(TAG, "clickItem:" + clickItem);
            if (getString(R.string.full_test).equals(clickItem)) {
                time = System.currentTimeMillis();
                mAutoTestArray = AutoTestItemList.getInstance(this).getTestItemList();
                mAutoTestCur = 0;
                mIsTested = true;
//                startBackgroundTest();
                autoTest();
            } else if (getString(R.string.item_test).equals(clickItem)) {
                Intent intent = new Intent(this, ListItemTestActivity.class);
                startActivity(intent);
            } else if (getString(R.string.test_info).equals(clickItem)) {
                Intent intent = new Intent(this, TestInfoMainActivity.class);
                intent.putExtra(IS_SYSTEM_TESTED, mIsTested);
                startActivity(intent);
            } else if (getString(R.string.camera_cali_verify).equals(clickItem)) {
                launcherCameraCaliVerify();
            } else if (getString(R.string.reset).equals(clickItem)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.reset)
                        .setMessage(R.string.factory_reset_message)
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_FACTORY_RESET);
                                intent.setPackage("android");
                                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                                intent.putExtra(Intent.EXTRA_REASON, "MasterClearConfirm");
                                intent.putExtra(Intent.EXTRA_WIPE_EXTERNAL_STORAGE, false);
                                ValidationToolsMainActivity.this.sendBroadcast(intent);
                            }
                        })
                        .setPositiveButton(android.R.string.cancel, null);
                builder.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mUserId != 0) {
            finish();
            return;
        }
        if (!SystemProperties.get("ro.bootmode").contains("engtest")) {
            super.onBackPressed();
        }
    }

    private void launcherCameraCaliVerify() {
        try {
            String phasecheck = PhaseCheckParse.getInstance().getPhaseCheck();
            Intent intent = new Intent(ACTION_CAMERA_CALI_VERUFY);
            intent.putExtra(EXTRA_GET_PHASECHECK, phasecheck);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
