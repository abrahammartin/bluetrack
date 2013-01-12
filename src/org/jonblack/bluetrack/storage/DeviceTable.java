package org.jonblack.bluetrack.storage;

import org.jonblack.bluetrack.Device;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DeviceTable
{
  private static final String TAG = "DeviceTable";
  
  // Database table metadata
  // TODO: Some of this might be better off in a DeviceContract class.
  public static final String TABLE_NAME = "device";
  public static final String COL_ID = Device._ID;
  public static final Uri CONTENT_URI = Uri.parse("content://" + BluetrackContentProvider.AUTHORITY + "/device");
  
  // Database creation SQL statement
  private static final String DATABASE_CREATE = 
      "CREATE TABLE " + TABLE_NAME + 
      " (" +
      COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
      "mac_address TEXT NOT NULL UNIQUE," +
      "name TEXT NOT NULL);";
  
  public static void onCreate(SQLiteDatabase db)
  {
    Log.i(TAG, String.format("Creating table '%s'", TABLE_NAME));
    
    db.execSQL(DATABASE_CREATE);
  }
  
  public static void onUpgrade(SQLiteDatabase db, int oldVersion,
                               int newVersion)
  {
    Log.i(TAG, String.format("Upgrading table '%s' version from %d to %d",
                             TABLE_NAME, oldVersion, newVersion));
    
    assert(false);
  }
}
