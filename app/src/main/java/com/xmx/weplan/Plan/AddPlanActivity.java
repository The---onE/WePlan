package com.xmx.weplan.Plan;

import android.graphics.Paint;
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
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.xmx.weplan.ActivityBase.BaseTempActivity;
import com.xmx.weplan.Constants;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPlanActivity extends BaseTempActivity {
    EditText titleText;
    EditText textText;

    RadioButton repeatInfinite;
    RadioButton repeatOnce;

    CheckBox dailyCheck;
    CheckBox periodCheck;

    TextView TimeTextView;
    Date planTime;

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
                TimeTextView = getViewById(R.id.time_tv);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String time = df.format(new Date());
                TimeTextView.setText(time);
                TimeTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
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
                final TimePickerView pvTime = new TimePickerView(this, TimePickerView.Type.ALL);
                Calendar calendar = Calendar.getInstance();
                pvTime.setRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 99);
                pvTime.setTime(new Date());
                pvTime.setCancelable(true);
                pvTime.setCyclic(true);
                pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String time = df.format(date);
                        TimeTextView.setText(time);
                        planTime = date;
                    }
                });
                TimeTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pvTime.show();
                    }
                });


                Button timeOk = getViewById(R.id.time_ok);
                timeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long now = System.currentTimeMillis();
                        if (planTime.getTime() > now) {
                            insertPlan(planTime);
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
