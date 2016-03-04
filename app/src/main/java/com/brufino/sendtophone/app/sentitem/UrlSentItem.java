package com.brufino.sendtophone.app.sentitem;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import com.brufino.sendtophone.app.R;
import org.joda.time.DateTime;

import java.util.List;

public class UrlSentItem extends SentItem {

    public static final String TYPE = "url";

    private ResolveInfo mUrlResolveInfo;
    private String mTitle;
    private Drawable mIconDrawable;

    public UrlSentItem(String title, String description, String data, DateTime date) {
        super(title, description, data, date);
    }

    @Override
    public String getTitle(Context context) {
        if (mTitle != null) {
            return mTitle;
        }
        ResolveInfo info = resolveUrl(context);
        mTitle = (info == null) ? getData() : info.loadLabel(context.getPackageManager()).toString();
        return mTitle;
    }

    @Override
    public Intent getOpenIntent(Context context) {
        Uri url = Uri.parse(getData());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        if (resolveIntent(context, intent) == null) {
            return null;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public Drawable getIconDrawable(Context context) {
        if (mIconDrawable != null) {
            return mIconDrawable;
        }
        ResolveInfo info = resolveUrl(context);
        mIconDrawable = (info == null)
                ? ContextCompat.getDrawable(context, R.drawable.list_item_url_unreadable_icon)
                : info.loadIcon(context.getPackageManager());
        return mIconDrawable;
    }

    private ResolveInfo resolveUrl(Context context) {
        if (mUrlResolveInfo != null) {
            return mUrlResolveInfo;
        }
        Intent intent = getOpenIntent(context);
        mUrlResolveInfo = (intent == null) ? null : resolveIntent(context, intent);
        return mUrlResolveInfo;
    }

    private ResolveInfo resolveIntent(Context context, @NonNull Intent intent) {
        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(intent, 0);
        return infos.isEmpty() ? null : infos.get(0);
    }
}
