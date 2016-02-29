package com.brufino.sendtophone.app.sentitem;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import org.joda.time.DateTime;

import java.util.List;

public class UrlSentItem extends SentItem {

    public static final String TYPE = "url";

    public UrlSentItem(String title, String description, String data, DateTime date) {
        super(title, description, data, date);
    }

    @Override
    public Intent getOpenIntent(Context context) {
        Uri url = Uri.parse(getData());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public Drawable getIconDrawable(Context context) {
        Intent intent = getOpenIntent(context);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        ResolveInfo info = infos.get(0);
        return info.loadIcon(packageManager);
    }
}
