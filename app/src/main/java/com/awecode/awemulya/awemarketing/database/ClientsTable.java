package com.awecode.awemulya.awemarketing.database;

/**
 * Created by awemulya on 21/03/16.
 */
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ClientsTable {

    // Database table
    public static final String TABLE_Clients = "clients";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ADDRESS = "address";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_Clients
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CATEGORY + " text not null, "
            + COLUMN_SUMMARY + " text not null,"
            + COLUMN_ADDRESS + "text not null,"
            + COLUMN_DESCRIPTION
            + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ClientsTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_Clients);
        onCreate(database);
    }

}

