package com.xmx.weplan.Database;

import android.content.Context;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.xmx.weplan.User.Callback.AutoLoginCallback;
import com.xmx.weplan.User.UserManager;

import java.util.Date;

/**
 * Created by The_onE on 2016/2/9.
 */
public class CloudManager {
    private static CloudManager instance;

    Context mContext;

    public synchronized static CloudManager getInstance() {
        if (null == instance) {
            instance = new CloudManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void insertPlan(final long id, final String title, final String text, final Date date, final int type, final int repeat) {
        UserManager.getInstance().checkLogin(new AutoLoginCallback() {
            @Override
            public void success(AVObject user) {
                AVObject post = new AVObject("PlanList");

                post.put("sql_id", id);
                post.put("title", title);
                post.put("text", text);
                post.put("date", date.getTime());
                post.put("type", type);
                post.put("repeat", repeat);
                post.put("user", user.get("username"));
                post.put("pubTimestamp", System.currentTimeMillis() / 1000);

                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            showToast("云保存成功");
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void notLoggedIn() {
                showToast("请登录");
            }

            @Override
            public void errorNetwork() {
                showToast("网络连接失败");
            }

            @Override
            public void errorUsername() {
                showToast("请登录");
            }

            @Override
            public void errorChecksum() {
                showToast("请重新登录");
            }
        });
    }

    protected void showToast(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }

}
