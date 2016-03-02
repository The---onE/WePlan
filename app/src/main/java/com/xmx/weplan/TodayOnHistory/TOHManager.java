package com.xmx.weplan.TodayOnHistory;

import android.content.Context;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xmx.weplan.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by The_onE on 2016/3/2.
 */
public class TOHManager {
    private static TOHManager instance;

    Context mContext;

    public synchronized static TOHManager getInstance() {
        if (null == instance) {
            instance = new TOHManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void setTodayOnHistory(int month, int day, final ListView list) {
        String urlString = "http://v.juhe.cn/todayOnhistory/queryEvent.php?key="
                + Constants.TOH_APP_KEY + "&date=" + month + "/" + day;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlString, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                try {
                    JSONObject jsonObject = new JSONObject(new String(response));
                    JSONArray result = jsonObject.getJSONArray("result");
                    List<TodayOnHistory> todayOnHistories = new ArrayList<>();
                    for (int i = result.length() - 1; i >= 0; --i) {
                        JSONObject item = result.getJSONObject(i);
                        int id = item.getInt("e_id");
                        String day = item.getString("day");
                        String date = item.getString("date");
                        String title = item.getString("title");
                        TodayOnHistory todayOnHistory = new TodayOnHistory(id, day, date, title);
                        todayOnHistories.add(todayOnHistory);
                    }
                    TOHAdapter tohAdapter = new TOHAdapter(mContext, todayOnHistories);
                    list.setAdapter(tohAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("JSON Exception");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                showToast("Error " + statusCode);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                showToast("Retrying");
            }
        });
    }

    protected void showToast(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }
}
