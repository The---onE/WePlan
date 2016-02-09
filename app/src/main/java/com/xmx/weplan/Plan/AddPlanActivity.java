package com.xmx.weplan.Plan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TabHost;

import com.xmx.weplan.ActivityBase.BaseTempActivity;
import com.xmx.weplan.Database.CloudManager;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

import java.util.Date;

public class AddPlanActivity extends BaseTempActivity {
    SQLManager sqlManager = SQLManager.getInstance();
    CloudManager cloudManager = CloudManager.getInstance();

    EditText titleText;
    EditText textText;

    RadioButton repeatInfinite;
    RadioButton repeatOnce;

    CheckBox dailyCheck;

    EditText yearText;
    EditText monthText;
    EditText dayText;
    EditText hourText;
    EditText minuteText;
    EditText secondText;

    EditText delayYearText;
    EditText delayMonthText;
    EditText delayDayText;
    EditText delayHourText;
    EditText delayMinuteText;
    EditText delaySecondText;

    TabHost tabHost;

    final static int[] DaysOfMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_plan);

        titleText = getViewById(R.id.title);
        textText = getViewById(R.id.text);

        repeatInfinite = getViewById(R.id.repeat_infinite);
        repeatOnce = getViewById(R.id.repeat_once);

        dailyCheck = getViewById(R.id.daily);

        yearText = getViewById(R.id.year);
        monthText = getViewById(R.id.month);
        dayText = getViewById(R.id.day);
        hourText = getViewById(R.id.hour);
        minuteText = getViewById(R.id.min);
        secondText = getViewById(R.id.sec);

        delayYearText = getViewById(R.id.delay_year);
        delayMonthText = getViewById(R.id.delay_month);
        delayDayText = getViewById(R.id.delay_day);
        delayHourText = getViewById(R.id.delay_hour);
        delayMinuteText = getViewById(R.id.delay_minute);
        delaySecondText = getViewById(R.id.delay_second);

        Date now = new Date(System.currentTimeMillis());
        yearText.setText("" + (now.getYear() + 1900));
        monthText.setText("" + (now.getMonth() + 1));
        dayText.setText("" + now.getDate());
        hourText.setText("" + now.getHours());
        minuteText.setText("" + now.getMinutes());
        secondText.setText("" + now.getSeconds());

        tabHost = getViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("time").setIndicator("定时").setContent(R.id.tab_time));
        tabHost.addTab(tabHost.newTabSpec("delay").setIndicator("倒计时").setContent(R.id.tab_delay));
    }

    void insertPlan(String title, String text, Date plan, int type, int repeat) {
        long id = sqlManager.insertPlan(title, text, plan, type, repeat);
        if (id >= 0) {
            showToast("添加成功");
            cloudManager.setContext(this);
            cloudManager.insertPlan(id, title, text, plan, type, repeat);
            finish();
        } else {
            showToast("添加失败");
        }
    }

    @Override
    protected void setListener() {
        Button timeOk = getViewById(R.id.time_ok);
        timeOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleText.getText().toString().equals("")) {
                    showToast("请输入标题");
                    return;
                } else if (yearText.getText().toString().equals("")) {
                    showToast("请输入年份");
                    return;
                } else if (monthText.getText().toString().equals("")) {
                    showToast("请输入月份");
                    return;
                } else if (dayText.getText().toString().equals("")) {
                    showToast("请输入日期");
                    return;
                } else if (hourText.getText().toString().equals("")) {
                    showToast("请输入小时");
                    return;
                } else if (minuteText.getText().toString().equals("")) {
                    showToast("请输入分钟");
                    return;
                } else if (secondText.getText().toString().equals("")) {
                    showToast("请输入秒钟");
                    return;
                }

                Date plan = new Date(System.currentTimeMillis());
                int year = Integer.valueOf(yearText.getText().toString());
                int month = Integer.valueOf(monthText.getText().toString());
                int day = Integer.valueOf(dayText.getText().toString());
                int hour = Integer.valueOf(hourText.getText().toString());
                int minute = Integer.valueOf(minuteText.getText().toString());
                int second = Integer.valueOf(secondText.getText().toString());
                if (year <= 2099) {
                    plan.setYear(year - 1900);
                } else {
                    showToast("年份过大");
                    return;
                }
                if (1 <= month && month <= 12) {
                    plan.setMonth(month - 1);
                } else {
                    showToast("月份格式不正确");
                    return;
                }
                if (1 <= day && day <= DaysOfMonth[month - 1]) {
                    plan.setDate(day);
                } else {
                    showToast("日期格式不正确");
                    return;
                }
                if (0 <= hour && hour <= 30) {
                    plan.setHours(hour);
                } else {
                    showToast("小时格式不正确");
                    return;
                }
                if (0 <= minute && minute <= 59) {
                    plan.setMinutes(minute);
                } else {
                    showToast("分钟格式不正确");
                    return;
                }
                if (0 <= second && second <= 59) {
                    plan.setSeconds(second);

                } else {
                    showToast("秒格式不正确");
                    return;
                }

                long now = System.currentTimeMillis();
                if (plan.getTime() > now) {
                    String title = titleText.getText().toString();
                    String text = textText.getText().toString();

                    int repeat = -1;
                    if (repeatOnce.isChecked()) {
                        repeat = 1;
                    }

                    boolean dailyFlag = dailyCheck.isChecked();
                    int type = SQLManager.GENERAL_TYPE;
                    if (dailyFlag) {
                        type = SQLManager.DAILY_TYPE;
                    }

                    insertPlan(title, text, plan, type, repeat);
                } else {
                    showToast("请输入将来的时间");
                }
            }
        });

        Button delayOk = getViewById(R.id.delay_ok);
        delayOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleText.getText().toString().equals("")) {
                    showToast("请输入标题");
                    return;
                }

                if (delayYearText.getText().toString().equals("")) {
                    delayYearText.setText("0");
                }
                if (delayMonthText.getText().toString().equals("")) {
                    delayMonthText.setText("0");
                }
                if (delayDayText.getText().toString().equals("")) {
                    delayDayText.setText("0");
                }
                if (delayHourText.getText().toString().equals("")) {
                    delayHourText.setText("0");
                }
                if (delayMinuteText.getText().toString().equals("")) {
                    delayMinuteText.setText("0");
                }
                if (delaySecondText.getText().toString().equals("")) {
                    delaySecondText.setText("0");
                }

                int year = Integer.valueOf(delayYearText.getText().toString());
                int month = Integer.valueOf(delayMonthText.getText().toString());
                int day = Integer.valueOf(delayDayText.getText().toString());
                int hour = Integer.valueOf(delayHourText.getText().toString());
                int minute = Integer.valueOf(delayMinuteText.getText().toString());
                int second = Integer.valueOf(delaySecondText.getText().toString());

                Date plan = new Date(System.currentTimeMillis());
                plan.setYear(plan.getYear() + year);
                plan.setMonth(plan.getMonth() + month);
                plan.setDate(plan.getDate() + day);
                plan.setHours(plan.getHours() + hour);
                plan.setMinutes(plan.getMinutes() + minute);
                plan.setSeconds(plan.getSeconds() + second);

                String title = titleText.getText().toString();
                String text = textText.getText().toString();

                int repeat = -1;
                if (repeatOnce.isChecked()) {
                    repeat = 1;
                }

                boolean dailyFlag = dailyCheck.isChecked();
                int type = SQLManager.GENERAL_TYPE;
                if (dailyFlag) {
                    type = SQLManager.DAILY_TYPE;
                }

                insertPlan(title, text, plan, type, repeat);
            }
        });

        Button timeCancel = getViewById(R.id.time_cancel);
        timeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button delayCancel = getViewById(R.id.delay_cancel);
        delayCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
