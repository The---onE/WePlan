package com.xmx.weplan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.avos.avoscloud.AVObject;
import com.xmx.weplan.ActivityBase.BaseNavigationActivity;
import com.xmx.weplan.Database.CloudManager;
import com.xmx.weplan.Database.PlanManager;
import com.xmx.weplan.Plan.AddPlanActivity;
import com.xmx.weplan.Plan.InformationActivity;
import com.xmx.weplan.Plan.Plan;
import com.xmx.weplan.Plan.PlanAdapter;
import com.xmx.weplan.User.Callback.AutoLoginCallback;
import com.xmx.weplan.User.UserManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseNavigationActivity {
    private long exitTime = 0;

    PlanAdapter adapter;

    List<View> viewList;
    List<String> titleList;

    long version = 0;

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
        long ver = PlanManager.getInstance().updatePlans();
        if (ver != version) {
            adapter.changeList();
            version = ver;
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        adapter = new PlanAdapter(this);

        LayoutInflater lf = LayoutInflater.from(this);
        View plan = lf.inflate(R.layout.content_plan, null);
        View circle = lf.inflate(R.layout.content_circle, null);
        View me = lf.inflate(R.layout.content_me, null);

        viewList = new ArrayList<>();// 将要分页显示的View装入数组中
        viewList.add(plan);
        viewList.add(circle);
        viewList.add(me);

        titleList = new ArrayList<>();
        titleList.add("计划");
        titleList.add("圈子");
        titleList.add("我");

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));

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

    void setPagerListener(int position) {
        switch (position) {
            case 0:
                Button addPlan = getViewById(R.id.add_plan);
                addPlan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(AddPlanActivity.class);
                    }
                });

                ListView planList = getViewById(R.id.list_plan);
                planList.setAdapter(adapter);

                planList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getBaseContext(), InformationActivity.class);
                        Plan plan = (Plan) adapter.getItem(position);
                        intent.putExtra("id", plan.getId());
                        startActivity(intent);
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
        Intent service = new Intent(this, TimerService.class);
        startService(service);

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
