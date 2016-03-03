package com.brufino.sendtophone.app.activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.brufino.sendtophone.app.Preferences;
import com.brufino.sendtophone.app.R;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetupActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_GET_ACCOUNTS = 0;
    private static final String GOOGLE_ACCOUNT = "com.google";

    private View mPermissionRequestButton;
    private View mViewAskPermission;
    private View mViewChooseEmail;
    private LinearLayout mChooseEmailListView;
    private View mChooseEmailCreate;
    private View mChooseEmailDivider;
    private View mChooseEmailConfirmButton;
    private EditText mChooseEmailManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mViewAskPermission = findViewById(R.id.setup_view_ask_permission);
        mViewChooseEmail = findViewById(R.id.setup_view_choose_email);
        mChooseEmailDivider = mViewChooseEmail.findViewById(R.id.setup_choose_email_divider);
        mChooseEmailCreate = mViewChooseEmail.findViewById(R.id.setup_choose_email_create);
        mChooseEmailListView = (LinearLayout) mViewChooseEmail.findViewById(R.id.setup_view_email_list);
        mChooseEmailManual = (EditText) mViewChooseEmail.findViewById(R.id.setup_email_manual);
        mChooseEmailConfirmButton = mViewChooseEmail.findViewById(R.id.setup_email_manual_confirm_button);
        mChooseEmailConfirmButton.setOnClickListener(mOnManualEmailEntered);

        mPermissionRequestButton = findViewById(R.id.setup_permission_request_button);
        mPermissionRequestButton.setOnClickListener(mOnPermissionRequestButtonClicked);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            onGetContactsPermissionGranted();
        } else {
            activateView(mViewAskPermission);
        }
    }

    private void activateView(View activeView) {
        View[] views = {mViewAskPermission, mViewChooseEmail};
        for (View view : views) {
            view.setVisibility((view == activeView) ? View.VISIBLE : View.GONE);
        }
    }

    private View.OnClickListener mOnPermissionRequestButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActivityCompat.requestPermissions(
                    SetupActivity.this,
                    new String[] { Manifest.permission.GET_ACCOUNTS },
                    PERMISSION_REQUEST_GET_ACCOUNTS);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_GET_ACCOUNTS:
                int ans = grantResults[0];
                if (ans == PackageManager.PERMISSION_GRANTED) {
                    onGetContactsPermissionGranted();
                } else {
                    enterEmail(getString(R.string.setup_email_permission_denied));
                }
        }
    }

    private void onGetContactsPermissionGranted() {
        Collection<String> userEmails = getUserEmails();
        if (userEmails.isEmpty()) {
            enterEmail(getString(R.string.setup_no_email_found_message));
        } else if (userEmails.size() >= 1) {
            chooseEmail(userEmails);
        }
    }

    private void enterEmail(String message) {
        activateView(mViewChooseEmail);
        Snackbar.make(mViewChooseEmail, message, Snackbar.LENGTH_LONG).show();
        setTitle("Enter Chrome account email");
        mChooseEmailDivider.setVisibility(View.GONE);
        mChooseEmailCreate.setVisibility(View.VISIBLE);
        setEmailItems(mChooseEmailListView, new HashSet<String>());
    }

    private void chooseEmail(Collection<String> emails) {
        activateView(mViewChooseEmail);
        setTitle("Choose Chrome account");
        mChooseEmailDivider.setVisibility(View.VISIBLE);
        mChooseEmailCreate.setVisibility(View.VISIBLE);
        setEmailItems(mChooseEmailListView, emails);
    }

    private void setEmailItems(LinearLayout emailListView, Collection<String> emails) {
        for (int i = 0; i < emailListView.getChildCount(); i++) {
            View child = emailListView.getChildAt(i);
            if (child.getTag() != null) {
                emailListView.removeViewAt(i);
            }
        }
        for (String email : emails) {
            TextView emailItemView = (TextView) LayoutInflater.from(this)
                    .inflate(R.layout.setup_email_list_item, emailListView, false);
            emailListView.addView(emailItemView, 0);
            emailItemView.setText(email);
            /* TODO: Use custom tag, with key */
            emailItemView.setTag(email);
            emailItemView.setOnClickListener(mOnEmailItemClick);
        }

    }

    private View.OnClickListener mOnEmailItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = (String) v.getTag();
            useEmail(email);
        }
    };

    private View.OnClickListener mOnManualEmailEntered = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = mChooseEmailManual.getText().toString().trim();
            useEmail(email);
        }
    };

    private void useEmail(String email) {
        getSharedPreferences(Preferences.GENERAL_PREFERENCES, MODE_PRIVATE)
                .edit()
                .putString(Preferences.KEY_USER_EMAIL, email)
                .apply();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_ACCOUNT_SNACK, true);
        startActivity(intent);
    }

    private Collection<String> getUserEmails() {
        AccountManager manager = AccountManager.get(getApplicationContext());
        Account[] accounts = manager.getAccountsByType(GOOGLE_ACCOUNT);
        //Account[] accounts = manager.getAccounts();
        Set<String> emails = new HashSet<>();
        for (Account account : accounts) {
            if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                emails.add(account.name);
            }
        }
        return emails;
    }
}
