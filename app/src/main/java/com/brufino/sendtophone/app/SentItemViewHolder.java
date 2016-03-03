package com.brufino.sendtophone.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.brufino.sendtophone.app.sentitem.SentItem;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

public class SentItemViewHolder extends RecyclerView.ViewHolder {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.shortDate().withLocale(Locale.getDefault());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.shortTime().withLocale(Locale.getDefault());

    public TextView mDescriptionView;
    public ImageView mIconView;
    public TextView mTitleView;
    private TextView mDate;
    private TextView mTime;

    private SentItem mSentItem;

    public SentItemViewHolder(View itemView) {
        super(itemView);
        mIconView = (ImageView) itemView.findViewById(R.id.list_item_icon);
        mTitleView = (TextView) itemView.findViewById(R.id.list_item_title);
        mDescriptionView = (TextView) itemView.findViewById(R.id.list_item_description);
        mDate = (TextView) itemView.findViewById(R.id.list_item_date);
        mTime = (TextView) itemView.findViewById(R.id.list_item_time);
        itemView.setOnClickListener(mOnClickListener);
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = mSentItem.getOpenIntentProxy(context, false);
            if (intent == null) {
                Snackbar.make(v, R.string.unresolved_activity_snack, Snackbar.LENGTH_SHORT).show();
            } else {
                context.startActivity(intent);
            }
            mSentItem.executeAfterOpened(context, v);
        }
    };

    public void bind(SentItem sentItem) {
        mSentItem = sentItem;
        mIconView.setImageDrawable(sentItem.getIconDrawable(mIconView.getContext()));

        mTitleView.setText(sentItem.getTitle(mTitleView.getContext()));
        mDescriptionView.setText(sentItem.getDescription());

        mDate.setText(DATE_FORMATTER.print(sentItem.getDate()));
        mTime.setText(TIME_FORMATTER.print(sentItem.getDate()));

        if (!sentItem.isRead()) {
            mTitleView.setTypeface(null, Typeface.BOLD);
            mTitleView.setTextColor(ContextCompat.getColor(mTitleView.getContext(), R.color.listItemTitleUnread));
            mDescriptionView.setTypeface(null, Typeface.BOLD);
        } else {
            mTitleView.setTypeface(null, Typeface.NORMAL);
            mTitleView.setTextColor(ContextCompat.getColor(mTitleView.getContext(), R.color.listItemTitleRead));
            mDescriptionView.setTypeface(null, Typeface.NORMAL);
        }
    }
}