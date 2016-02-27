package com.brufino.sendtophone.app;

import android.content.Intent;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.common.base.Function;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final Random RANDOM = new Random();

    private SentItemsManager mSentItemsManager;
    private List<SentItem> mSentItemsList;
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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mSentItemsManager = SentItemsManager.getInstance();
        mSentItemsManager.load(this);
        mSentItemsManager.addChangeListener(mSentItemsListChangeListener);
        mSentItemsList = mSentItemsManager.getBackingList();

        mSentItemsRecyclerView = (RecyclerView) findViewById(R.id.list);
        mListLayoutManager = new LinearLayoutManager(this);
        mSentItemsRecyclerView.setLayoutManager(mListLayoutManager);
        mListAdapter = new SentItemsAdapter(mSentItemsList);
        mSentItemsRecyclerView.setAdapter(mListAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSentItemCallback);
        itemTouchHelper.attachToRecyclerView(mSentItemsRecyclerView);

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
            mSentItemsList.remove(position);
            mSentItemsManager.notifyDataChanged();
            mSentItemsManager.save(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            Snackbar.make(mSentItemsRecyclerView, "Item removed", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new UndoSentItemRemoveListener())
                    .show();
        }
    }

    private class UndoSentItemRemoveListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Snackbar.make(v, "TODO: Implement UNDO", Snackbar.LENGTH_LONG).show();
        }
    }
}