package com.brufino.sendtophone.app.sentitem;

import android.content.Context;
import android.os.AsyncTask;
import com.google.common.base.Function;
import com.google.common.base.Throwables;

import java.io.*;
import java.util.ArrayList;
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
    private int mNextId = 0;

    // Prevents outside instantiation
    private SentItemsManager() {
    }

    public boolean hasLoaded() {
        return mSentItems != null;
    }

    public void load(Context context) {
        if (hasLoaded()) {
            return;
        }
        try {
            FileInputStream inputStream = context.openFileInput(FILE_NAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            mSentItems = new ArrayList<>();
            SentItem sentItem = SentItem.read(bufferedReader);
            while (sentItem != null) {
                mNextId = Math.max(mNextId, sentItem.getId() + 1);
                mSentItems.add(sentItem);
                sentItem = SentItem.read(bufferedReader);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            // Empty!
            mSentItems = new ArrayList<>();
        } catch (IOException e) {
            /* TODO: Do something about this and use try with resources */
            throw Throwables.propagate(e);
        }
    }

    public void save(Context context) {
        try {
            FileOutputStream output = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(output));
            for (SentItem sentItem : mSentItems) {
                if (sentItem.getId() == SentItem.UNDEFINED_ID) {
                    sentItem.setId(mNextId++);
                }
                sentItem.write(bufferedWriter);
            }
            bufferedWriter.close();
        } catch (IOException e) {
            /* TODO: Do something about this and use try with resources */
            throw Throwables.propagate(e);
        }
    }

    public void triggerMarkItemAsRead(Context context, int id) {
        if (hasLoaded() && checkedGetById(id).isRead()) {
            return;
        }
        new MarkSentItemAsReadTask().execute(context, id);
    }

    public List<SentItem> getBackingList() {
        checkState(mSentItems != null, "Initialize object calling load() method first");
        return mSentItems;
    }

    public SentItem getById(int id) {
        checkArgument(id != SentItem.UNDEFINED_ID, "Parameter id can't be undefined");

        for (SentItem sentItem : mSentItems) {
            if (sentItem.getId() == id) {
                return sentItem;
            }
        }
        return null;
    }

    public SentItem checkedGetById(int id) {
        return checkNotNull(getById(id), "No SentItem found with id " + id);
    }

    /**
     * Loads the manager, inserts {@param sentItem} at {@param position} and saves.
     */
    public void insertAndSave(Context context, int position, SentItem sentItem) {
        load(context);
        mSentItems.add(position, sentItem);
        save(context);
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

    /* TODO: Leak? */
    private class MarkSentItemAsReadTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            Context context = (Context) params[0];
            int id = (int) params[1];

            load(context);
            SentItem sentItem = checkedGetById(id);
            if (!sentItem.isRead()) {
                sentItem.setRead(true);
                sentItem.cancelNotification(context);
                save(context);
                notifyDataChanged();
            }
            return null;
        }
    }
}
