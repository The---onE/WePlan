package com.xmx.weplan.Plan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

import java.util.List;

/**
 * Created by The_onE on 2016/1/30.
 */
public class PlanAdapter extends BaseAdapter {
    Context mContext;
    List<Plan> mPlans;

    public PlanAdapter(Context context, List<Plan> plans) {
        mContext = context;
        mPlans = plans;
    }

    public void changeList(List<Plan> plans) {
        mPlans = plans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPlans.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < mPlans.size()) {
            return mPlans.get(position);
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_plan, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            holder.time = (TextView) convertView.findViewById(R.id.item_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position < mPlans.size()) {
            holder.title.setText(mPlans.get(position).getTitle());
            holder.title.setTextColor(Color.BLACK);

            holder.time.setText(mPlans.get(position).getTime());
        } else {
            holder.title.setText("加载失败");
            holder.time.setText("");
        }

        return convertView;
    }
}