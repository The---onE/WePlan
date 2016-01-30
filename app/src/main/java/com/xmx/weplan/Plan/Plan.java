package com.xmx.weplan.Plan;

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

    public String getTitle() {
        return mTitle;
    }

    public String getTime() {
        return mTime;
    }
}
