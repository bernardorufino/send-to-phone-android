package com.brufino.sendtophone.app.messaging;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;

public class InstanceIdListenerServiceImpl extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
