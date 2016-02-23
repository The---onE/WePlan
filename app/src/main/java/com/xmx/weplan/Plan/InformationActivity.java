package com.xmx.weplan.Plan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xmx.weplan.ActivityBase.BaseTempActivity;
import com.xmx.weplan.Database.CloudManager;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

public class InformationActivity extends BaseTempActivity {
    int id;

    static final long DAY_TIME = 60 * 60 * 24 * 1000;
    static final long HOUR_TIME = 60 * 60 * 1000;
    static final long MINUTE_TIME = 60 * 1000;
    static final long SECOND_TIME = 1000;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_information);

        String title = getIntent().getStringExtra("title");
        setTitle(title);

        TextView titleView = getViewById(R.id.title);
        titleView.setText(title);

        String text = getIntent().getStringExtra("text");
        TextView textView = getViewById(R.id.text);
        if (text != null) {
            textView.setText(text);
        }

        String time = getIntent().getStringExtra("time");
        TextView timeView = getViewById(R.id.time);
        timeView.setText(time);

        boolean remind = getIntent().getBooleanExtra("remind", false);
        TextView remindView = getViewById(R.id.remind_tag);
        boolean daily = getIntent().getBooleanExtra("daily", false);
        TextView dailyView = getViewById(R.id.daily_tag);
        int period = getIntent().getIntExtra("period", 0);
        TextView periodView = getViewById(R.id.period_tag);
        if (remind) {
            remindView.setVisibility(View.VISIBLE);
        } else {
            remindView.setVisibility(View.INVISIBLE);
        }

        if (daily) {
            dailyView.setVisibility(View.VISIBLE);
        } else {
            dailyView.setVisibility(View.INVISIBLE);
        }

        if (period <= 0) {
            periodView.setVisibility(View.INVISIBLE);
        } else {
            String periodString = "";
            if (period / DAY_TIME > 0) {
                long day = period / DAY_TIME;
                periodString += day + "天";
                period %= DAY_TIME;
            }
            if (period / HOUR_TIME > 0) {
                long hour = period / HOUR_TIME;
                periodString += +hour + "小时";
                period %= HOUR_TIME;
            }
            if (period / MINUTE_TIME > 0) {
                long minute = period / MINUTE_TIME;
                periodString += +minute + "分钟";
                period %= MINUTE_TIME;
            }
            if (period / SECOND_TIME > 0) {
                long second = period / SECOND_TIME;
                periodString += +second + "秒";
            }
            periodView.setText(periodString);
            periodView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setListener() {
        id = getIntent().getIntExtra("id", -1);

        Button complete = getViewById(R.id.complete);
        if (id > 0) {
            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLManager.getInstance().completePlan(id);
                    finish();
                }
            });
        } else {
            showToast("加载出错");
        }

        Button cancel = getViewById(R.id.cancel);
        if (id > 0) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLManager.getInstance().cancelPlan(id);
                    finish();
                }
            });
        } else {
            showToast("加载出错");
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
}
