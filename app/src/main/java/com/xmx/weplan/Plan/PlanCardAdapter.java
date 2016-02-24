package com.xmx.weplan.Plan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.xmx.weplan.Constants;
import com.xmx.weplan.Database.PlanManager;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

import java.util.List;

/**
 * Created by The_onE on 2016/2/25.
 */
public class PlanCardAdapter extends BaseAdapter {
    Context mContext;

    public PlanCardAdapter(Context context) {
        mContext = context;
    }

    public void changeList() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return PlanManager.getInstance().getPlans().size();
    }

    @Override
    public Object getItem(int position) {
        if (position < PlanManager.getInstance().getPlans().size()) {
            return PlanManager.getInstance().getPlans().get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView title;
        TextView time;
        TextView text;

        TextView remind;
        TextView daily;
        TextView period;

        Button complete;
        Button cancel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_plan_card, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.card_title);
            holder.time = (TextView) convertView.findViewById(R.id.card_time);
            holder.text = (TextView) convertView.findViewById(R.id.card_text);
            holder.remind = (TextView) convertView.findViewById(R.id.card_remind_tag);
            holder.daily = (TextView) convertView.findViewById(R.id.card_daily_tag);
            holder.period = (TextView) convertView.findViewById(R.id.card_period_tag);
            holder.complete = (Button) convertView.findViewById(R.id.card_complete);
            holder.cancel = (Button) convertView.findViewById(R.id.card_cancel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        List<Plan> plans = PlanManager.getInstance().getPlans();
        if (position < plans.size()) {
            Plan plan = plans.get(position);
            holder.title.setText(plan.getTitle());
            holder.time.setText(plan.getTimeString());
            holder.text.setText(plan.getText());

            if (plan.isRemindFlag()) {
                holder.remind.setVisibility(View.VISIBLE);
            } else {
                holder.remind.setVisibility(View.INVISIBLE);
            }

            if (plan.isDailyFlag()) {
                holder.daily.setVisibility(View.VISIBLE);
            } else {
                holder.daily.setVisibility(View.INVISIBLE);
            }

            int period = plan.getPeriod();
            if (period <= 0) {
                holder.period.setVisibility(View.INVISIBLE);
            } else {
                String periodString = "";
                if (period / Constants.DAY_TIME > 0) {
                    long day = period / Constants.DAY_TIME;
                    periodString += day + "天";
                    period %= Constants.DAY_TIME;
                }
                if (period / Constants.HOUR_TIME > 0) {
                    long hour = period / Constants.HOUR_TIME;
                    periodString += +hour + "小时";
                    period %= Constants.HOUR_TIME;
                }
                if (period / Constants.MINUTE_TIME > 0) {
                    long minute = period / Constants.MINUTE_TIME;
                    periodString += +minute + "分钟";
                    period %= Constants.MINUTE_TIME;
                }
                if (period / Constants.SECOND_TIME > 0) {
                    long second = period / Constants.SECOND_TIME;
                    periodString += +second + "秒";
                }
                holder.period.setText(periodString);
                holder.period.setVisibility(View.VISIBLE);
            }

            final int id = plan.getId();
            if (id > 0) {
                holder.complete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SQLManager.getInstance().completePlan(id);
                    }
                });

                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SQLManager.getInstance().cancelPlan(id);
                    }
                });
            }
        } else {
            holder.title.setText("加载失败");
        }

        return convertView;
    }
}
