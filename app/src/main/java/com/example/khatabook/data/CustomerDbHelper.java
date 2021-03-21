package com.example.khatabook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomerDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="shop.db";

    private static final int DATABASE_VERSION=1;

    public CustomerDbHelper(Context context){

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creates Table service
        String SQL_CREATE_CUSTOMERS_TABLE ="CREATE TABLE "+CustomerContract.MyContract.TABLE_NAME+"("
                +CustomerContract.MyContract._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +CustomerContract.MyContract.COLUMN_NAME+" TEXT NOT NULL,"
                +CustomerContract.MyContract.COLUMN_MOBILE+" INTEGER NOT NULL,"
                +CustomerContract.MyContract.COLUMN_DOCUMENTS+" TEXT NOT NULL,"
                +CustomerContract.MyContract.COLUMN_BILL+" INTEGER NOT NULL DEFAULT 0,"
                +CustomerContract.MyContract.COLUMN_PAID+" INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_CUSTOMERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
