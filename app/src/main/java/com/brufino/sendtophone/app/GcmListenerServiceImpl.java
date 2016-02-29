package com.brufino.sendtophone.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.brufino.sendtophone.app.sentitem.SentItem;
import com.brufino.sendtophone.app.sentitem.SentItemsManager;
import com.google.android.gms.gcm.GcmListenerService;
import org.joda.time.DateTime;

public class GcmListenerServiceImpl extends GcmListenerService {

    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onMessageReceived(String from, Bundle payload) {
        String type = payload.getString("type");
        String title = payload.getString("title");
        String description = payload.getString("description");
        String data = payload.getString("data");
        DateTime date = DateTime.now();

        if (type == null || title == null || description == null ||  data == null) {
            Log.e("S2P", "Malformed message received");
            return;
        }

        String actionType = payload.getString("action_type", "notification");

        SentItem sentItem = SentItem.create(type, title, description, data, date);

        SentItemsManager manager = SentItemsManager.getInstance();
        manager.load(getApplicationContext());
        manager.insert(sentItem);
        manager.save(getApplicationContext());

        int notifId = sentItem.getId();
        Notification notification;
        switch (actionType) {
            case "notification":
                notification = sentItem.getNotification(this, true).build();
                mNotificationManager.notify(notifId, notification);
                break;
            case "open":
                notification = sentItem.getNotification(this, false).build();
                mNotificationManager.notify(notifId, notification);
                Intent intent = sentItem.getOpenIntentProxy(this);
                startActivity(intent);
                break;
        }
    }
}
