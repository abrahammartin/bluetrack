package org.jonblack.bluetrack.adapters;

import org.jonblack.bluetrack.BluetoothClassLookup;
import org.jonblack.bluetrack.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LiveTrackingCursorAdapter extends CursorAdapter
{
  public LiveTrackingCursorAdapter(Context context, Cursor cursor, int flags)
  {
    super(context, cursor, flags);
  }
  
  @Override
  public void bindView(View view, Context context, Cursor cursor)
  {
    ImageView ivClass = (ImageView) view.findViewById(R.id.live_tracking_row_class);
    int idClass = cursor.getColumnIndexOrThrow("major_class");
    final int majorClass = cursor.getInt(idClass);
    
    int classIconId = BluetoothClassLookup.getMajorIconId(majorClass);
    ivClass.setImageResource(classIconId);
    
    TextView tvMac = (TextView) view.findViewById(R.id.live_tracking_row_mac);
    int idMac = cursor.getColumnIndexOrThrow("mac_address");
    tvMac.setText(cursor.getString(idMac));
    
    TextView tvName = (TextView) view.findViewById(R.id.live_tracking_row_name);
    int idName = cursor.getColumnIndexOrThrow("name");
    tvName.setText(cursor.getString(idName));
    
    TextView tvRssi = (TextView) view.findViewById(R.id.live_tracking_signal_strength);
    int idRssi = cursor.getColumnIndexOrThrow("rssi");
    tvRssi.setText(cursor.getString(idRssi));
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent)
  {
    LayoutInflater inflater = LayoutInflater.from(context);
    final View view = inflater.inflate(R.layout.live_tracking_list_row, parent,
                                       false);
    
    return view;
  }
}
