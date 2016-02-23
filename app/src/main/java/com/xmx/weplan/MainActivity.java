package com.xmx.weplan;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;

import com.avos.avoscloud.AVObject;
import com.xmx.weplan.ActivityBase.BaseNavigationActivity;
import com.xmx.weplan.Database.CloudManager;
import com.xmx.weplan.Plan.AddPlanActivity;
import com.xmx.weplan.Plan.InformationActivity;
import com.xmx.weplan.Plan.Plan;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.Plan.PlanAdapter;
import com.xmx.weplan.User.Callback.AutoLoginCallback;
import com.xmx.weplan.User.UserManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseNavigationActivity {
    private long exitTime = 0;
    static final long LONGEST_EXIT_TIME = 2000;
    static final long DAY_TIME = 60 * 60 * 24;
    static final long HOUR_TIME = 60 * 60;
    static final long MINUTE_TIME = 60;
    static final long SECOND_TIME = 1;

    static int[] num = {R.drawable._0, R.drawable._1, R.drawable._2, R.drawable._3, R.drawable._4,
            R.drawable._5, R.drawable._6, R.drawable._7, R.drawable._8, R.drawable._9};

    PlanAdapter adapter;
    List<Plan> plans = new ArrayList<>();

    TabHost tabHost;

    int version = 0;

    private class TimerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            timerHandler.sendEmptyMessageDelayed(0, 1000);
            super.handleMessage(msg);

            refreshTime();

            updatePlanList();
        }
    }

    TimerHandler timerHandler = new TimerHandler();

    @Override
    protected void onStart() {
        super.onStart();

        timerHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onStop() {
        super.onStop();

        timerHandler.removeMessages(0);
    }

    void refreshTime() {
        Time t = new Time();
        t.setToNow();
        int mon = t.month + 1;
        int day = t.monthDay;
        int hour = t.hour;
        int min = t.minute;
        int sec = t.second;

        ImageView mon1 = getViewById(R.id.mon1);
        mon1.setImageResource(num[mon / 10]);

        ImageView mon2 = getViewById(R.id.mon2);
        mon2.setImageResource(num[mon % 10]);

        ImageView day1 = getViewById(R.id.day1);
        day1.setImageResource(num[day / 10]);

        ImageView day2 = getViewById(R.id.day2);
        day2.setImageResource(num[day % 10]);

        ImageView hour1 = getViewById(R.id.hour1);
        hour1.setImageResource(num[hour / 10]);

        ImageView hour2 = getViewById(R.id.hour2);
        hour2.setImageResource(num[hour % 10]);

        ImageView min1 = getViewById(R.id.min1);
        min1.setImageResource(num[min / 10]);

        ImageView min2 = getViewById(R.id.min2);
        min2.setImageResource(num[min % 10]);

        ImageView sec1 = getViewById(R.id.sec1);
        sec1.setImageResource(num[sec / 10]);

        ImageView sec2 = getViewById(R.id.sec2);
        sec2.setImageResource(num[sec % 10]);
    }

    void updatePlanList() {
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
                    if (type == SQLManager.DAILY_TYPE) {
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

        long now = System.currentTimeMillis() / 1000;
        for (Plan p : plans) {
            long pt = p.getTime() / 1000;
            long delta = pt - now;
            long newBefore = 0;
            String newBeforeString = "";
            if (delta / DAY_TIME > 0) {
                long day = delta / DAY_TIME;
                newBefore = day * DAY_TIME;
                newBeforeString = "还有" + day + "天";
            } else if (delta / HOUR_TIME > 0) {
                long hour = delta / HOUR_TIME;
                newBefore = hour * HOUR_TIME;
                newBeforeString = "还有" + hour + "小时";
            } else if (delta / MINUTE_TIME > 0) {
                long minute = delta / MINUTE_TIME;
                newBefore = minute * MINUTE_TIME;
                newBeforeString = "还有" + minute + "分钟";
            } else if (delta / SECOND_TIME > 0) {
                long second = delta / SECOND_TIME;
                newBefore = second * SECOND_TIME;
                newBeforeString = "还有" + second + "秒";
            }

            if (p.checkBefore(newBefore)) {
                changeFlag = true;
                p.setBefore(newBefore);
                p.setBeforeString(newBeforeString);
            }
        }

        if (changeFlag) {
            adapter.changeList(plans);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        Intent service = new Intent(this, TimerService.class);
        startService(service);

        tabHost = getViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("plan").setIndicator("计划").setContent(R.id.tab_plan));
        tabHost.addTab(tabHost.newTabSpec("circle").setIndicator("圈子").setContent(R.id.tab_circle));
        tabHost.addTab(tabHost.newTabSpec("me").setIndicator("我").setContent(R.id.tab_me));
    }

    @Override
    protected void setListener() {
        Button addPlan = getViewById(R.id.add_plan);
        addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddPlanActivity.class);
            }
        });

        ListView planList = getViewById(R.id.list_plan);
        adapter = new PlanAdapter(this, plans);
        planList.setAdapter(adapter);

        planList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), InformationActivity.class);
                Plan plan = plans.get(position);
                intent.putExtra("id", plan.getId());
                intent.putExtra("title", plan.getTitle());
                intent.putExtra("text", plan.getText());
                intent.putExtra("time", plan.getTimeString());
                intent.putExtra("remind", plan.isRemindFlag());
                intent.putExtra("daily", plan.isDailyFlag());
                intent.putExtra("period", plan.getPeriod());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        UserManager.getInstance().autoLogin(new AutoLoginCallback() {
            @Override
            public void success(AVObject user) {
                showToast("登录成功");
                CloudManager.getInstance().setPlansToSQL(user);
                checkLoggedIn();
            }

            @Override
            public void notLoggedIn() {
                showToast("请登录");
            }

            @Override
            public void errorNetwork() {
            }

            @Override
            public void errorUsername() {

            }

            @Override
            public void errorChecksum() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - exitTime) > LONGEST_EXIT_TIME) {
                showToast(R.string.confirm_exit);
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
