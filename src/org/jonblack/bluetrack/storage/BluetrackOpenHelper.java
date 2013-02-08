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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BluetrackOpenHelper extends SQLiteOpenHelper
{
  private static final String TAG = "BluetrackOpenHelper";
  
  private static final String DB_NAME = "bluetrack";
  private static final int DB_VERSION = 3;
  
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
    
    SessionTable.onCreate(db);
    DeviceDiscoveryTable.onCreate(db);
    DeviceTable.onCreate(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    Log.i(TAG, String.format("Upgrading database version from %d to %d",
                             oldVersion,
                             newVersion));
    
    SessionTable.onUpgrade(db, oldVersion, newVersion);
    DeviceDiscoveryTable.onUpgrade(db, oldVersion, newVersion);
    DeviceTable.onUpgrade(db, oldVersion, newVersion);
  }
}
