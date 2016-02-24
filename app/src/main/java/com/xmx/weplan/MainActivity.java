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
import com.xmx.weplan.Database.PlanManager;
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

    PlanAdapter adapter;

    TabHost tabHost;

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
        mon1.setImageResource(Constants.NUM_DRAWABLE[mon / 10]);

        ImageView mon2 = getViewById(R.id.mon2);
        mon2.setImageResource(Constants.NUM_DRAWABLE[mon % 10]);

        ImageView day1 = getViewById(R.id.day1);
        day1.setImageResource(Constants.NUM_DRAWABLE[day / 10]);

        ImageView day2 = getViewById(R.id.day2);
        day2.setImageResource(Constants.NUM_DRAWABLE[day % 10]);

        ImageView hour1 = getViewById(R.id.hour1);
        hour1.setImageResource(Constants.NUM_DRAWABLE[hour / 10]);

        ImageView hour2 = getViewById(R.id.hour2);
        hour2.setImageResource(Constants.NUM_DRAWABLE[hour % 10]);

        ImageView min1 = getViewById(R.id.min1);
        min1.setImageResource(Constants.NUM_DRAWABLE[min / 10]);

        ImageView min2 = getViewById(R.id.min2);
        min2.setImageResource(Constants.NUM_DRAWABLE[min % 10]);

        ImageView sec1 = getViewById(R.id.sec1);
        sec1.setImageResource(Constants.NUM_DRAWABLE[sec / 10]);

        ImageView sec2 = getViewById(R.id.sec2);
        sec2.setImageResource(Constants.NUM_DRAWABLE[sec % 10]);
    }

    void updatePlanList() {
        if (PlanManager.getInstance().updatePlans()) {
            adapter.changeList();
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
        adapter = new PlanAdapter(this);
        planList.setAdapter(adapter);

        planList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), InformationActivity.class);
                Plan plan = (Plan) adapter.getItem(position);
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
            if ((System.currentTimeMillis() - exitTime) > Constants.LONGEST_EXIT_TIME) {
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
