package org.jonblack.bluetrack.adapters;

import org.jonblack.bluetrack.BluetoothClassLookup;
import org.jonblack.bluetrack.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceCursorAdapter extends CursorAdapter
{
  public DeviceCursorAdapter(Context context, Cursor cursor, int flags)
  {
    super(context, cursor, flags);
  }
  
  @Override
  public void bindView(View view, Context context, Cursor cursor)
  {
    int colIdMajorClass = cursor.getColumnIndexOrThrow("major_class");
    final int majorClass = cursor.getInt(colIdMajorClass);
    int colIdMinorClass = cursor.getColumnIndexOrThrow("minor_class");
    final int minorClass = cursor.getInt(colIdMinorClass);
    
    int classIconId = BluetoothClassLookup.getIconId(majorClass, minorClass);
    
    ImageView ivClass = (ImageView) view.findViewById(R.id.device_row_class);
    ivClass.setImageResource(classIconId);
    
    TextView tvMac = (TextView) view.findViewById(R.id.device_row_mac);
    int idMac = cursor.getColumnIndexOrThrow("mac_address");
    tvMac.setText(cursor.getString(idMac));
    
    TextView tvName = (TextView) view.findViewById(R.id.device_row_name);
    int idName = cursor.getColumnIndexOrThrow("name");
    tvName.setText(cursor.getString(idName));
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent)
  {
    LayoutInflater inflater = LayoutInflater.from(context);
    final View view = inflater.inflate(R.layout.device_list_row, parent,
                                       false);
    
    return view;
  }
}
