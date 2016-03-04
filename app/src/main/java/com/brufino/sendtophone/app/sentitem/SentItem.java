package com.brufino.sendtophone.app.sentitem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import com.brufino.sendtophone.app.R;
import com.brufino.sendtophone.app.activities.MainActivity;
import com.brufino.sendtophone.app.activities.OpenProxyActivity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import static com.google.common.base.Preconditions.*;

public abstract class SentItem {

    public static final int UNDEFINED_ID = -1;
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();

    public static SentItem create(String type, String title, String description, String data, DateTime date) {
        switch (type) {
            case "url":
                return new UrlSentItem(title, description, data, date);
            case "text":
                return new TextSentItem(title, description, data, date);
        }
        throw new IllegalArgumentException("Invalid SentItem type " + type);
    }

    /**
     * Attempts to read a serialized version of SentItem (created with method {@link #write}). If {@param input}
     * doesn't contain any more data null is returned. If {@param input} contains incomplete data an error is thrown.
     * @param input Reader from where the serial data will be pulled.
     * @return SentItem created
     * @throws IOException
     */
    public static SentItem read(BufferedReader input) throws IOException {
        String line = input.readLine();
        if (line == null) {
            return null;
        }
        String errorMsg = "Input terminated unexpectedly";

        int id = Integer.parseInt(line.trim());
        String type = checkNotNull(input.readLine(), errorMsg).trim();
        String title = checkNotNull(input.readLine(), errorMsg).trim();
        String description = checkNotNull(input.readLine(), errorMsg).trim();
        String data = checkNotNull(input.readLine(), errorMsg).trim();
        String dateString = checkNotNull(input.readLine(), errorMsg).trim();
        DateTime date = ISO_DATE_TIME_FORMATTER.parseDateTime(dateString);
        String readString = checkNotNull(input.readLine(), errorMsg).trim();
        boolean read = Boolean.parseBoolean(readString);

        SentItem sentItem = create(type, title, description, data, date);

        sentItem.mId = id;
        sentItem.mRead = read;

        return sentItem;
    }

    private int mId;
    private String mDescription;
    private String mTitle;
    private String mData;
    private DateTime mDate;
    private boolean mRead;

    public SentItem(String title, String description, String data, DateTime date) {
        mId = UNDEFINED_ID;
        mTitle = title;
        mDescription = description;
        mData = data;
        mDate = date;
        mRead = false;
    }

    public int getId() {
        return mId;
    }

    /* package private */ void setId(int id) {
        mId = id;
    }

    public String getTitle(Context context) {
        return mTitle;
    }

    protected String getData() {
        return mData;
    }

    public String getDescription() {
        return mDescription;
    }

    public DateTime getDate() {
        return mDate;
    }

    public boolean isRead() {
        return mRead;
    }

    public void setRead(boolean read) {
        mRead = read;
    }

    public void write(BufferedWriter output) throws IOException {
        String n = System.getProperty("line.separator");
        checkState(getId() != UNDEFINED_ID);
        output.write(getId() + n);
        output.write(getType() + n);
        output.write(mTitle + n);
        output.write(mDescription + n);
        output.write(mData + n);
        output.write(ISO_DATE_TIME_FORMATTER.print(mDate) + n);
        output.write(Boolean.toString(mRead) + n);
    }

    public abstract String getType();

    public void cancelNotification(Context context) {
        checkState(mId != UNDEFINED_ID, "Can't cancel notification of an unsaved SentItem");

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mId);
    }

    public abstract Intent getOpenIntent(Context context);

    public Intent getOpenIntentProxy(Context context, boolean redirectIfCantResolve) {
        checkState(mId != UNDEFINED_ID, "Can't get the proxied intent without saving the object first");

        Intent openIntent = getOpenIntent(context);
        if (openIntent == null) {
            if (redirectIfCantResolve) {
                return new Intent(context, MainActivity.class);
            } else {
                return null;
            }
        } else {
            Intent intent = new Intent(context, OpenProxyActivity.class);
            intent.putExtra(OpenProxyActivity.EXTRA_INTENT, openIntent);
            intent.putExtra(OpenProxyActivity.EXTRA_SENT_ITEM_ID, mId);
            return intent;
        }
    }

    public abstract Drawable getIconDrawable(Context context);

    public void executeAfterOpened(Context context, View view) {
        /* To be overriden */
    }

    public NotificationCompat.Builder getNotification(Context context, boolean clickToOpen) {
        checkState(mId != UNDEFINED_ID, "Can't generate notification without saving the object first");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_cloud_download_white_24dp)
                .setContentTitle(getTitle(context))
                .setContentText(mDescription)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Intent intent = (clickToOpen) ? getOpenIntentProxy(context, true) : new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, mId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        return builder;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SentItem)) return false;

        SentItem sentItem = (SentItem) other;

        if (mId != sentItem.mId) return false;
        if (mRead != sentItem.mRead) return false;
        if (mDescription != null ? !mDescription.equals(sentItem.mDescription) : sentItem.mDescription != null)
            return false;
        if (!mTitle.equals(sentItem.mTitle)) return false;
        if (!mData.equals(sentItem.mData)) return false;
        return mDate.equals(sentItem.mDate);
    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
        result = 31 * result + mTitle.hashCode();
        result = 31 * result + mData.hashCode();
        result = 31 * result + mDate.hashCode();
        result = 31 * result + (mRead ? 1 : 0);
        return result;
    }
}
