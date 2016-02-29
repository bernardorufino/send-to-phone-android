package com.brufino.sendtophone.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import static com.google.common.base.Preconditions.*;

public class SendTextToClipboardActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();

        String text = getIntent().getExtras().getString(Intent.EXTRA_TEXT);
        checkState(text != null, getClass().getSimpleName() + " wasn't provided a text");

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(Helper.truncate(text, 30), text);
        clipboard.setPrimaryClip(clip);

        //Toast.makeText(this, "'" + text + "' copied to clipboard", Toast.LENGTH_LONG).show();
        finish();
    }
}
