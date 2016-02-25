package com.xmx.weplan.Plan;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.xmx.weplan.ActivityBase.BaseTempActivity;
import com.xmx.weplan.Database.PlanManager;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

public class InformationActivity extends BaseTempActivity
        implements BGARefreshLayout.BGARefreshLayoutDelegate {

    SwipeMenuListView planList;
    PlanCardAdapter adapter;
    BGARefreshLayout mRefreshLayout;

    private class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (PlanManager.getInstance().updatePlans() >= 0) {
                adapter.changeList();
            }
            mRefreshLayout.endRefreshing();
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_information);

        setTitle("计划");

        planList = getViewById(R.id.list_plan_card);
        adapter = new PlanCardAdapter(this);
        planList.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem completeItem = new SwipeMenuItem(getApplicationContext());
                completeItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                completeItem.setWidth(300);
                completeItem.setTitle("开始啦");
                completeItem.setTitleSize(20);
                completeItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(completeItem);

                SwipeMenuItem cancelItem = new SwipeMenuItem(getApplicationContext());
                cancelItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                cancelItem.setWidth(200);
                cancelItem.setIcon(android.R.drawable.ic_menu_delete);
                menu.addMenuItem(cancelItem);
            }
        };

        planList.setMenuCreator(creator);

        planList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Plan plan = (Plan) adapter.getItem(position);
                int id = plan.getId();
                switch (index) {
                    case 0: //Complete
                        SQLManager.getInstance().completePlan(id);
                        PlanManager.getInstance().updatePlans();
                        adapter.changeList();
                        break;
                    case 1: //Cancel
                        SQLManager.getInstance().cancelPlan(id);
                        PlanManager.getInstance().updatePlans();
                        adapter.changeList();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        planList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        initRefreshLayout();
    }

    private void initRefreshLayout() {
        mRefreshLayout = getViewById(R.id.item_refresh);
        // 为BGARefreshLayout设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        // 设置正在加载更多时的文本
        refreshViewHolder.setLoadingMoreText("正在加载");

        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置整个加载更多控件的背景颜色资源id
        //refreshViewHolder.setLoadMoreBackgroundColorRes(loadMoreBackgroundColorRes);
        // 设置整个加载更多控件的背景drawable资源id
        //refreshViewHolder.setLoadMoreBackgroundDrawableRes(loadMoreBackgroundDrawableRes);
        // 设置下拉刷新控件的背景颜色资源id
        //refreshViewHolder.setRefreshViewBackgroundColorRes(refreshViewBackgroundColorRes);
        // 设置下拉刷新控件的背景drawable资源id
        //refreshViewHolder.setRefreshViewBackgroundDrawableRes(refreshViewBackgroundDrawableRes);
        // 设置自定义头部视图（也可以不用设置）     参数1：自定义头部视图（例如广告位）， 参数2：上拉加载更多是否可用
        //mRefreshLayout.setCustomHeaderView(mBanner, false);
        // 可选配置  -------------END
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        int id = getIntent().getIntExtra("id", -1);
        for (int i = 0; i < adapter.getCount(); ++i) {
            if (adapter.getItemId(i) == id) {
                planList.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        RefreshHandler timerHandler = new RefreshHandler();
        timerHandler.sendEmptyMessage(0);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return false;
    }
}
