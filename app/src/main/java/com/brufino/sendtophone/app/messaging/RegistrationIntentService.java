package com.brufino.sendtophone.app.messaging;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.brufino.sendtophone.app.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("S2P", getClass().getSimpleName() + ".onHandleIntent()");
        try {
            InstanceID iid = InstanceID.getInstance(this);
            String token = iid.getToken(
                    getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.i("S2P", "token = " + token);
            // subscribe to channels?
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("S2P", "token refresh error", e);
        }
    }
}
