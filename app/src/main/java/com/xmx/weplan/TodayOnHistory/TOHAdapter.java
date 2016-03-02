package com.xmx.weplan.TodayOnHistory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmx.weplan.R;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by The_onE on 2016/3/2.
 */
public class TOHAdapter extends BaseAdapter {
    Context mContext;

    List<TodayOnHistory> mTodayOnHistories;

    public TOHAdapter(Context context, List<TodayOnHistory> todayOnHistories) {
        mContext = context;
        mTodayOnHistories = todayOnHistories;
    }

    @Override
    public int getCount() {
        return mTodayOnHistories.size();
    }

    @Override
    public Object getItem(int position) {
        return mTodayOnHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView date;
        TextView title;
        TextView delta;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_today_on_history, null);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.toh_date);
            holder.title = (TextView) convertView.findViewById(R.id.toh_title);
            holder.delta = (TextView) convertView.findViewById(R.id.toh_delta);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position < mTodayOnHistories.size()) {
            TodayOnHistory todayOnHistory = mTodayOnHistories.get(position);
            holder.title.setText(todayOnHistory.getTitle());
            holder.title.setTextColor(Color.BLACK);
            holder.date.setText(todayOnHistory.getDate());
            holder.date.setTextColor(Color.BLACK);

            String delta = "";
            String regex = "(前?)(\\d+)年";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(todayOnHistory.getDate());
            if (matcher.find()) {
                String flag = matcher.group(1);
                int year = Integer.valueOf(matcher.group(2));
                if (flag.equals("前")) {
                    year = -year;
                }
                Calendar calendar = Calendar.getInstance();
                int now = calendar.get(Calendar.YEAR);
                delta = "" + (now - year);
            }
            holder.delta.setText(delta + "年前");
            holder.delta.setTextColor(Color.BLACK);
        } else {
            holder.title.setText("加载失败");
        }

        return convertView;
    }
}
