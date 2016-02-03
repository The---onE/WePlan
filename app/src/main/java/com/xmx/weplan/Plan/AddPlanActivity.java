package com.xmx.weplan.Plan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xmx.weplan.ActivityBase.BaseTempActivity;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

import java.util.Date;

public class AddPlanActivity extends BaseTempActivity {
    SQLManager sqlManager = SQLManager.getInstance();
    EditText yearText;
    EditText monthText;
    EditText dayText;
    EditText hourText;
    EditText minuteText;
    EditText secondText;

    final static int[] DaysOfMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_plan);

        yearText = getViewById(R.id.year);
        monthText = getViewById(R.id.month);
        dayText = getViewById(R.id.day);
        hourText = getViewById(R.id.hour);
        minuteText = getViewById(R.id.min);
        secondText = getViewById(R.id.sec);

        Date now = new Date(System.currentTimeMillis());
        yearText.setText("" + (now.getYear() + 1900));
        monthText.setText("" + (now.getMonth() + 1));
        dayText.setText("" + now.getDay());
        hourText.setText("" + now.getHours());
        minuteText.setText("" + now.getMinutes());
        secondText.setText("" + now.getSeconds());
    }

    @Override
    protected void setListener() {
        Button ok = getViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleText = getViewById(R.id.title);
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
                    if (sqlManager.insertPlan(title, "", plan)) {
                        showToast("添加成功");
                        finish();
                    } else {
                        showToast("添加失败");
                    }
                } else {
                    showToast("请输入将来的时间");
                }
            }
        });

        Button cancel = getViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
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
