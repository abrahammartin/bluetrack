package org.jonblack.bluetrack.storage;

import java.util.Arrays;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class BluetrackContentProvider extends ContentProvider
{
  private static final String TAG = "BluetrackContentProvider";
  
  public static final String AUTHORITY = "org.jonblack.bluetrack.storage.BluetrackContentProvider";
  private static final UriMatcher sUriMatcher;
  
  // URI's
  private static final int DEVICE       = 1;
  private static final int DEVICE_ID    = 2;
  private static final int DISCOVERY    = 3;
  private static final int DISCOVERY_ID = 4;
  
  private BluetrackOpenHelper mOpenHelper;
  
  @Override
  public boolean onCreate()
  {
    mOpenHelper = new BluetrackOpenHelper(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder)
  {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    
    switch (sUriMatcher.match(uri))
    {
    case DEVICE:
      qb.setTables(DeviceTable.TABLE_NAME);
      break;
    case DEVICE_ID:
      qb.setTables(DeviceTable.TABLE_NAME);
      selection = selection + DeviceTable.COL_ID + " = " + uri.getLastPathSegment();
      break;
    case DISCOVERY:
      qb.setTables(DeviceDiscoveryTable.TABLE_NAME);
      break;
    case DISCOVERY_ID:
      qb.setTables(DeviceDiscoveryTable.TABLE_NAME);
      selection = selection + DeviceDiscoveryTable.COL_ID + " = " + uri.getLastPathSegment();
      break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    
    Log.d(TAG, String.format("Quering '%s' where '%s' with args '%s'",
                             qb.getTables(), selection,
                             Arrays.toString(selectionArgs)));
    
    SQLiteDatabase db = mOpenHelper.getReadableDatabase();
    Cursor c = qb.query(db, projection, selection, selectionArgs, null, null,
                        sortOrder);
    c.setNotificationUri(getContext().getContentResolver(), uri);
    return c;
  }

  @Override
  public String getType(Uri uri)
  {
    String contentType = null;
    
    switch (sUriMatcher.match(uri))
    {
    case DEVICE:
      contentType = "vnd.android.cursor.dir/vnd.org.jonblack.provider." +
                    DeviceTable.TABLE_NAME;
      break;
    case DEVICE_ID:
      contentType = "vnd.android.cursor.item/vnd.org.jonblack.provider." +
                    DeviceTable.TABLE_NAME;
      break;
    case DISCOVERY:
      contentType = "vnd.android.cursor.dir/vnd.org.jonblack.provider." +
                    DeviceDiscoveryTable.TABLE_NAME;
      break;
    case DISCOVERY_ID:
      contentType = "vnd.android.cursor.item/vnd.org.jonblack.provider." +
                    DeviceDiscoveryTable.TABLE_NAME;
      break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    
    return contentType;
  }

  @Override
  public Uri insert(Uri uri, ContentValues initialValues)
  {
    String table;
    Uri contentUri;
    
    // Only accept table (not row) uri's
    switch (sUriMatcher.match(uri))
    {
    case DEVICE:
      table = DeviceTable.TABLE_NAME;
      contentUri = DeviceTable.CONTENT_URI;
      break;
    case DISCOVERY:
      table = DeviceDiscoveryTable.TABLE_NAME;
      contentUri = DeviceDiscoveryTable.CONTENT_URI;
      break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    
    // Add to database
    ContentValues values;
    if (initialValues != null)
    {
      values = new ContentValues(initialValues);
    }
    else
    {
      values = new ContentValues();
    }
    
    Log.d(TAG, String.format("Inserting into '%s' values '%s'", table,
                             values.toString()));
    
    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    long rowId = db.insertOrThrow(table, null, values);
    if (rowId > 0)
    {
      Uri noteUri = ContentUris.withAppendedId(contentUri, rowId);
      getContext().getContentResolver().notifyChange(noteUri, null);
      
      return noteUri;
    }
    
    throw new SQLException("Failed to insert row into " + uri);
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs)
  {
    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    String table = null;
    
    switch (sUriMatcher.match(uri))
    {
    case DEVICE_ID:
      table = DeviceTable.TABLE_NAME;
      selection = DeviceTable.COL_ID + " = " + uri.getLastPathSegment();
      break;
    case DISCOVERY_ID:
      table = DeviceDiscoveryTable.TABLE_NAME;
      selection = DeviceDiscoveryTable.COL_ID + " = " + uri.getLastPathSegment();
      break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    
    Log.d(TAG, String.format("Deleting from table '%s' where '%s' with args '%s'",
                             table, selection, Arrays.toString(selectionArgs)));
    
    int count = db.delete(table, selection, selectionArgs);
    getContext().getContentResolver().notifyChange(uri, null);
    
    Log.d(TAG, String.format("Deleted %d rows", count));
    
    return count;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
                    String[] selectionArgs)
  {
    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    
    int count;
    switch (sUriMatcher.match(uri))
    {
      case DEVICE_ID:
        Log.d(TAG, String.format("Updating '%s' where '%s' with values '%s'",
                                 DeviceTable.TABLE_NAME, selection,
                                 values.toString()));
        selection = DeviceTable.COL_ID + " = " + uri.getLastPathSegment();
        count = db.update(DeviceTable.TABLE_NAME, values, selection,
                          selectionArgs);
        break;
      case DISCOVERY_ID:
        Log.d(TAG, String.format("Updating '%s' where '%s' with values '%s'",
                                 DeviceDiscoveryTable.TABLE_NAME, selection,
                                 values.toString()));
        selection = DeviceDiscoveryTable.COL_ID + " = " + uri.getLastPathSegment();
        count = db.update(DeviceDiscoveryTable.TABLE_NAME, values, selection,
                          selectionArgs);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }
    
    Log.d(TAG, String.format("Updated %d rows", count));
    
    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }
  
  public SQLiteOpenHelper getDatabaseHelper()
  {
    return mOpenHelper;
  }
  
  static
  {
    sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    sUriMatcher.addURI(AUTHORITY, "device",   DEVICE);
    sUriMatcher.addURI(AUTHORITY, "device/#", DEVICE_ID);
    sUriMatcher.addURI(AUTHORITY, "device_discovery",   DISCOVERY);
    sUriMatcher.addURI(AUTHORITY, "device_discovery/#", DISCOVERY_ID);
  }
}