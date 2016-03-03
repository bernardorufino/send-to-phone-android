package com.brufino.sendtophone.app.messaging;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;

public class InstanceIdListenerServiceImpl extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        /* TODO: Handle token refreshed but lost user email address (maybe user erased app data) */
        /* TODO: Save token in some sort of buffer */
        startService(intent);
    }
}
