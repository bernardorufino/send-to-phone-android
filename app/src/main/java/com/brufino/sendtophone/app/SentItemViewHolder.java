package com.brufino.sendtophone.app;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SentItemViewHolder extends RecyclerView.ViewHolder {

    public TextView mDescriptionView;
    public ImageView mIconView;
    public TextView mTitleView;

    public SentItemViewHolder(View itemView) {
        super(itemView);
        mIconView = (ImageView) itemView.findViewById(R.id.list_item_icon);
        mTitleView = (TextView) itemView.findViewById(R.id.list_item_title);
        mDescriptionView = (TextView) itemView.findViewById(R.id.list_item_description);
    }

    public void bind(SentItem item) {
        int iconId = (item.getType() == SentItem.Type.TEXT)
                     ? R.drawable.ic_format_quote_white_24dp
                     : R.drawable.ic_web_asset_white_24dp;
        mIconView.setImageResource(iconId);
        mTitleView.setText(item.getTitle());
        mDescriptionView.setText(item.getData());
        if (item.isRead()) {
            mTitleView.setTypeface(null, Typeface.BOLD);
            mTitleView.setTextColor(0xFF000000);
            //description.setTextColor(0xFF444444);
        } else {
            mTitleView.setTypeface(null, Typeface.NORMAL);
            mTitleView.setTextColor(0xFF444444);
            //this.description.setTextColor(0xFF666666);
        }
    }
}