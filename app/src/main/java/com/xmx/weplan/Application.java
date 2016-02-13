package com.xmx.weplan;

import com.avos.avoscloud.AVOSCloud;
import com.xmx.weplan.Database.CloudManager;
import com.xmx.weplan.User.UserManager;

/**
 * Created by The_onE on 2016/1/3.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "77ic9kqs0XrcUWCGkBxxpe2C-gzGzoHsz", "3AxKNwrCCiCdkJmADgEOeplq");
        //AVIMMessageManager.registerMessageHandler(AVIMTextMessage.class, new MessageHandler(this));
        //AVIMMessageManager.registerMessageHandler(AVIMMessage.class, new MessageBaseHandler(this));
        //PushService.setDefaultPushCallback(this, ReceiveMessageActivity.class);
        //PushService.subscribe(this, "systemmessage", ReceiveMessageActivity.class);
        //AVInstallation.getCurrentInstallation().saveInBackground();
        CloudManager.getInstance().setContext(this);
        UserManager.getInstance().setContext(this);
    }
}
