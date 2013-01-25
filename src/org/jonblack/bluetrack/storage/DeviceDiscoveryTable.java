//*****************************************************************************
// This file is part of bluetrack.
//
// bluetrack is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// bluetrack is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with bluetrack.  If not, see <http://www.gnu.org/licenses/>.
//*****************************************************************************

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
      "session_id INTEGER NOT NULL, " +
      "signal_strength INTEGER, " +
      "FOREIGN KEY(device_id) REFERENCES device(" + COL_ID  + " ) ON DELETE CASCADE " +
      "FOREIGN KEY(session_id) REFERENCES session(" + COL_ID  + " ) ON DELETE CASCADE " +
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
    
    switch (oldVersion)
    {
    case 1:
      upgradeF1T2(db);
      break;
    default:
      assert(false);
    }
  }
  
  /**
   * Upgrades the database from version 1 to version 2.
   * @param db
   */
  private static void upgradeF1T2(SQLiteDatabase db)
  {
    String sql = "ALTER TABLE " + TABLE_NAME +
                 " ADD COLUMN signal_strength INTEGER";
    db.execSQL(sql);
  }
}
