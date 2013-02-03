package org.jonblack.bluetrack;

import android.bluetooth.BluetoothClass;
import android.util.Log;

public class BluetoothClassLookup
{
  private static final String TAG = "BluetoothClassLookup";
  
  /**
   * Returns the drawable to the icon that represents the bluetooth class id.
   * @param classId
   * @return
   */
  public static int getMajorIconId(Integer classId)
  {
    int classIconId = -1;
    switch (classId)
    {
    case BluetoothClass.Device.Major.AUDIO_VIDEO:
      classIconId = R.drawable.bt_audio_video;
      break;
    case BluetoothClass.Device.Major.COMPUTER:
      classIconId = R.drawable.bt_computer;
      break;
    case BluetoothClass.Device.Major.HEALTH:
      classIconId = R.drawable.bt_health;
      break;
    case BluetoothClass.Device.Major.IMAGING:
      classIconId = R.drawable.bt_imaging;
      break;
    case BluetoothClass.Device.Major.MISC:
      classIconId = R.drawable.bt_misc;
      break;
    case BluetoothClass.Device.Major.NETWORKING:
      classIconId = R.drawable.bt_networking;
      break;
    case BluetoothClass.Device.Major.PERIPHERAL:
      classIconId = R.drawable.bt_peripheral;
      break;
    case BluetoothClass.Device.Major.PHONE:
      classIconId = R.drawable.bt_phone;
      break;
    case BluetoothClass.Device.Major.TOY:
      classIconId = R.drawable.bt_toy;
      break;
    case BluetoothClass.Device.Major.UNCATEGORIZED:
      classIconId = R.drawable.bt_uncategorized;
      break;
    case BluetoothClass.Device.Major.WEARABLE:
      classIconId = R.drawable.bt_wearable;
      break;
    default:
      // Class is not recognised. Use the same icon as for uncategorized, and
      // log a warning.
      Log.w(TAG, String.format("Icon for bluetooth class '%d' was not found."));
      classIconId = R.drawable.bt_uncategorized;
    }
    
    return classIconId;
  }
}
