package org.jonblack.bluetrack.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BluetrackOpenHelper extends SQLiteOpenHelper
{
  private static final String TAG = "BluetrackOpenHelper";
  
  private static final String DB_NAME = "bluetrack";
  private static final int DB_VERSION = 1;
  
  public BluetrackOpenHelper(Context context)
  {
    super(context, DB_NAME, null, DB_VERSION);
  }
  
  @Override
  public void onOpen(SQLiteDatabase db)
  {
    super.onOpen(db);
    if (!db.isReadOnly())
    {
      // Enable foreign key constraints
      db.execSQL("PRAGMA foreign_keys=ON;");
    }
  }
  
  @Override
  public void onCreate(SQLiteDatabase db)
  {
    Log.i(TAG, "Creating database");
    
    DeviceDiscoveryTable.onCreate(db);
    DeviceTable.onCreate(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    Log.i(TAG, String.format("Upgrading database version from %d to %d",
                             oldVersion,
                             newVersion));
    
    DeviceDiscoveryTable.onUpgrade(db, oldVersion, newVersion);
    DeviceTable.onUpgrade(db, oldVersion, newVersion);
  }
}
