package com.xmx.weplan.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.Date;
import java.util.Random;

/**
 * Created by The_onE on 2015/10/23.
 */
public class SQLManager {
    private static SQLManager instance;

    public static final int GENERAL_TYPE = 0;
    public static final int DAILY_TYPE = 1;

    static final long DAY_TIME = 1000 * 60 * 60 * 24;

    SQLiteDatabase database = null;
    int version = new Random().nextInt();

    public synchronized static SQLManager getInstance() {
        if (null == instance) {
            instance = new SQLManager();
        }
        return instance;
    }

    private SQLManager() {
        openDatabase();
    }

    public int getVersion() {
        return version;
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

    public static long getPlanTime(Cursor c) {
        return c.getLong(5);
    }

    public static int getType(Cursor c) {
        return c.getInt(6);
    }

    public static int getRepeat(Cursor c) {
        return c.getInt(7);
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
                    "PLAN_TIME integer not null default(0), " +
                    "TYPE integer default(0), " +
                    "REPEAT integer default(-1)" +
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

    public boolean clearDatabase() {
        if (!checkDatabase()) {
            return false;
        }
        String clear = "delete from PLAN";
        database.execSQL(clear);
        String zero = "delete from sqlite_sequence where NAME = 'PLAN'";
        database.execSQL(zero);

        version++;
        return true;
    }

    public long insertPlan(String title, String text, Date date, int type, int repeat) {
        if (!checkDatabase()) {
            return -1;
        }
        ContentValues content = new ContentValues();
        content.put("TITLE", title);
        content.put("TEXT", text);
        content.put("ACTUAL_TIME", date.getTime());
        content.put("PLAN_TIME", date.getTime());
        content.put("TYPE", type);
        content.put("REPEAT", repeat);
        content.put("STATUS", 0);

        long id = database.insert("PLAN", null, content);

        version++;

        return id;
    }

    public long insertPlan(long id, String title, String text, long actualTime,
                           long planTime, int type, int repeat, int status) {
        if (!checkDatabase()) {
            return -1;
        }
        ContentValues content = new ContentValues();
        content.put("ID", id);
        content.put("TITLE", title);
        content.put("TEXT", text);
        content.put("ACTUAL_TIME", actualTime);
        content.put("PLAN_TIME", planTime);
        content.put("TYPE", type);
        content.put("REPEAT", repeat);
        content.put("STATUS", status);

        database.insert("PLAN", null, content);

        version++;

        return id;
    }

    public Cursor getLatestPlan() {
        if (!checkDatabase()) {
            return null;
        }
        return database.rawQuery("select * from PLAN where STATUS = 0 order by ACTUAL_TIME asc limit " + 1, null);
    }

    public void cancelPlan(int id) {
        String update = "update PLAN set STATUS = 1 where ID = " + id;
        database.execSQL(update);

        version++;
    }

    public boolean completePlan(int id) {
        Cursor c = selectById(id);
        if (c == null) {
            return false;
        }
        if (c.moveToFirst()) {
            String update;
            int type = getType(c);
            if (type == DAILY_TYPE) {
                long planTime = getPlanTime(c);
                long now = System.currentTimeMillis();
                long newTime = planTime;
                long delta = now - planTime;
                if (delta < 0) {
                    delta = -DAY_TIME;
                }
                newTime += (delta / DAY_TIME + 1) * DAY_TIME;

                int repeat = getRepeat(c);
                if (repeat < 0) {
                    update = "update PLAN set ACTUAL_TIME = " + newTime + " where ID = " + id;
                } else {
                    update = "update PLAN set ACTUAL_TIME = " + newTime + ", REPEAT = 1 where ID = " + id;
                }
            } else {
                update = "update PLAN set STATUS = 1 where ID = " + id;
            }
            database.execSQL(update);

            version++;
            return true;
        } else {
            return false;
        }
    }

    public boolean delayPlan(int id, long newTime) {
        Cursor c = selectById(id);
        if (c == null) {
            return false;
        }
        if (c.moveToFirst()) {
            String update;
            int repeat = getRepeat(c);
            if (repeat < 0) {
                update = "update PLAN set ACTUAL_TIME = " + newTime + " where ID = " + id;
            } else {
                repeat--;
                if (repeat <= 0) {
                    if (completePlan(id)) {
                        CloudManager.getInstance().completePlan(id);
                    }
                    return false;
                } else {
                    update = "update PLAN set ACTUAL_TIME = " + newTime + ", REPEAT = " + repeat + " where ID = " + id;
                }
            }
            database.execSQL(update);

            version++;
            return true;
        } else {
            return false;
        }
    }

    public Cursor selectFuturePlan() {
        if (!checkDatabase()) {
            return null;
        }
        return database.rawQuery("select * from PLAN where STATUS = 0 order by ACTUAL_TIME", null);
    }

    public Cursor selectById(int id) {
        if (!checkDatabase()) {
            return null;
        }
        return database.rawQuery("select * from PLAN where ID=" + id, null);
    }
}
