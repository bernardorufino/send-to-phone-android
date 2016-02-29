package com.brufino.sendtophone.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import com.brufino.sendtophone.app.sentitem.SentItem;
import com.brufino.sendtophone.app.sentitem.SentItemsManager;

import static com.google.common.base.Preconditions.*;

public class OpenProxyActivity extends Activity {

    public static final String EXTRA_INTENT = "extra_intent";
    public static final String EXTRA_SENT_ITEM_ID = "extra_sent_item_id ";
    private SentItemsManager mSentItemsManager;

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent().getParcelableExtra(EXTRA_INTENT);
        checkState(intent != null, getClass().getSimpleName() + " wasn't provided a delegate intent");

        int id = getIntent().getIntExtra(EXTRA_SENT_ITEM_ID, SentItem.UNDEFINED_ID);
        checkState(id != SentItem.UNDEFINED_ID, getClass().getSimpleName() + " received an undefined SentItem id");

        mSentItemsManager = SentItemsManager.getInstance();
        if (!mSentItemsManager.hasLoaded()) {
            new MarkSentItemAsReadTask().execute(id);
        } else {
            SentItem sentItem = mSentItemsManager.checkedGetById(id);
            if (!sentItem.isRead()) {
                new MarkSentItemAsReadTask().execute(id);
            }
            // Avoid triggering the task if the manager has loaded and the item has already been marked as read
        }

        startActivity(intent);
        finish();
    }

    /* TODO: Leak? */
    private class MarkSentItemAsReadTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int id = params[0];
            mSentItemsManager.load(getApplicationContext());
            SentItem sentItem = mSentItemsManager.checkedGetById(id);
            if (!sentItem.isRead()) {
                sentItem.setRead(true);
                sentItem.cancelNotification(getApplicationContext());
                mSentItemsManager.save(getApplicationContext());
                mSentItemsManager.notifyDataChanged();
            }
            return null;
        }
    }
}
