package com.xmx.weplan;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.avos.avospush.notification.NotificationCompat;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.Plan.InformationActivity;
import com.xmx.weplan.Plan.NotificationTempActivity;

public class TimerService extends Service {
    SQLManager sqlManager = SQLManager.getInstance();
    int version = 0;

    static final long DELAY_TIME = 1000 * 60 * 3;
    int latestId;
    String latestTitle;
    long latestTime;
    long latestPlanTime;
    boolean latestFlag = false;

    Handler timerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            checkTime();

            timerHandler.sendEmptyMessageDelayed(0, 1000);
        }
    };

    boolean getLatestPlan() {
        Cursor c = sqlManager.getLatestPlan();

        if (c.moveToFirst()) {
            latestId = SQLManager.getId(c);
            latestTime = SQLManager.getActualTime(c);
            latestTitle = SQLManager.getTitle(c);
            latestPlanTime = SQLManager.getPlanTime(c);
            latestFlag = true;
        } else {
            latestFlag = false;
        }

        return latestFlag;
    }

    boolean checkTime() {
        if (sqlManager.getVersion() != version) {
            version = sqlManager.getVersion();
            if (!getLatestPlan()) {
                return false;
            }
        }

        if (latestFlag) {
            long now = System.currentTimeMillis();
            if (now > latestTime) {
                showNotification(latestId, latestTitle, now - latestPlanTime);
                //sqlManager.completePlan(id);
                long time = now + DELAY_TIME;
                sqlManager.delayPlan(latestId, time);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    void showNotification(int id, String title, long delay) {
        int notificationId = (title + "|" + id).hashCode();

        Intent intent = new Intent(this, InformationActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        PendingIntent contentIntent = PendingIntent.getActivity(this, notificationId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String content = "该 " + title + " 啦";
        if ((delay / 1000 / 60) > 0) {
            content += "， 已经拖了" + (delay / 1000 / 60) + "分钟啦！";
        }

        int startId = (title + "|" + id + "s").hashCode();
        Intent startIntent = new Intent(this, NotificationTempActivity.class);
        startIntent.putExtra("start", true);
        startIntent.putExtra("id", id);
        startIntent.putExtra("title", title);
        PendingIntent startPending = PendingIntent.getActivity(this, startId,
                startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int delayId = (title + "|" + id + "d").hashCode();
        Intent delayIntent = new Intent(this, NotificationTempActivity.class);
        delayIntent.putExtra("start", false);
        delayIntent.putExtra("id", id);
        delayIntent.putExtra("title", title);
        PendingIntent delayPending = PendingIntent.getActivity(this, delayId,
                delayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("时间到啦")
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                        .setContentText(content)
                        .addAction(R.drawable.ic_menu_send, "开始啦", startPending)
                        .addAction(R.drawable.ic_menu_slideshow, "再等会", delayPending);
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        manager.notify(notificationId, notification);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        getLatestPlan();

        timerHandler.sendEmptyMessage(0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}
