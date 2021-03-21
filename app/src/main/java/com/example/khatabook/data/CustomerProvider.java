package com.example.khatabook.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomerProvider extends ContentProvider {


    private static final String LOG_TAG =CustomerProvider.class.getSimpleName();
    CustomerDbHelper mDbHelper;

   //Declaring int constant for URI matcher
   private static final int CUSTOMER=100;
   private static final int CUSTOMER_ID=101;

    public static final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static{
        mUriMatcher.addURI(CustomerContract.MyContract.CONTENT_AUTHORITY,
                CustomerContract.MyContract.PATH_CUSTOMERS,CUSTOMER);

        mUriMatcher.addURI(CustomerContract.MyContract.CONTENT_AUTHORITY,
                CustomerContract.MyContract.PATH_CUSTOMERS+"/#",CUSTOMER_ID);

    }

    @Override
    public boolean onCreate() {

        mDbHelper=new CustomerDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor=null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = mUriMatcher.match(uri);
        switch (match) {
            case CUSTOMER:
                cursor=database.query(CustomerContract.MyContract.TABLE_NAME,projection,selection,
                        selectionArgs,null,null,null);
                break;
            case CUSTOMER_ID:
                selection = CustomerContract.MyContract._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the Customer table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(CustomerContract.MyContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case CUSTOMER:
                return CustomerContract.MyContract.CONTENT_LIST_TYPE;
            case CUSTOMER_ID:
                return CustomerContract.MyContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case CUSTOMER:
                return insertCustomer(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertCustomer(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(CustomerContract.MyContract.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Customer requires a name");
        }

        String mobile = values.getAsString(CustomerContract.MyContract.COLUMN_MOBILE);

        if(!CustomerContract.MyContract.isValidMobile(mobile)){

            throw new IllegalArgumentException("Customer requires valid mobile");
        }

        Integer bill = values.getAsInteger(CustomerContract.MyContract.COLUMN_BILL);

        if (bill != null && bill < 0) {
            throw new IllegalArgumentException("invalid bill");
        }

        Integer paid = values.getAsInteger(CustomerContract.MyContract.COLUMN_PAID);

        if (paid != null && paid < 0) {
            throw new IllegalArgumentException("invalid bill");
        }

        SQLiteDatabase database=mDbHelper.getWritableDatabase();

       long id=database.insert(CustomerContract.MyContract.TABLE_NAME,null,values);

      
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

       getContext().getContentResolver().notifyChange(uri,null);

       // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = mUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case CUSTOMER:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(CustomerContract.MyContract.TABLE_NAME, selection, selectionArgs);
                break;
            case CUSTOMER_ID:
                // Delete a single row given by the ID in the URI
                selection = CustomerContract.MyContract._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(CustomerContract.MyContract.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case CUSTOMER:
                return updatePet(uri, values, selection, selectionArgs);
            case CUSTOMER_ID:

                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = CustomerContract.MyContract._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        // Check that the name is not null
        String name = values.getAsString(CustomerContract.MyContract.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Customer requires a name");
        }

        String mobile = values.getAsString(CustomerContract.MyContract.COLUMN_MOBILE);

        if(!CustomerContract.MyContract.isValidMobile(mobile)){
            Log.e(LOG_TAG, "Chaking mobile " + uri);
            throw new IllegalArgumentException("Customer requires valid mobile");
        }

        Integer bill = values.getAsInteger(CustomerContract.MyContract.COLUMN_BILL);

        if (bill != null && bill < 0) {
            throw new IllegalArgumentException("invalid bill");
        }

        Integer paid = values.getAsInteger(CustomerContract.MyContract.COLUMN_PAID);

        if (paid != null && paid < 0) {
            throw new IllegalArgumentException("invalid bill");
        }



        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated = database.update(CustomerContract.MyContract.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


}
