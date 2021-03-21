package com.example.khatabook;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khatabook.data.CustomerContract;

public class CustomerCursorAdapter extends CursorAdapter {


    public CustomerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }



    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }


    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current Customer can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView)view.findViewById(R.id.name);

        TextView mobileTextView = (TextView)view.findViewById(R.id.mobile_no);
        ImageView call=(ImageView) view.findViewById(R.id.call_button);
        int mobileColumnIndex=cursor.getColumnIndex(CustomerContract.MyContract.COLUMN_MOBILE);
        long mobile=cursor.getLong(mobileColumnIndex);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dial = new Intent();
                dial.setAction("android.intent.action.DIAL");
                dial.setData(Uri.parse("tel:"+mobile));
                context.startActivity(dial);

            }
        });

        int nameColumnIndex = cursor.getColumnIndex(CustomerContract.MyContract.COLUMN_NAME);
        int breedColumnIndex = cursor.getColumnIndex(CustomerContract.MyContract.COLUMN_MOBILE);

        // Read the pet attributes from the Cursor for the current pet
        String customerName = cursor.getString(nameColumnIndex);
        String customerMobile = cursor.getString(breedColumnIndex);



        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(customerName);
        mobileTextView.setText(customerMobile);
    }
}