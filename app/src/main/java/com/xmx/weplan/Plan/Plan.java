package com.xmx.weplan.Plan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by The_onE on 2016/1/30.
 */
public class Plan {
    int mId;
    String mTitle;
    String mTime;

    public Plan(int id, String title, String time) {
        mId = id;
        mTitle = title;
        mTime = time;
    }

    public Plan(int id, String title, long time) {
        mId = id;
        mTitle = title;
        Date date = new Date(time);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mTime = df.format(date);
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTime() {
        return mTime;
    }
}
