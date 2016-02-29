package com.brufino.sendtophone.app.sentitem;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import com.brufino.sendtophone.app.R;
import com.brufino.sendtophone.app.SendTextToClipboardActivity;
import org.joda.time.DateTime;

public class TextSentItem extends SentItem {

    public static final String TYPE = "text";

    public TextSentItem(String title, String description, String data, DateTime date) {
        super(title, description, data, date);
    }

    @Override
    public Intent getOpenIntent(Context context) {
        Intent intent = new Intent(context, SendTextToClipboardActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, getData());
        return intent;
    }

    @Override
    public void executeAfterOpened(Context context, View view) {
        Snackbar.make(view, String.format("\"%s\" copied", getData()), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Drawable getIconDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.list_item_text_icon);
    }
}
