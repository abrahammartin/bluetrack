package org.jonblack.bluetrack.activities;


import org.jonblack.bluetrack.R;
import org.jonblack.bluetrack.services.BluetoothLogService;
import org.jonblack.bluetrack.storage.DeviceTable;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.bluetooth.BluetoothAdapter;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends ListActivity
                          implements LoaderManager.LoaderCallbacks<Cursor>
{
  private static final String TAG = "MainActivity";
  
  /**
   * Request code for handling calls to enable bluetooth."
   */
  private static final int REQUEST_ENABLE_BT = 1;
  
  /**
   * Intent used to communicate with the BluetoothLogService.
   */
  private Intent mBluetoothLogServiceIntent;
  
  /**
   * SimpleCursorAdapter used by the list view to get data.
   */
  private SimpleCursorAdapter mAdapter;
  
  /**
   * Whether or not tracking is in progress.
   */
  private boolean mTracking = false;
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    // Get the local bluetooth adapter and check if bluetooth is supported.
    BluetoothAdapter localBtAdapter = BluetoothAdapter.getDefaultAdapter();
    if (localBtAdapter == null)
    {
      Log.w(TAG, "Bluetooth isn't supported on device.");
      
      AlertDialog ad = new AlertDialog.Builder(this).create();
      ad.setCancelable(false);
      Resources r = getResources();
      String msg = r.getString(R.string.error_bluetooth_not_supported,
                               r.getString(R.string.app_name));
      ad.setMessage(msg);
      ad.setButton(AlertDialog.BUTTON_POSITIVE,
                   r.getString(R.string.button_exit),
                   new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
          finish();
          dialog.dismiss();
        }
      });
      ad.show();
      
      return;
    }
    
    // If bluetooth is disabled, ask the user to enable it. Otherwise, start
    // the BluetoothLogService.
    if (!localBtAdapter.isEnabled())
    {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
    
    // Configure the ListView adapter, which will connect to the database.
    mAdapter= new SimpleCursorAdapter(getApplicationContext(),
                                      android.R.layout.two_line_list_item,
                                      null,
                                      new String[] {"name",
                                                    "mac_address"},
                                      new int[] {android.R.id.text1,
                                                 android.R.id.text2}, 0);
    setListAdapter(mAdapter);
  }
  
  @Override
  protected void onDestroy()
  {
    Log.d(TAG, "Destroying MainActivity");
    
    stopBluetoothLogService();
    
    super.onDestroy();
  }
  
  /**
   * @see android.app.Activity.onActivityResult
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    // Handle result for starting "enable bluetooth" intent.
    if (requestCode == REQUEST_ENABLE_BT)
    {
      if (resultCode == RESULT_OK)
      {
        Log.d(TAG, "Bluetooth enabled.");
      }
      else
      {
        Log.d(TAG, "Bluetooth not enabled.");
      }
    }
  }
  
  /**
   * Starts the BluetoothLogService. This will continue to run until
   * stopBluetoothLogService() is called.
   */
  private void startBluetoothLogService()
  {
    Log.d(TAG, "Starting BluetoothLogService.");
    
    mBluetoothLogServiceIntent = new Intent(this, BluetoothLogService.class);
    startService(mBluetoothLogServiceIntent);
    
    // Toast starting the service
    Toast toast = Toast.makeText(getApplicationContext(),
                                 getString(R.string.toast_tracking_started),
                                 Toast.LENGTH_SHORT);
    toast.show();
    
    // Cause the action bar menu to be updated so the button text can change.
    invalidateOptionsMenu();
    
    // Prepare the loader. Either re-connect with an existing one, or start a
    // new one.
    getLoaderManager().initLoader(0, null, this);
    
    mTracking = true;
  }
  
  /**
   * Stops the BluetoothLogService.
   */
  private void stopBluetoothLogService()
  {
    Log.d(TAG, "Stopping BluetoothLogService.");
    
    if (mBluetoothLogServiceIntent != null)
    {
      stopService(mBluetoothLogServiceIntent);
      
      Toast toast = Toast.makeText(getApplicationContext(),
                                   getString(R.string.toast_tracking_stopped),
                                   Toast.LENGTH_SHORT);
      toast.show();
    }
    
    // Cause the action bar menu to be updated so the button text can change.
    invalidateOptionsMenu();
    
    // Remove the adapter cursor. Devices are only show devices when tracking
    // is on.
    mAdapter.swapCursor(null);
    
    mTracking = false;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    // Now create and return a CursorLoader that will take care of
    // creating a Cursor for the data being displayed.
    return new CursorLoader(this, DeviceTable.CONTENT_URI, null, null, null,
                            null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data)
  {
    // Swap the new cursor in. (The framework will take care of closing the
    // old cursor once we return.)
    mAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader)
  {
    // This is called when the last Cursor provided to onLoadFinished()
    // above is about to be closed.  We need to make sure we are no
    // longer using it.
    mAdapter.swapCursor(null);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.action_bar, menu);
    return true;
  }
  
  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    // Set the button text depending on the service state.
    MenuItem menuItem = menu.findItem(R.id.menu_toggle_tracking);
    if (mTracking)
    {
      menuItem.setTitle(getString(R.string.ab_stop_tracking));
    }
    else
    {
      menuItem.setTitle(getString(R.string.ab_start_tracking));
    }
    
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
    case R.id.menu_toggle_tracking:
      if (mTracking)
      {
        stopBluetoothLogService();
      }
      else
      {
        startBluetoothLogService();
      }
      break;
    default:
      assert(false);
    }
    
    return true;
  }
}
