package com.awecode.awemulya.awemarketing.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.awecode.awemulya.awemarketing.database.ClientsDatabaseHelper;
import com.awecode.awemulya.awemarketing.database.ClientsTable;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by awemulya on 22/03/16.
 */
public class ClientsContentProvider  extends ContentProvider{
    private ClientsDatabaseHelper database;

    // used for the UriMacher
    private static final int CLIENTS = 10;
    private static final int CLIENT_ID = 20;

    private static final String AUTHORITY = "com.awecode.awemulya.awemarketing.contentprovider";

    private static final String BASE_PATH = "clients";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/clients";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/clients";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, CLIENTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CLIENT_ID);
    }

    @Override
    public boolean onCreate() {
        database = new ClientsDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(ClientsTable.TABLE_CLIENTS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CLIENTS:
                break;
            case CLIENT_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(ClientsTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    private void checkColumns(String[] projection) {
        String[] available = { ClientsTable.COLUMN_CATEGORY,
                ClientsTable.COLUMN_SUMMARY, ClientsTable.COLUMN_DESCRIPTION,
                ClientsTable.COLUMN_ID , ClientsTable.COLUMN_ADDRESS};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }



    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        long id = 0;
        switch (uriType){
            case CLIENTS:
                id = sqLiteDatabase.insert(ClientsTable.TABLE_CLIENTS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri"+ uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH+ "/"+ id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case CLIENTS:
                rowsDeleted = sqlDB.delete(ClientsTable.TABLE_CLIENTS, selection,
                        selectionArgs);
                break;
            case CLIENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ClientsTable.TABLE_CLIENTS,
                            ClientsTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(ClientsTable.TABLE_CLIENTS,
                            ClientsTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case CLIENTS:
                rowsUpdated = sqlDB.update(ClientsTable.TABLE_CLIENTS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CLIENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ClientsTable.TABLE_CLIENTS,
                            values,
                            ClientsTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(ClientsTable.TABLE_CLIENTS,
                            values,
                            ClientsTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
