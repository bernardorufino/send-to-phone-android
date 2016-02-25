package com.brufino.sendtophone.app;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

public class GcmListenerServiceImpl extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("S2P", "[message] " + from + ": " + message);
    }
}
