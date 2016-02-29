package com.brufino.sendtophone.app.sentitem;

import android.content.Context;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.*;


public class SentItemsManager {

    private static final String FILE_NAME = "sent_items.dat";
    private List<Function<? super List<SentItem>, Void>> mListeners = new ArrayList<>();

    private static class InstanceHolder {
        private static final SentItemsManager INSTANCE = new SentItemsManager();
    }

    public static SentItemsManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private List<SentItem> mSentItems;

    // Prevents outside instantiation
    private SentItemsManager() {
    }

    public void load(Context context) {
        if (mSentItems != null) {
            return;
        }
        try {
            FileInputStream inputStream = context.openFileInput(FILE_NAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            //noinspection unchecked
            mSentItems = (LinkedList<SentItem>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            // Empty!
            mSentItems = new LinkedList<>();
        } catch (IOException | ClassNotFoundException e) {
            /* TODO: Do something about this and use try with resources */
            throw Throwables.propagate(e);
        }
    }

    public void save(Context context) {
        try {
            FileOutputStream output = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(output);
            objectOutputStream.writeObject(mSentItems);
            objectOutputStream.close();
        } catch (IOException e) {
            /* TODO: Do something about this and use try with resources */
            throw Throwables.propagate(e);
        }
    }

    public List<SentItem> getBackingList() {
        checkState(mSentItems != null, "Initialize object calling load() method");
        return mSentItems;
    }

    public List<SentItem> getAll() {
        return ImmutableList.copyOf(getBackingList());
    }

    public int count() {
        return getBackingList().size();
    }

    public void insert(SentItem sentItem) {
        mSentItems.add(0, sentItem);
        notifyDataChanged();
    }

    public void delete(int position) {
        mSentItems.remove(position);
        notifyDataChanged();
    }

    public void notifyDataChanged() {
        for (Function<? super List<SentItem>, Void> listener : mListeners) {
            listener.apply(mSentItems);
        }
    }

    public void addChangeListener(Function<? super List<SentItem>, Void> listener) {
        mListeners.add(listener);
    }
}
