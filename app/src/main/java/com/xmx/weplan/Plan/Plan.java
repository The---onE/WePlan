package com.xmx.weplan.Plan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by The_onE on 2016/1/30.
 */
public class Plan {
    String mTitle;
    String mTime;

    public Plan(String title, String time) {
        mTitle = title;
        mTime = time;
    }

    public Plan(String title, long time) {
        mTitle = title;
        Date date = new Date(time);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mTime = df.format(date);
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTime() {
        return mTime;
    }
}
