package com.xmx.weplan.Database;

import android.database.Cursor;

import com.xmx.weplan.Constants;
import com.xmx.weplan.Plan.Plan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The_onE on 2016/2/24.
 */
public class PlanManager {
    private static PlanManager instance;

    int version = 0;
    List<Plan> plans = new ArrayList<>();

    public synchronized static PlanManager getInstance() {
        if (null == instance) {
            instance = new PlanManager();
        }
        return instance;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public boolean updatePlans() {
        boolean changeFlag = false;

        SQLManager sqlManager = SQLManager.getInstance();
        if (sqlManager.getVersion() != version) {
            version = sqlManager.getVersion();

            Cursor c = sqlManager.selectFuturePlan();
            plans.clear();
            if (c.moveToFirst()) {
                do {
                    int id = SQLManager.getId(c);
                    String title = SQLManager.getTitle(c);
                    String text = SQLManager.getText(c);

                    boolean remindFlag = false;
                    boolean dailyFlag = false;

                    int repeat = SQLManager.getRepeat(c);
                    if (repeat > 0) {
                        remindFlag = true;
                    }

                    int type = SQLManager.getType(c);
                    if (type == Constants.DAILY_TYPE) {
                        dailyFlag = true;
                    }
                    long time = SQLManager.getActualTime(c);

                    int period = SQLManager.getPeriod(c);

                    Plan p = new Plan(id, title, text,  time, remindFlag, dailyFlag, period);
                    plans.add(p);
                } while (c.moveToNext());
            }
            changeFlag = true;
        }

        long now = System.currentTimeMillis();
        for (Plan p : plans) {
            long pt = p.getTime();
            long delta = pt - now;
            long newBefore = 0;
            String newBeforeString = "";
            if (delta / Constants.DAY_TIME > 0) {
                long day = delta / Constants.DAY_TIME;
                newBefore = day * Constants.DAY_TIME;
                newBeforeString = "还有" + day + "天";
            } else if (delta / Constants.HOUR_TIME > 0) {
                long hour = delta / Constants.HOUR_TIME;
                newBefore = hour * Constants.HOUR_TIME;
                newBeforeString = "还有" + hour + "小时";
            } else if (delta / Constants.MINUTE_TIME > 0) {
                long minute = delta / Constants.MINUTE_TIME;
                newBefore = minute * Constants.MINUTE_TIME;
                newBeforeString = "还有" + minute + "分钟";
            } else if (delta / Constants.SECOND_TIME > 0) {
                long second = delta / Constants.SECOND_TIME;
                newBefore = second * Constants.SECOND_TIME;
                newBeforeString = "还有" + second + "秒";
            }

            if (p.checkBefore(newBefore)) {
                changeFlag = true;
                p.setBefore(newBefore);
                p.setBeforeString(newBeforeString);
            }
        }

        return changeFlag;
    }
}
