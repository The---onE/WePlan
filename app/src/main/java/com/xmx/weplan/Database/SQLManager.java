package com.xmx.weplan.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.Date;

/**
 * Created by The_onE on 2015/10/23.
 */
public class SQLManager {
    private static SQLManager instance;

    SQLiteDatabase database = null;
    boolean changeFlag = false;

    public synchronized static SQLManager getInstance() {
        if (null == instance) {
            instance = new SQLManager();
        }
        return instance;
    }

    private SQLManager() {
        openDatabase();
    }

    public boolean isChanged() {
        return changeFlag;
    }

    public void processedChange() {
        changeFlag = false;
    }

    public static int getId(Cursor c) {
        return c.getInt(0);
    }

    public static String getTitle(Cursor c) {
        return c.getString(1);
    }

    public static long getActualTime(Cursor c) {
        return c.getLong(3);
    }

    private boolean openDatabase() {
        String d = android.os.Environment.getExternalStorageDirectory() + "/WePlan/Database";
        File dir = new File(d);
        boolean flag = dir.exists() || dir.mkdirs();

        if (flag) {
            String sqlFile = android.os.Environment.getExternalStorageDirectory() + "/WePlan/Database/note.db";
            File file = new File(sqlFile);
            database = SQLiteDatabase.openOrCreateDatabase(file, null);
            if (database == null) {
                Log.e("DatabaseError", "创建文件失败");
                return false;
            }
            // ID TITLE TEXT PHOTO TIME
            String createPlanSQL = "create table if not exists PLAN(" +
                    "ID integer not null primary key autoincrement, " +
                    "TITLE text not null, " +
                    "TEXT text, " +
                    "ACTUAL_TIME integer not null default(0), " +
                    "STATUS integer default(0), " +
                    "PLAN_TIME integer not null default(0)" +
                    ")";
            database.execSQL(createPlanSQL);
        } else {
            Log.e("DatabaseError", "创建目录失败");
            return false;
        }
        return database != null;
    }

    private boolean checkDatabase() {
        return database != null || openDatabase();
    }

    public boolean insertPlan(String title, String text, Date date) {
        if (!checkDatabase()) {
            return false;
        }
        ContentValues content = new ContentValues();
        content.put("TITLE", title);
        content.put("TEXT", text);
        content.put("ACTUAL_TIME", date.getTime());
        content.put("PLAN_TIME", date.getTime());

        database.insert("PLAN", null, content);
        changeFlag = true;

        return true;
    }

    public Cursor getLatestPlan() {
        if (!checkDatabase()) {
            return null;
        }
        return database.rawQuery("select * from PLAN where STATUS = 0 order by ACTUAL_TIME asc limit " + 1, null);
    }

    public void completePlan(int id) {
        String update = "update PLAN set STATUS = 1 where ID = " + id;
        database.execSQL(update);
        changeFlag = true;
    }

    public void delayPlan(int id, long newTime) {
        String update = "update PLAN set ACTUAL_TIME = " + newTime + " where ID = " + id;
        database.execSQL(update);
        changeFlag = true;
    }

    public Cursor selectFuturePlan() {
        if (!checkDatabase()) {
            return null;
        }
        return database.rawQuery("select * from PLAN where STATUS = 0 order by ACTUAL_TIME", null);
    }

    public Cursor selectById(long id) {
        if (!checkDatabase()) {
            return null;
        }
        return database.rawQuery("select * from PLAN where ID=" + id, null);
    }
}
