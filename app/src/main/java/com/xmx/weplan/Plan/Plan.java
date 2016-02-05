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
    long mTime;
    String mTimeString;
    long mBefore;
    String mBeforeString;

    public Plan(int id, String title, long time) {
        mId = id;
        mTitle = title;

        mTime = time;
        Date date = new Date(time);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mTimeString = df.format(date);
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public long getTime() {
        return mTime;
    }

    public String getTimeString () {
        return mTimeString;
    }

    public void setBefore(long mBefore) {
        this.mBefore = mBefore;
    }

    public boolean checkBefore(long newBefore) {
        return mBefore != newBefore;
    }

    public String getBeforeString() {
        return mBeforeString;
    }

    public void setBeforeString(String mBeforeString) {
        this.mBeforeString = mBeforeString;
    }
}
