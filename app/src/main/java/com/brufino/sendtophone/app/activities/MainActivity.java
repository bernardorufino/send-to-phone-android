package com.brufino.sendtophone.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;
import com.brufino.sendtophone.app.Preferences;
import com.brufino.sendtophone.app.R;
import com.brufino.sendtophone.app.SentItemsAdapter;
import com.brufino.sendtophone.app.messaging.RegistrationIntentService;
import com.brufino.sendtophone.app.sentitem.SentItem;
import com.brufino.sendtophone.app.sentitem.SentItemsManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.common.base.Function;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ACCOUNT_SNACK = "account_snack";
    public static final String EXTRA_SNACK = "snack";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private SentItemsManager mSentItemsManager;
    private List<SentItem> mSentItemsList;
    private View mRootView;
    private Toolbar mToolbar;
    private RecyclerView mSentItemsRecyclerView;
    private LinearLayoutManager mListLayoutManager;
    private SentItemsAdapter mListAdapter;

    private ItemTouchHelper.SimpleCallback mSentItemCallback = new ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(
                RecyclerView recyclerView,
                RecyclerView.ViewHolder viewHolder,
                RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            new RemoveSentItemTask().execute(position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = findViewById(android.R.id.content);

        SharedPreferences prefs = getSharedPreferences(Preferences.GENERAL_PREFERENCES, MODE_PRIVATE);
        String userEmail = prefs.getString(Preferences.KEY_USER_EMAIL, null);
        if (userEmail == null) {
            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
            finish();
            return;
        } else if (getIntent().getBooleanExtra(EXTRA_ACCOUNT_SNACK, false)) {
            Snackbar.make(mRootView, userEmail, Snackbar.LENGTH_SHORT).show();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mSentItemsRecyclerView = (RecyclerView) findViewById(R.id.list);
        mListLayoutManager = new LinearLayoutManager(this);
        mSentItemsRecyclerView.setLayoutManager(mListLayoutManager);
        mListAdapter = new SentItemsAdapter();
        mSentItemsRecyclerView.setAdapter(mListAdapter);

        mSentItemsManager = SentItemsManager.getInstance();
        mSentItemsManager.addChangeListener(mSentItemsListChangeListener);
        new LoadSentItemsManagerTask().execute();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSentItemCallback);
        itemTouchHelper.attachToRecyclerView(mSentItemsRecyclerView);

        String snack = getIntent().getStringExtra(EXTRA_SNACK);
        if (snack != null) {
            Snackbar.make(mRootView, snack, Snackbar.LENGTH_SHORT).show();
        }

        if (hasGooglePlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "No Play Services", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Check if the device has Google Play Services
     */
    private boolean hasGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            return true;
        }
        if (apiAvailability.isUserResolvableError(resultCode)) {
            apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
        } else {
            // Device not supported
            finish();
        }
        return false;
    }

    private Function<List<SentItem>, Void> mSentItemsListChangeListener = new Function<List<SentItem>, Void>() {
        @Override
        public Void apply(List<SentItem> sendItemsList) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mListAdapter.notifyDataSetChanged();
                }
            });
            return null;
        }
    };

    private class RemoveSentItemTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int position = params[0];
            SentItem sentItem = mSentItemsList.get(position);
            sentItem.cancelNotification(getApplicationContext());
            mSentItemsList.remove(position);
            mSentItemsManager.save(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSentItemsManager.notifyDataChanged();
                    Snackbar.make(mSentItemsRecyclerView, "Item removed", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new UndoSentItemRemoveListener())
                            .show();
                }
            }, 100);
        }
    }

    private class LoadSentItemsManagerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            mSentItemsManager.load(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mSentItemsList = mSentItemsManager.getBackingList();
            mListAdapter.setBackingList(mSentItemsList);
        }
    }

    private class UndoSentItemRemoveListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Snackbar.make(v, "TODO: Implement UNDO", Snackbar.LENGTH_LONG).show();
        }
    }
}
