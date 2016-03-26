package com.awecode.awemulya.awemarketing;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.awecode.awemulya.awemarketing.contentprovider.ClientsContentProvider;
import com.awecode.awemulya.awemarketing.database.ClientsTable;

/**
 * Created by awemulya on 21/03/16.
 */
public class ClientDetailActivity extends Activity {
    private Spinner mCategory;
    private EditText mTitleText;
    private EditText mBodyText;
    private EditText mAddressText;

    private Uri clientUrl;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.client_edit);

        mCategory = (Spinner) findViewById(R.id.category);
        mTitleText = (EditText) findViewById(R.id.client_edit_summary);
        mBodyText = (EditText) findViewById(R.id.client_edit_description);
        mAddressText = (EditText) findViewById(R.id.client_edit_address);
        Button confirmButton = (Button) findViewById(R.id.client_edit_button);

        Bundle extras = getIntent().getExtras();

        // check from the saved Instance
         clientUrl= (bundle == null) ? null : (Uri) bundle
                .getParcelable(ClientsContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            clientUrl = extras
                    .getParcelable(ClientsContentProvider.CONTENT_ITEM_TYPE);

            fillData(clientUrl);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(mTitleText.getText().toString())) {
                    makeToast();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

        });
    }

    private void fillData(Uri uri) {
        String[] projection = { ClientsTable.COLUMN_SUMMARY,
                ClientsTable.COLUMN_DESCRIPTION, ClientsTable.COLUMN_CATEGORY, ClientsTable.COLUMN_ADDRESS };
//        address here
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String category = cursor.getString(cursor
                    .getColumnIndexOrThrow(ClientsTable.COLUMN_CATEGORY));

            for (int i = 0; i < mCategory.getCount(); i++) {

                String s = (String) mCategory.getItemAtPosition(i);
                if (s.equalsIgnoreCase(category)) {
                    mCategory.setSelection(i);
                }
            }

            mTitleText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(ClientsTable.COLUMN_SUMMARY)));
            mBodyText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(ClientsTable.COLUMN_DESCRIPTION)));
            mAddressText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(ClientsTable.COLUMN_ADDRESS)));

            // always close the cursor
            cursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(ClientsContentProvider.CONTENT_ITEM_TYPE, clientUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String category = (String) mCategory.getSelectedItem();
        String summary = mTitleText.getText().toString();
        String description = mBodyText.getText().toString();
        String address = mAddressText.getText().toString();

        // only save if either summary or description
        // is available

        if (description.length() == 0 && summary.length() == 0 && address.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ClientsTable.COLUMN_CATEGORY, category);
        values.put(ClientsTable.COLUMN_SUMMARY, summary);
        values.put(ClientsTable.COLUMN_DESCRIPTION, description);
        values.put(ClientsTable.COLUMN_ADDRESS, address);

        if (clientUrl == null) {
            // New todo
            clientUrl = getContentResolver().insert(ClientsContentProvider.CONTENT_URI, values);
        } else {
            // Update todo
            getContentResolver().update(clientUrl, values, null, null);
        }
    }

    private void makeToast() {
        Toast.makeText(ClientDetailActivity.this, "Please maintain a summary",
                Toast.LENGTH_LONG).show();
    }
}