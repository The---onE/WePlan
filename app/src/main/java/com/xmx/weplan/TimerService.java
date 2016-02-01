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

public class TimerService extends Service {
    SQLManager sqlManager = SQLManager.getInstance();
    int version = 0;

    static final long DELAY_TIME = 1000 * 60 * 5;
    int latestId;
    String latestTitle;
    long latestTime;
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
            latestFlag = true;
        } else {
            latestFlag = false;
        }

        return latestFlag;
    }

    boolean checkTime() {
        if (sqlManager.getVersion() != version) {
            if (!getLatestPlan()) {
                return false;
            }
            version = sqlManager.getVersion();
        }

        if (latestFlag) {
            long now = System.currentTimeMillis();
            if (now > latestTime) {
                showNotification(latestId, latestTitle);
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

    void showNotification(int id, String title) {
        int notificationId = title.hashCode();

        Intent intent = new Intent(this, InformationActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        PendingIntent contentIntent = PendingIntent.getActivity(this, notificationId, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("时间到啦")
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                        .setContentText("该 " + title + " 啦");
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
