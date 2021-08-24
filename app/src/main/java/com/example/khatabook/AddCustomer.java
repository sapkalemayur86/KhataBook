 package com.example.khatabook;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.khatabook.data.CustomerContract;
import com.example.khatabook.data.CustomerDbHelper;
import com.example.khatabook.data.CustomerProvider;

public class AddCustomer extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_PET_LOADER = 0;
    private String documents="";

    private String []doc={"Income Certificate","Cast Certificate","Non Creamylayer",
                            "Nationality","Domicile","Central Cast", "Adhar Card","Pan Card"};

    private int []idArray={R.id.checkbox_income,R.id.checkbox_cast,R.id.checkbox_non_criminal,R.id.checkbox_nationality,
                                 R.id.checkbox_domecile,R.id.checkbox_central_cast,R.id.checkbox_adhar,R.id.checkbox_pan};

    // EditText variable to get input filed values
    private EditText mNameEditText;
    private EditText mMobileEditText;
    private EditText mBillEditText;
    private EditText mPaidEditText;

    private Uri mCurrentUri;

    private static final String LOG_TAG = AddCustomer.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_customer);

        Intent intent=getIntent();
        mCurrentUri=intent.getData();

        if(mCurrentUri==null){
            setTitle(getString(R.string.addCutomer));
            invalidateOptionsMenu();
        }else {
            setTitle(getString(R.string.edit_Customer));
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);

        }



        // assign view by id
        mNameEditText=(EditText)findViewById(R.id.edit_customer_name);
        mMobileEditText=(EditText)findViewById(R.id.edit_contact_no);
        mBillEditText=(EditText)findViewById(R.id.edit_customer_total_amount);
        mPaidEditText=(EditText)findViewById(R.id.edit_customer_paid_amount);



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            if(isValid()) {
                saveCustomer();
                finish();
            }
            else return false;

        }
        if (id == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isValid() {
         boolean valid=true;
        if(mNameEditText.getText().toString().length()==0){
            mNameEditText.setError("Please Enter Valid name");
            Log.e(LOG_TAG,"form Namestring");
            valid=false;
        }

        if(!CustomerContract.MyContract.isValidMobile(mMobileEditText.getText().toString())){
            mMobileEditText.setError("Please Enter valid Mobile");
            valid=false;
        }
      return valid;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }




    private void saveCustomer() {

        String nameString = mNameEditText.getText().toString().trim();

        String mobileString = mMobileEditText.getText().toString().trim();
        long mobile=Long.parseLong(mobileString);

        String billString =mBillEditText.getText().toString().trim();
        int bill= Integer.parseInt(billString);

        String paidString =mPaidEditText.getText().toString().trim();
        int paid=Integer.parseInt(paidString);

        ContentValues value=new ContentValues();


        if (mCurrentUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(mobileString) &&
                TextUtils.isEmpty(billString)) {return;}
        else if (mCurrentUri == null) {

            value.put(CustomerContract.MyContract.COLUMN_NAME,nameString);
            value.put(CustomerContract.MyContract.COLUMN_MOBILE,mobile);
            value.put(CustomerContract.MyContract.COLUMN_BILL,bill);
            value.put(CustomerContract.MyContract.COLUMN_PAID,paid);
            value.put(CustomerContract.MyContract.COLUMN_DOCUMENTS,documents);

            Uri newUri = getContentResolver().insert(CustomerContract.MyContract.CONTENT_URI, value);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_Customer_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_customer_successful),
                        Toast.LENGTH_SHORT).show();

            }

        }else {

            // Otherwise this is an EXISTING Customer, so update the customer with content URI: mCurrentUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentUri will already identify the correct row in the database that
            // we want to modify.
           String currentDoc="";
            for(int i=0;i<doc.length;i++){
                CheckBox checkBox=(CheckBox)findViewById(idArray[i]);
                if(checkBox.isChecked()){
                    currentDoc=currentDoc+doc[i];
                }
            }

            value.put(CustomerContract.MyContract.COLUMN_NAME,nameString);
            value.put(CustomerContract.MyContract.COLUMN_MOBILE,mobile);
            value.put(CustomerContract.MyContract.COLUMN_BILL,bill);
            value.put(CustomerContract.MyContract.COLUMN_PAID,paid);
            value.put(CustomerContract.MyContract.COLUMN_DOCUMENTS,currentDoc);

            int rowsAffected = getContentResolver().update(mCurrentUri, value, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_customer_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_customer_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }


    }


    public void onCheckboxClicked(View view) {
        //Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_income:
                if (checked){
                    documents=documents+"Income Certificate ";
                }
                break;
            case R.id.checkbox_cast:
                if (checked){
                    documents=documents+"Cast Certificate ";
                }
                break;

            case R.id.checkbox_non_criminal:
                if (checked){
                    documents=documents+"Non Creamylayer ";
                }
                break;

            case R.id.checkbox_nationality:
                if (checked){
                    documents=documents+"Nationality ";
                }
                break;
            case R.id.checkbox_domecile:
                if (checked){
                    documents=documents+"Domicille ";
                }
                break;
            case R.id.checkbox_central_cast:
                if (checked){
                    documents=documents+"Central Cast ";
                }
                break;
            case R.id.checkbox_adhar:
                if (checked){
                    documents=documents+"Adhar Card";
                }
                break;

            case R.id.checkbox_pan:
                if (checked){
                    documents=documents+"Pan Card ";
                }
                break;

        }


    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all Customer attributes, define a projection that contains
        // all columns from the shop table
        String[] projection = {
                CustomerContract.MyContract._ID,
                CustomerContract.MyContract.COLUMN_NAME,
                CustomerContract.MyContract.COLUMN_MOBILE,
                CustomerContract.MyContract.COLUMN_PAID,
                CustomerContract.MyContract.COLUMN_BILL,
                CustomerContract.MyContract.COLUMN_DOCUMENTS
           };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(CustomerContract.MyContract.COLUMN_NAME);
            int mobileColumnIndex = cursor.getColumnIndex(CustomerContract.MyContract.COLUMN_MOBILE);
            int paidColumnIndex = cursor.getColumnIndex(CustomerContract.MyContract.COLUMN_PAID);
            int billColumnIndex = cursor.getColumnIndex(CustomerContract.MyContract.COLUMN_BILL);
            int documentColumnIndex = cursor.getColumnIndex(CustomerContract.MyContract.COLUMN_DOCUMENTS);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            long mobile = cursor.getLong(mobileColumnIndex);
            int paid = cursor.getInt(paidColumnIndex);
            int bill = cursor.getInt(billColumnIndex);
            String currentDocuments=cursor.getString(documentColumnIndex);




            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mMobileEditText.setText(Long.toString(mobile));
            mBillEditText.setText(Integer.toString(bill));
            mPaidEditText.setText(Integer.toString(paid));
            CheckBox currentCheckBox;
            for(int i=0;i<doc.length;i++){
                if(currentDocuments.contains(doc[i])){
                    currentCheckBox=(CheckBox)findViewById(idArray[i]);
                    currentCheckBox.setChecked(true);
                }
            }





        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mMobileEditText.setText("");
        mPaidEditText.setText("");
        mBillEditText.setText("");

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteCustomer();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteCustomer() {

        // Only perform the delete if this is an existing Customer.
        if (mCurrentUri != null) {
            // Call the ContentResolver to delete the customer at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }

            finish();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
