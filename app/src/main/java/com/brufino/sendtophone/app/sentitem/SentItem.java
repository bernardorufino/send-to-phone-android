package com.brufino.sendtophone.app.sentitem;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import com.brufino.sendtophone.app.MainActivity;
import com.brufino.sendtophone.app.R;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import static com.google.common.base.Preconditions.*;

public abstract class SentItem {


    public static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();

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
        String type = line.trim();
        String title = checkNotNull(input.readLine(), errorMsg).trim();
        String description = checkNotNull(input.readLine(), errorMsg).trim();
        String data = checkNotNull(input.readLine(), errorMsg).trim();
        String dateString = checkNotNull(input.readLine(), errorMsg).trim();
        DateTime date = ISO_DATE_TIME_FORMATTER.parseDateTime(dateString);
        return create(type, title, description, data, date);
    }

    private String mDescription;
    private String mTitle;
    private String mData;
    private DateTime mDate;
    private boolean mRead;

    public SentItem(String title, String description, String data, DateTime date) {
        mTitle = title;
        mDescription = description;
        mData = data;
        mDate = date;
        mRead = false;
    }

    public String getTitle() {
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
        output.write(getType() + n);
        output.write(mTitle + n);
        output.write(mDescription + n);
        output.write(mData + n);
        output.write(ISO_DATE_TIME_FORMATTER.print(mDate) + n);
    }

    public abstract String getType();

    public abstract Intent getOpenIntent(Context context);

    public abstract Drawable getIconDrawable(Context context);

    public abstract int getCircleColor(Context context);

    public void executeAfterOpened(Context context, View view) {
        /* To be overriden */
    }

    public NotificationCompat.Builder getNotification(Context context, boolean clickToOpen) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_cloud_download_black_24dp)
                .setContentTitle(mTitle)
                .setContentText(mDescription)
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
        if (!mDescription.equals(sentItem.mDescription)) return false;
        if (!mTitle.equals(sentItem.mTitle)) return false;
        return mData.equals(sentItem.mData);
    }

    @Override
    public int hashCode() {
        int result = mDescription.hashCode();
        result = 31 * result + mTitle.hashCode();
        result = 31 * result + mData.hashCode();
        result = 31 * result + (mRead ? 1 : 0);
        return result;
    }
}
