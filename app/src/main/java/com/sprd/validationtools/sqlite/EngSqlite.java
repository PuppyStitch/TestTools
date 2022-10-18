package com.sprd.validationtools.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.sprd.validationtools.Const;
import com.sprd.validationtools.modules.TestItem;
import com.sprd.validationtools.modules.UnitTestItemList;

public class EngSqlite {
    private static final String TAG = "EngSqlite";
    private Context mContext;
    private SQLiteDatabase mSqLiteDatabase = null;

    //    public static final String ENG_ENGTEST_DB = Const.PRODUCTINFO_DIR
//            + "/mmitest.db";
    public static final String ENG_ENGTEST_DB = "test.db";
    public static final String ENG_STRING2INT_TABLE = "str2int";
    public static final String ENG_MMI2_TABLE = "mmi2";
    public static final String ENG_SMT_TABLE = "smt";
    public static final String ENG_STRING2INT_NAME = "name";
    public static final String ENG_STRING2INT_DISPLAYNAME = "displayname";
    public static final String ENG_STRING2INT_VALUE = "value";
    public static final String ENG_GROUPID_VALUE = "groupid";
    public static final int ENG_ENGTEST_VERSION = 1;

    public static String currentTable = ENG_STRING2INT_TABLE;

    private static EngSqlite mEngSqlite;

    public static synchronized EngSqlite getInstance(Context context) {
        if (mEngSqlite == null) {
            mEngSqlite = new EngSqlite(context);
        }
        return mEngSqlite;
    }

