package com.awecode.awemulya.awemarketing.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by awemulya on 22/03/16.
 */
public class ClientsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "clientstable.db";
    private static final int DATABASE_VERSION = 1;

    public ClientsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ClientsTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ClientsTable.onUpgrade(db,oldVersion,newVersion);

    }
}
