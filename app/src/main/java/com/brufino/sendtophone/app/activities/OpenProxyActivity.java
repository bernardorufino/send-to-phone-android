package com.brufino.sendtophone.app.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import com.brufino.sendtophone.app.R;
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
        mSentItemsManager.triggerMarkItemAsRead(getApplicationContext(), id);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_SNACK, getString(R.string.unresolved_activity_snack));
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        finish();
    }

}
