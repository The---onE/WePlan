package com.xmx.weplan.TodayOnHistory;

/**
 * Created by The_onE on 2016/3/2.
 */
public class TodayOnHistory {
    int mId;
    String mDay;
    String mDate;
    String mTitle;

    public TodayOnHistory(int id, String day, String date, String title) {
        mId = id;
        mDay = day;
        mDate = date;
        mTitle = title;
    }

    public String getDay() {
        return mDay;
    }

    public String getDate() {
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }
}
