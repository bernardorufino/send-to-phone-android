package com.brufino.sendtophone.app.messaging;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import com.brufino.sendtophone.app.Preferences;
import com.brufino.sendtophone.app.R;
import com.brufino.sendtophone.app.services.AppService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.common.collect.ImmutableMap;
import com.jaredrummler.android.device.DeviceName;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

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
            String name = DeviceName.getDeviceName();
            String token = iid.getToken(
                    getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            String userEmail = getSharedPreferences(Preferences.GENERAL_PREFERENCES, MODE_PRIVATE)
                    .getString(Preferences.KEY_USER_EMAIL, null);
            String deviceProvidedId = Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            /* TODO: Actually it can, trace this case and handle it, see InstanceIdListenerServiceImpl */
            checkNotNull(userEmail, "userEmail can't be null");

            Call<Map<String, String>> call = AppService.getApi().registerDevice(new ImmutableMap.Builder<String, String>()
                    .put("name", name)
                    .put("token", token)
                    .put("user_email", userEmail)
                    .put("provided_id", deviceProvidedId)
                    .build());

            Log.d("S2P", "flying to register device with:");
            Log.d("S2P", "  name = " + name);
            Log.d("S2P", "  token = " + token);
            Log.d("S2P", "  user_email = " + userEmail);
            Log.d("S2P", "  provided_id = " + deviceProvidedId);

            Response<Map<String, String>> response = call.execute();

            Log.d("S2P", "returned with:");
            Log.d("S2P", "  server response code = " + response.code());
            Log.d("S2P", "  server response body = " + response.body());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("S2P", "token refresh error", e);
        }
    }
}
