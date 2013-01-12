package org.jonblack.bluetrack.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jonblack.bluetrack.storage.DeviceDiscoveryTable;
import org.jonblack.bluetrack.storage.DeviceTable;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


public class BluetoothLogService extends Service
{
  private static final String TAG = "BluetoothLogService";
  
  private final int PERIODIC_EVENT_TIMEOUT = 30000;
  
  private Handler mPeriodicEventHandler;
  
  /**
   * The bluetooth adapter of this device.
   */
  private BluetoothAdapter mLocalBtAdapter;
  
  /**
   * Receiver used when bluetooth devices have been found during discovery.
   */
  private final BroadcastReceiver mBtDiscoveryReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      // Get the action that prompted the broadcast
      String action = intent.getAction();
      
      // If the discovery process found devices, add the details to the
      // database.
      if (action.equals(BluetoothDevice.ACTION_FOUND))
      {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Log.d(TAG, String.format("Found device: %s (%s)",
                                 device.getName(),
                                 device.getAddress()));
        
        // If the device is unknown, add it to the database.
        Log.i(TAG, "Checking if the device is new.");
        final Cursor c = getContentResolver().query(DeviceTable.CONTENT_URI,
                                                    new String[] {DeviceTable.COL_ID},
                                                    "mac_address = ?",
                                                    new String[] {device.getAddress()},
                                                    null);
        if (c == null)
        {
          Log.e(TAG, "Database query unexpectedly returned a null Cursor.");
          return;
        }
        
        long id = -1;
        if (c.getCount() < 1)
        {
          // No device found, add it to the database
          id = addDevice(device);
        }
        else
        {
          // Only one row should be returned from the query.
          assert(c.getCount() == 1);
          
          Log.i(TAG, "Device is known. Getting id.");
          c.moveToFirst();
          id = c.getLong(c.getColumnIndex(DeviceTable.COL_ID));
        }
        assert(id != -1);
        c.close();
        
        // Add discovery of device to database.
        addDeviceDiscovery(id);
        
        return;
      }
      
      // If the discovery process has finished, setup the handler to trigger
      // again after a delay.
      if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
      {
        Log.d(TAG, "Discovery process finished.");
        Log.d(TAG, String.format("Set handler to discover again in %d ms.",
                                 PERIODIC_EVENT_TIMEOUT));
        mPeriodicEventHandler.postDelayed(doPeriodicTask,
                                          PERIODIC_EVENT_TIMEOUT);
        
        return;
      }
    }
    
    private long addDevice(BluetoothDevice device)
    {
      Log.i(TAG, "Device is unknown. Adding device to database.");
      
      ContentValues values = new ContentValues();
      values.put("mac_address", device.getAddress());
      values.put("name", device.getName());
      Uri uri = getContentResolver().insert(DeviceTable.CONTENT_URI, values);
      
      // Get the id of the new row
      return ContentUris.parseId(uri);
    }
    
    private void addDeviceDiscovery(long id)
    {
      Log.i(TAG, "Adding discovery to database.");
      
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
      Date date = new Date();
      
      ContentValues values = new ContentValues();
      values.put("date_time", dateFormat.format(date));
      values.put("device_id", id);
      getContentResolver().insert(DeviceDiscoveryTable.CONTENT_URI, values);
    }
  };
  
  private final Runnable doPeriodicTask = new Runnable()
  {
    public void run()
    {
      Log.d(TAG, "doPeriodicTask");
      
      // Bluetooth should be available and enabled at this point.
      assert(mLocalBtAdapter != null);
      assert(mLocalBtAdapter.isEnabled());
      
      // If the bluetooth adapter isn't discovering, start discovering.
      if (!mLocalBtAdapter.isDiscovering())
      {
        Log.d(TAG, "Starting bluetooth discovery.");
        mLocalBtAdapter.startDiscovery();
      }
      else
      {
        Log.d(TAG, "Already discovering bluetooth devices.");
      }
    }
  };
  
  @Override
  public void onCreate()
  {
    Log.d(TAG, "Creating BluetoothLogService");
    
    super.onCreate();
    
    mLocalBtAdapter = BluetoothAdapter.getDefaultAdapter();
    
    // Bluetooth should be available and enabled at this point.
    assert(mLocalBtAdapter != null);
    assert(mLocalBtAdapter.isEnabled());
    
    // Register broadcast receiver for finding bluetooth devices.
    registerReceiver(mBtDiscoveryReceiver,
                     new IntentFilter(BluetoothDevice.ACTION_FOUND));
    
    // Register broadcast receiver for when discovery finished.
    registerReceiver(mBtDiscoveryReceiver,
                     new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    
    // Setup the periodic handler which will do the discovery
    mPeriodicEventHandler = new Handler();
  }
  
  @Override
  public void onDestroy()
  {
    Log.d(TAG, "Destroying BluetoothLogService");
    
    // Unregister the broadcast receiver for finding bluetooth devices.
    unregisterReceiver(mBtDiscoveryReceiver);
    
    // If the bluetooth device is in discovery mode, cancel it.
    if (mLocalBtAdapter.isDiscovering())
    {
      Log.d(TAG, "Canceling discovery.");
      mLocalBtAdapter.cancelDiscovery();
    }
    
    super.onDestroy();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId)
  {
    // Bluetooth should be available and enabled at this point.
    assert(mLocalBtAdapter != null);
    assert(mLocalBtAdapter.isEnabled());
    
    // Discover devices immediately
    mPeriodicEventHandler.post(doPeriodicTask);
    
    // We want this service to continue running until it is explicitly stopped,
    // so return sticky.
    return START_STICKY;
  }
  
  /**
   * Disables the binding interface.
   * @see android.app.Service.onBind
   */
  @Override
  public IBinder onBind(Intent intent)
  {
    return null;
  }
}
