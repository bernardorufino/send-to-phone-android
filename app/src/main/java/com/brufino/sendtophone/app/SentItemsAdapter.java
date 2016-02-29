package com.brufino.sendtophone.app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.brufino.sendtophone.app.sentitem.SentItem;

import java.util.List;

public class SentItemsAdapter extends RecyclerView.Adapter<SentItemViewHolder> {

    private final List<SentItem> mList;

    public SentItemsAdapter(List<SentItem> list) {
        mList = list;
    }

    @Override
    public SentItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new SentItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SentItemViewHolder holder, int position) {
        SentItem item = mList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