    private EngSqlite(Context context) {
        mContext = context;
        ValidationToolsDatabaseHelper databaseHelper = null;
        try {
            databaseHelper = new ValidationToolsDatabaseHelper(
                    mContext);
            mSqLiteDatabase = databaseHelper.getWritableDatabase();
            if(mSqLiteDatabase != null){
                mSqLiteDatabase.disableWriteAheadLogging();
            }
            Log.v(TAG, "EngSqlite mSqLiteDatabase= " + mSqLiteDatabase);
        }catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentTable(String table) {
        currentTable = table;
    }

    public String getCurrentTable() {
        return currentTable;
    }

    public ArrayList<TestItem> queryData(ArrayList<TestItem> queryListitem) {
        ArrayList<TestItem> resultListItem = queryListitem;
        for (int i = 0; i < resultListItem.size(); i++) {
            TestItem item = resultListItem.get(i);
            item.setTestResult(getTestListItemStatus(item.getTestClassName()));
        }
        return resultListItem;
    }

    public int getTestListItemStatus(String name) {
        if (mSqLiteDatabase == null) {
            Log.e(TAG, "getTestListItemStatus, mSqLiteDatabase == null");
            return Const.DEFAULT;
        }

        Cursor cursor = mSqLiteDatabase.query(currentTable,
                new String[] { "value" }, "name=" + "\'" + name + "\'", null,
                null, null, null);
        Log.d(TAG, "name=" + name);
        if(cursor == null) return Const.DEFAULT;
        Log.d(TAG, "cursor.count=" + cursor.getCount());
        try {
            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                if (cursor.getInt(0) == Const.FAIL) {
                    return Const.FAIL;
                } else if (cursor.getInt(0) == Const.SUCCESS) {
                    return Const.SUCCESS;
                } else {
                    return Const.DEFAULT;
                }
            } else {
                Log.d(TAG, "cursor.count2=" + cursor.getCount());
                return Const.DEFAULT;
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            Log.d(TAG, "fianlly");
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return Const.DEFAULT;
    }

    public void updateData(String name, int value) {
        if (mSqLiteDatabase == null) {
            Log.e(TAG, "updateData, mSqLiteDatabase == null");
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(ENG_STRING2INT_NAME, name);
        cv.put(ENG_STRING2INT_VALUE, value);
        cv.put(ENG_STRING2INT_VALUE, value);
        mSqLiteDatabase.beginTransactionNonExclusive();
        try {
            mSqLiteDatabase.update(currentTable, cv,
                    ENG_STRING2INT_NAME + "= \'" + name + "\'", null);
            mSqLiteDatabase.setTransactionSuccessful();
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
        } finally{
            mSqLiteDatabase.endTransaction();
        }
    }

    public void updateDB(String name, int value) {
        if (queryData(name)) {
            updateData(name, value);
        } else {
            Log.d(TAG, "Error,unqueryData");
            if (name != null) {
                insertData(name, value);
            }
        }
    }

    private boolean queryData(String name) {
        if (mSqLiteDatabase == null) {
            Log.e(TAG, "queryData, mSqLiteDatabase == null");
            return false;
        }

        try {
            Cursor c = mSqLiteDatabase.query(currentTable,
                    new String[] { ENG_STRING2INT_NAME, ENG_STRING2INT_VALUE },
                    ENG_STRING2INT_NAME + "= \'" + name + "\'", null, null,
                    null, null);
            if (c != null) {
                if (c.getCount() > 0) {
                    c.close();
                    return true;
                }
                c.close();
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private void insertData(String name, int value) {
        ContentValues cv = new ContentValues();
        cv.put(ENG_STRING2INT_NAME, name);
        cv.put(ENG_STRING2INT_VALUE, value);
        Log.d(TAG, "name" + name + "value:" + value);

        if (mSqLiteDatabase == null) {
            Log.e(TAG, "insertData, mSqLiteDatabase == null");
            return;
        }

        long returnValue = mSqLiteDatabase.insert(currentTable, null, cv);
        Log.e(TAG, "returnValue" + returnValue);
        if (returnValue == -1) {
            Log.e(TAG, "insert DB error!");
        }
    }

    public int queryNotTestCount() {
        int bln = 0;
        if (mSqLiteDatabase == null)
            return bln;
        Cursor cur = mSqLiteDatabase.query(currentTable, new String[] {
                        "name", "value" }, "value=?", new String[] { "2" }, null, null,
                null);
        if (cur != null) {
            bln = cur.getCount();
            cur.close();
        }
        return bln;
    }

    public int queryFailCount() {
        int bln = 0;
        if (mSqLiteDatabase == null)
            return bln;
        Cursor cur = mSqLiteDatabase.query(currentTable, new String[] {
                        "name", "value" }, "value!=?", new String[] { "1" }, null,
                null, null);
        if (cur != null) {
            bln = cur.getCount();
            cur.close();
        }
        return bln;
    }

    public int querySystemFailCount() {
        ArrayList<TestItem> supportList = new ArrayList<TestItem>();

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

        supportList.addAll(list);
        int count = 0;
        for (int i = 0; i < supportList.size(); i++) {
            if (Const.SUCCESS != getTestListItemStatus(supportList.get(i)
                    .getTestClassName())) {
                count++;
            }
        }
        return count;
    }

    private static class ValidationToolsDatabaseHelper extends SQLiteOpenHelper {

        Context mContext = null;

        public ValidationToolsDatabaseHelper(Context context) {
            super(context, ENG_ENGTEST_DB, null, ENG_ENGTEST_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTable(db, ENG_STRING2INT_TABLE, UnitTestItemList.getInstance(
                    mContext).getTestItemList());
            createTable(db, ENG_MMI2_TABLE, UnitTestItemList.getInstance(
                    mContext).getMMI2ItemList());
            createTable(db, ENG_SMT_TABLE, UnitTestItemList.getInstance(
                    mContext).getSMTItemList());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + ENG_STRING2INT_TABLE + ";");
                db.execSQL("DROP TABLE IF EXISTS " + ENG_MMI2_TABLE + ";");
                db.execSQL("DROP TABLE IF EXISTS " + ENG_SMT_TABLE + ";");
                onCreate(db);
            }
        }

        private void createTable(SQLiteDatabase db, String table, ArrayList<TestItem> list) {
            db.execSQL("DROP TABLE IF EXISTS " + table + ";");
            db.execSQL("CREATE TABLE " + table + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ENG_GROUPID_VALUE + " INTEGER NOT NULL DEFAULT 0,"
                    + ENG_STRING2INT_NAME + " TEXT,"
                    + ENG_STRING2INT_DISPLAYNAME + " TEXT,"
                    + ENG_STRING2INT_VALUE + " INTEGER NOT NULL DEFAULT 0"
                    + ");");

            for (int index = 0; index < list.size(); index++) {
                ContentValues cv = new ContentValues();
                cv.put(table, list.get(index)
                        .getTestClassName());
                cv.put(ENG_STRING2INT_DISPLAYNAME, list.get(index)
                        .getTestName());
                cv.put(ENG_STRING2INT_VALUE, String.valueOf(Const.DEFAULT));
                long returnValue = db.insert(table, null, cv);
                if (returnValue == -1) {
                    Log.e(TAG, "insert DB error!");
                    continue;
                }
            }
        }

    }
}
