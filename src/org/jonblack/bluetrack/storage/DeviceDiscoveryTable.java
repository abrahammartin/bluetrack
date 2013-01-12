package org.jonblack.bluetrack.storage;

import org.jonblack.bluetrack.DeviceDiscovery;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class DeviceDiscoveryTable
{
  private static final String TAG = "DeviceDiscoveryTable";
  
  // Database table metadata
  // TODO: Some of this might be better off in a DeviceContract class.
  public static final String TABLE_NAME = "device_discovery";
  public static final String COL_ID = DeviceDiscovery._ID;
  public static final Uri CONTENT_URI = Uri.parse("content://" + BluetrackContentProvider.AUTHORITY + "/device_discovery");
  
  // Database creation SQL statement
  private static final String DATABASE_CREATE = 
      "CREATE TABLE " + TABLE_NAME + 
      " (" +
      COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
      "date_time TEXT NOT NULL," +
      "device_id INTEGER NOT NULL, " +
      "FOREIGN KEY(device_id) REFERENCES device(" + COL_ID  + " ) ON DELETE CASCADE" +
      ");";
  
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
