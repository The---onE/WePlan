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

    public abstract class FindPlan {
        Context mContext;

        FindPlan(Context context) {
            mContext = context;
        }

        protected void showToast(String str) {
            Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
        }

        public void exec(final int id) {
            UserManager.getInstance().checkLogin(new AutoLoginCallback() {
                @Override
                public void success(final AVObject user) {
                    final AVQuery<AVObject> query = new AVQuery<>("PlanList");
                    query.whereEqualTo("user", user.get("username"));
                    query.whereEqualTo("sql_id", id);
                    query.findInBackground(new FindCallback<AVObject>() {
                        public void done(List<AVObject> avObjects, AVException e) {
                            if (e == null) {
                                if (avObjects.size() > 0) {
                                    final AVObject plan = avObjects.get(0);

                                    found(user, plan);
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

        public abstract void found(AVObject user, AVObject plan);
    }

    static final long DAY_TIME = 1000 * 60 * 60 * 24;

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

    public void insertPlan(final long id, final String title, final String text, final Date date,
                           final int type, final int repeat, final int period) {
        UserManager.getInstance().checkLogin(new AutoLoginCallback() {
            @Override
            public void success(AVObject user) {
                AVObject post = new AVObject("PlanList");

                post.put("sql_id", id);
                post.put("title", title);
                post.put("text", text);
                post.put("actualTime", date.getTime());
                post.put("planTime", date.getTime());
                post.put("type", type);
                post.put("period", period);
                post.put("repeat", repeat);
                post.put("status", 0);
                post.put("user", user.get("username"));
                post.put("timestamp", System.currentTimeMillis() / 1000);

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

    public void completeDailyPlan(final int id, final long newTime, final int repeat) {
        FindPlan findplan = new FindPlan(mContext) {
            @Override
            public void found(AVObject user, AVObject plan) {
                if (repeat < 0) {
                    plan.put("actualTime", newTime);
                } else {
                    plan.put("actualTime", newTime);
                    plan.put("repeat", 1);
                }
            }
        };

        findplan.exec(id);
    }

    public void completePeriodPlan(final int id, final long newTime, final int repeat) {
        FindPlan findplan = new FindPlan(mContext) {
            @Override
            public void found(AVObject user, AVObject plan) {
                if (repeat < 0) {
                    plan.put("actualTime", newTime);
                } else {
                    plan.put("actualTime", newTime);
                    plan.put("repeat", 1);
                }
            }
        };

        findplan.exec(id);
    }

    public void completeGeneralPlan(final int id) {
        FindPlan findplan = new FindPlan(mContext) {
            @Override
            public void found(AVObject user, AVObject plan) {
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
            }
        };

        findplan.exec(id);
    }

    public void delayPlan(final int id, final long newTime, final int repeat) {
        FindPlan findPlan = new FindPlan(mContext) {
            @Override
            public void found(AVObject user, AVObject plan) {
                plan.put("actualTime", newTime);
                plan.put("repeat", repeat);

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
            }
        };

        findPlan.exec(id);
    }

    public void setPlansToSQL(AVObject user) {
        final AVQuery<AVObject> query = new AVQuery<>("PlanList");
        query.whereEqualTo("user", user.get("username"));
        query.whereEqualTo("status", 0);
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    if (avObjects.size() > 0) {
                        SQLManager sqlManager = SQLManager.getInstance();
                        sqlManager.clearDatabase();
                        for (AVObject plan : avObjects) {
                            long id = plan.getLong("sql_id");
                            String title = plan.getString("title");
                            String text = plan.getString("text");
                            long actualTime = plan.getLong("actualTime");
                            long planTime = plan.getLong("planTime");
                            int type = plan.getInt("type");
                            int repeat = plan.getInt("repeat");
                            int status = plan.getInt("status");
                            int period = plan.getInt("period");
                            sqlManager.insertPlan(id, title, text, actualTime, planTime,
                                    type, repeat, status, period);
                        }
                        showToast("云同步完成");
                    }
                } else {
                    showToast("云同步失败");
                }
            }
        });
    }

    protected void showToast(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }

}
