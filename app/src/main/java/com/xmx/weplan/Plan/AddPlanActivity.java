package com.xmx.weplan.Plan;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.xmx.weplan.ActivityBase.BaseTempActivity;
import com.xmx.weplan.Constants;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddPlanActivity extends BaseTempActivity {
    EditText titleText;
    EditText textText;

    RadioButton repeatInfinite;
    RadioButton repeatOnce;

    CheckBox dailyCheck;
    CheckBox periodCheck;

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

    List<View> viewList;
    List<String> titleList;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_plan);

        titleText = getViewById(R.id.title);
        textText = getViewById(R.id.text);

        repeatInfinite = getViewById(R.id.repeat_infinite);
        repeatOnce = getViewById(R.id.repeat_once);

        dailyCheck = getViewById(R.id.daily);
        periodCheck = getViewById(R.id.period);
        periodCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout period = getViewById(R.id.period_time);
                if (isChecked) {
                    period.setVisibility(View.VISIBLE);
                } else {
                    period.setVisibility(View.INVISIBLE);
                }
            }
        });

        LayoutInflater lf = LayoutInflater.from(this);
        View time = lf.inflate(R.layout.content_add_plan_by_time, null);
        View delay = lf.inflate(R.layout.content_add_plan_by_delay, null);

        viewList = new ArrayList<>();// 将要分页显示的View装入数组中
        viewList.add(time);
        viewList.add(delay);

        titleList = new ArrayList<>();
        titleList.add("定时");
        titleList.add("倒计时");

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));

                initPagerView(position);
                setPagerListener(position);

                return viewList.get(position);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        };
        ViewPager viewPager = getViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
    }

    int getEditViewInt(EditText et) {
        if (!et.getText().toString().equals("")) {
            return Integer.valueOf(et.getText().toString());
        } else {
            return 0;
        }
    }

    void insertPlan(String title, String text, Date date, int type, int repeat, int period) {
        long id = SQLManager.getInstance().insertPlan(title, text, date, type, repeat, period);
        if (id >= 0) {
            showToast("添加成功");
            finish();
        } else {
            showToast("添加失败");
        }
    }

    void insertPlan(Date plan) {
        if (titleText.getText().toString().equals("")) {
            showToast("请输入标题");
            return;
        }
        String title = titleText.getText().toString();
        String text = textText.getText().toString();

        int repeat = -1;
        if (repeatOnce.isChecked()) {
            repeat = 1;
        }

        boolean dailyFlag = dailyCheck.isChecked();
        boolean periodFlag = periodCheck.isChecked();
        int type = Constants.GENERAL_TYPE;
        if (dailyFlag) {
            if (periodFlag) {
                showToast("不能同时为每日和周期计划");
                return;
            }
            type = Constants.DAILY_TYPE;
        }

        int period = 0;
        if (periodFlag) {
            type = Constants.PERIOD_TYPE;
            EditText periodHourText = getViewById(R.id.period_hour);
            int periodHour = Integer.valueOf(periodHourText.getText().toString());
            EditText periodMinuteText = getViewById(R.id.period_minute);
            int periodMinute = Integer.valueOf(periodMinuteText.getText().toString());
            EditText periodSecondText = getViewById(R.id.period_second);
            int periodSecond = Integer.valueOf(periodSecondText.getText().toString());

            period = (periodSecond + periodMinute * 60 + periodHour * 60 * 60) * 1000;
            if (period <= Constants.MIN_PERIOD) {
                showToast("周期太小");
                return;
            }
        }

        insertPlan(title, text, plan, type, repeat, period);
    }

    void initPagerView(int position) {
        switch (position) {
            case 0:
                yearText = getViewById(R.id.year);
                monthText = getViewById(R.id.month);
                dayText = getViewById(R.id.day);
                hourText = getViewById(R.id.hour);
                minuteText = getViewById(R.id.min);
                secondText = getViewById(R.id.sec);

                Date now = new Date(System.currentTimeMillis());
                yearText.setText("" + (now.getYear() + 1900));
                monthText.setText("" + (now.getMonth() + 1));
                dayText.setText("" + now.getDate());
                hourText.setText("" + now.getHours());
                minuteText.setText("" + now.getMinutes());
                secondText.setText("" + now.getSeconds());
                break;

            case 1:
                delayYearText = getViewById(R.id.delay_year);
                delayMonthText = getViewById(R.id.delay_month);
                delayDayText = getViewById(R.id.delay_day);
                delayHourText = getViewById(R.id.delay_hour);
                delayMinuteText = getViewById(R.id.delay_minute);
                delaySecondText = getViewById(R.id.delay_second);
                break;
        }

    }

    void setPagerListener(int position) {
        switch (position) {
            case 0:
                Button timeOk = getViewById(R.id.time_ok);
                timeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (yearText.getText().toString().equals("")) {
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
                        int year = getEditViewInt(yearText);
                        int month = getEditViewInt(monthText);
                        int day = getEditViewInt(dayText);
                        int hour = getEditViewInt(hourText);
                        int minute = getEditViewInt(minuteText);
                        int second = getEditViewInt(secondText);
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
                        if (1 <= day && day <= Constants.DAYS_OF_MONTH[month - 1]) {
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
                            insertPlan(plan);
                        } else {
                            showToast("请输入将来的时间");
                        }
                    }
                });
                break;

            case 1:
                Button delayOk = getViewById(R.id.delay_ok);
                delayOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int year = getEditViewInt(delayYearText);
                        int month = getEditViewInt(delayMonthText);
                        int day = getEditViewInt(delayDayText);
                        int hour = getEditViewInt(delayHourText);
                        int minute = getEditViewInt(delayMinuteText);
                        int second = getEditViewInt(delaySecondText);

                        Date plan = new Date(System.currentTimeMillis());
                        plan.setYear(plan.getYear() + year);
                        plan.setMonth(plan.getMonth() + month);
                        plan.setDate(plan.getDate() + day);
                        plan.setHours(plan.getHours() + hour);
                        plan.setMinutes(plan.getMinutes() + minute);
                        plan.setSeconds(plan.getSeconds() + second);

                        insertPlan(plan);
                    }
                });
                break;
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
