package org.jonblack.bluetrack.activities;

import org.jonblack.bluetrack.R;
import org.jonblack.bluetrack.services.BluetoothLogService;
import org.jonblack.bluetrack.storage.DeviceTable;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class LiveTrackingFragment extends ListFragment
                                  implements LoaderManager.LoaderCallbacks<Cursor>
{
  private static final String TAG = "LiveTrackingFragment";
  
  /**
   * Intent used to communicate with the BluetoothLogService.
   */
  private Intent mBluetoothLogServiceIntent;
  
  /**
   * Whether or not tracking is in progress.
   */
  private boolean mTracking = false;
  
  /**
   * SimpleCursorAdapter used by the list view to get data.
   */
  private SimpleCursorAdapter mAdapter;
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    
    setHasOptionsMenu(true);
    
    return view;
  }
  
  @Override
  public void onDestroyView()
  {
    super.onDestroyView();
    
    stopBluetoothLogService();
  }
  
  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);
    
    // Configure the ListView adapter, which will connect to the database.
    mAdapter= new SimpleCursorAdapter(getActivity(),
                                      android.R.layout.two_line_list_item,
                                      null,
                                      new String[] {"name",
                                                    "mac_address"},
                                      new int[] {android.R.id.text1,
                                                 android.R.id.text2}, 0);
    setListAdapter(mAdapter);
  }
  
  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    // Now create and return a CursorLoader that will take care of
    // creating a Cursor for the data being displayed.
    return new CursorLoader(getActivity(), DeviceTable.CONTENT_URI, null, null,
                            null, null);
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
  
  /**
   * Starts the BluetoothLogService. This will continue to run until
   * stopBluetoothLogService() is called.
   */
  private void startBluetoothLogService()
  {
    Log.d(TAG, "Starting BluetoothLogService.");
    
    mBluetoothLogServiceIntent = new Intent(getActivity(), BluetoothLogService.class);
    getActivity().startService(mBluetoothLogServiceIntent);
    
    // Toast starting the service
    Toast toast = Toast.makeText(getActivity(),
                                 getString(R.string.toast_tracking_started),
                                 Toast.LENGTH_SHORT);
    toast.show();
    
    // Cause the action bar menu to be updated so the button text can change.
    getActivity().invalidateOptionsMenu();
    
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
      getActivity().stopService(mBluetoothLogServiceIntent);
      
      Toast toast = Toast.makeText(getActivity(),
                                   getString(R.string.toast_tracking_stopped),
                                   Toast.LENGTH_SHORT);
      toast.show();
    }
    
    // Cause the action bar menu to be updated so the button text can change.
    getActivity().invalidateOptionsMenu();
    
    // Remove the adapter cursor. Devices are only show devices when tracking
    // is on.
    mAdapter.swapCursor(null);
    
    mTracking = false;
  }
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
    inflater.inflate(R.menu.action_bar, menu);
  }
  
  @Override
  public void onPrepareOptionsMenu(Menu menu)
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
