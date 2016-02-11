package com.xmx.weplan.Database;

import android.content.Context;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.xmx.weplan.User.Callback.AutoLoginCallback;
import com.xmx.weplan.User.UserManager;

import java.util.Date;
import java.util.List;

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
                post.put("status", 0);
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

    public void cancelPlan(final int id) {
        UserManager.getInstance().checkLogin(new AutoLoginCallback() {
            @Override
            public void success(AVObject user) {
                final AVQuery<AVObject> query = new AVQuery<>("PlanList");
                query.whereEqualTo("user", user.get("username"));
                query.whereEqualTo("sql_id", id);
                query.findInBackground(new FindCallback<AVObject>() {
                    public void done(List<AVObject> avObjects, AVException e) {
                        if (e == null) {
                            if (avObjects.size() > 0) {
                                final AVObject plan = avObjects.get(0);
                                plan.put("status", 1);
                                plan.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            showToast("云同步成功");
                                        } else {
                                            showToast("云同步失败");
                                        }
                                    }
                                });
                            } else {
                                showToast("云同步失败");
                            }
                        } else {
                            showToast("云同步失败");
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
