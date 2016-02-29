package com.brufino.sendtophone.app.sentitem;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.brufino.sendtophone.app.MainActivity;
import com.brufino.sendtophone.app.R;
import com.brufino.sendtophone.app.SendTextToClipboardActivity;

import java.io.Serializable;

/* TODO: Don't use serializable */
public class SentItem implements Serializable {

    private String mTitle;
    private String mData;
    private Type mType;
    private boolean mRead;

    public SentItem(String title, String data, Type type) {
        mTitle = title;
        mData = data;
        mType = type;
        mRead = false;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public boolean isRead() {
        return mRead;
    }

    public void setRead(boolean read) {
        mRead = read;
    }

    public Intent getOpenIntent(Context context) {
        Intent intent;
        switch (mType) {
            case TEXT:
                intent = new Intent(context, SendTextToClipboardActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mData);
                return intent;
            case URL:
                Uri url = Uri.parse(mData);
                intent = new Intent(Intent.ACTION_VIEW, url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return intent;
        }
        throw new AssertionError();
    }

    public String getSnackAfterOpen(Context context) {
        switch (mType) {
            case TEXT:
                return "\"" + mData + "\" copied to clipboard";
        }
        return null;
    }

    public NotificationCompat.Builder getNotification(Context context, boolean clickToOpen) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_cloud_download_black_24dp)
                .setContentTitle(mTitle)
                .setContentText(mData)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Intent intent = (clickToOpen) ? getOpenIntent(context) : new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        return builder;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SentItem)) return false;

        SentItem sentItem = (SentItem) other;

        if (mRead != sentItem.mRead) return false;
        if (!mTitle.equals(sentItem.mTitle)) return false;
        if (!mData.equals(sentItem.mData)) return false;
        return mType == sentItem.mType;

    }

    @Override
    public int hashCode() {
        int result = mTitle.hashCode();
        result = 31 * result + mData.hashCode();
        result = 31 * result + mType.hashCode();
        result = 31 * result + (mRead ? 1 : 0);
        return result;
    }
}
