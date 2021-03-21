package com.example.khatabook;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.example.khatabook.data.CustomerContract;
import com.example.khatabook.data.CustomerDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CUSTOMER_LOADER=0;

    CustomerCursorAdapter mCursorAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


         FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent=new Intent(MainActivity.this,AddCustomer.class);
              startActivity(intent);
            }
        });


        ListView customerListView =findViewById(R.id.list);


        View emptyView=findViewById(R.id.empty_view);
        customerListView.setEmptyView(emptyView);


        mCursorAdapter=new CustomerCursorAdapter(this,null);
        customerListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(CUSTOMER_LOADER,null,this);


        customerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,AddCustomer.class);

                Uri currentUri= ContentUris.withAppendedId(CustomerContract.MyContract.CONTENT_URI,id);

                intent.setData(currentUri);

                startActivity(intent);
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor c= getContentResolver().query(CustomerContract.MyContract.CONTENT_URI,null,null,null,null);
        mCursorAdapter.swapCursor(c);
        mCursorAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem=menu.findItem(R.id.search_view);

         MenuItem.OnActionExpandListener onActionExpandListener=new MenuItem.OnActionExpandListener() {

             FloatingActionButton fab = findViewById(R.id.fab);
             @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
               fab.hide();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                Cursor c= getContentResolver().query(CustomerContract.MyContract.CONTENT_URI,null,null,null,null);
                mCursorAdapter.swapCursor(c);
                mCursorAdapter.notifyDataSetChanged();
                fab.show();
                return true;
            }
        };

        mCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {

               Cursor c= getContentResolver().query(CustomerContract.MyContract.CONTENT_URI,
                       null,
                       CustomerContract.MyContract.COLUMN_NAME+" LIKE '%" + constraint.toString() + "%'",
                       null,//new String[]{constraint.toString()},
                       null);
               return c;
            }
        });



        final SearchView searchView = (SearchView) searchItem.getActionView();


        SearchView.OnQueryTextListener onQueryTextListener=new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text=newText;
                mCursorAdapter.getFilter().filter(text);
                mCursorAdapter.notifyDataSetChanged();
                return true;
            }
        };
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Customers.");
        searchView.setOnQueryTextListener(onQueryTextListener);
        searchItem.setOnActionExpandListener(onActionExpandListener);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_view) {
           return true;
        }
        if (id == R.id.action_delete_all_entries) {
            showDeleteConfirmationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, getString(R.string.on_start),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                CustomerContract.MyContract._ID,
                CustomerContract.MyContract.COLUMN_NAME,
                CustomerContract.MyContract.COLUMN_MOBILE,

        };

        return new CursorLoader(this,
                CustomerContract.MyContract.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
     mCursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAllCustomer();
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


    private void deleteAllCustomer() {
        int rowsDeleted = getContentResolver().delete(CustomerContract.MyContract.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }




}