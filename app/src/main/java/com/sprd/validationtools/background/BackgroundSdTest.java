package com.sprd.validationtools.background;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

import com.sprd.validationtools.itemstest.storage.SDCardTest;
import com.sprd.validationtools.modules.TestItem;
import com.sprd.validationtools.modules.UnitTestItemList;
import com.sprd.validationtools.utils.StorageUtil;

public class BackgroundSdTest implements BackgroundTest {
    private int testResult = RESULT_INVALID;
    private static final String SPRD_SD_TESTFILE = "sprdtest.txt";
    private static final String PHONE_STORAGE_PATH = "/data/data/com.sprd.validationtools/";
    private byte[] mounted = new byte[2];
    private Thread thread = null;
    private Context mContext = null;

    private static final String TEST_CLASS_NAME = SDCardTest.class.getName();

    public BackgroundSdTest(Context context) {
        mContext = context;
    }

    @Override
    public void startTest() {
        thread = new Thread() {
            public void run() {
                if (!StorageUtil.getExternalStoragePathState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    mounted[0] = 1;
                } else {
                    mounted[0] = 0;
                }
                mounted[1] = 0;

                if (mounted[0] == 1 || mounted[1] == 1) {
                    testResult = RESULT_FAIL;
                    stopTest();
                    return;
                }

                FileInputStream in = null;
                FileOutputStream out = null;
                try {
                    byte mSDCardTestFlag[] = new byte[1];
                    byte[] result = new byte[2];

                    if (mounted[0] == 0) {
                        File fp = new File(
                                StorageUtil.getExternalStorageAppPath(mContext,
                                        0), SPRD_SD_TESTFILE);
                        if (fp.exists())
                            fp.delete();
                        fp.createNewFile();
                        out = new FileOutputStream(fp);
                        mSDCardTestFlag[0] = '6';
                        out.write(mSDCardTestFlag, 0, 1);
                        out.close();
                        in = new FileInputStream(fp);
                        in.read(mSDCardTestFlag, 0, 1);
                        in.close();
                        if (mSDCardTestFlag[0] == '6') {
                            result[0] = 0;
                        } else {
                            result[0] = 1;
                        }
                    }
                    if (mounted[1] == 0) {
                        File fp = new File(PHONE_STORAGE_PATH, SPRD_SD_TESTFILE);
                        if (fp.exists())
                            fp.delete();
                        fp.createNewFile();
                        out = new FileOutputStream(fp);
                        mSDCardTestFlag[0] = 'd';
                        out.write(mSDCardTestFlag, 0, 1);
                        out.close();
                        in = new FileInputStream(fp);
                        in.read(mSDCardTestFlag, 0, 1);
                        in.close();
                        if (mSDCardTestFlag[0] == 'd') {
                            result[1] = 0;
                        } else {
                            result[1] = 1;
                        }
                    }

                    if (result[0] == 0 && result[1] == 0) {
                        testResult = RESULT_PASS;
                        stopTest();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                            out = null;
                        }
                        if (in != null) {
                            in.close();
                            in = null;
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    public void stopTest() {
    }

    @Override
    public int getResult() {
        return testResult;
    }

    @Override
    public String getResultStr() {
        String btResult = "SD:";
        if (RESULT_PASS == testResult) {
            btResult += "PASS";
        } else {
            btResult += "FAIL";
        }

        return btResult;
    }

    @Override
    public int getTestItemIdx() {
        return -1;
    }

    @Override
    public TestItem getTestItem(Context context) {
        return UnitTestItemList.getInstance(mContext).getTestItemByClassName(
                TEST_CLASS_NAME);
    }
}
