package com.example.khatabook.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class CustomerContract {
    public static abstract class MyContract implements BaseColumns {

        public static final String TABLE_NAME = "customers";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME="name";
        public static final String COLUMN_MOBILE="mobile";
        public static final String COLUMN_BILL="bill";
        public static final String COLUMN_PAID="paid";
        public static final String COLUMN_DOCUMENTS="documents";

        public static boolean isValidMobile(String mobile){
            String regex = "(0/91)?[7-9][0-9]{9}";
           if(mobile.matches(regex))
               return true;
           else return false;
        }


        public static final String CONTENT_AUTHORITY = "com.example.khatabook";

        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        public static final String PATH_CUSTOMERS= "customers";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CUSTOMERS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of Customer.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CUSTOMERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single Customer.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CUSTOMERS;





    }
}
