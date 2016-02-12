package com.xmx.weplan.Plan;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.xmx.weplan.Database.CloudManager;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

public class NotificationTempActivity extends Activity {
    SQLManager sqlManager = SQLManager.getInstance();
    CloudManager cloudManager = CloudManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_temp);

        boolean flag = getIntent().getBooleanExtra("start", false);
        int id = getIntent().getIntExtra("id", -1);
        String title = getIntent().getStringExtra("title");
        if (flag) {
            if (sqlManager.completePlan(id)) {
                cloudManager.completePlan(id);
            }
        }

        int notificationId = (title + "|" + id).hashCode();

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
        finish();
    }
}
