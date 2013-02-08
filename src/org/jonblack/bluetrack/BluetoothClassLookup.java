package org.jonblack.bluetrack;

import android.bluetooth.BluetoothClass;
import android.util.Log;

public class BluetoothClassLookup
{
  private static final String TAG = "BluetoothClassLookup";
  
  /**
   * Id when no icon exists for the bluetooth class.
   */
  private static final int INVALID_CLASS_ICON = -1;
  
  /**
   * Returns the drawable to the icon that represents the bluetooth class id.
   * 
   * First checks the minor class id, and if no icon is available, then checks
   * the major class id.
   * 
   * A valid icon id is always returned from this function.
   */
  public static int getIconId(int majorClassId, int minorClassId)
  {
    int classIconId = INVALID_CLASS_ICON;
    
    classIconId = getMinorIconId(minorClassId);
    if (classIconId == INVALID_CLASS_ICON)
    {
      classIconId = getMajorIconId(majorClassId);
    }
    assert(classIconId != INVALID_CLASS_ICON);
    
    return classIconId;
  }
  
  private static int getMajorIconId(int majorClassId)
  {
    switch (majorClassId)
    {
    case BluetoothClass.Device.Major.AUDIO_VIDEO:
      return R.drawable.bt_audio_video;
    case BluetoothClass.Device.Major.COMPUTER:
      return R.drawable.bt_computer;
    case BluetoothClass.Device.Major.HEALTH:
      return R.drawable.bt_health;
    case BluetoothClass.Device.Major.IMAGING:
      return R.drawable.bt_imaging;
    case BluetoothClass.Device.Major.MISC:
      return R.drawable.bt_misc;
    case BluetoothClass.Device.Major.NETWORKING:
      return R.drawable.bt_networking;
    case BluetoothClass.Device.Major.PERIPHERAL:
      return R.drawable.bt_peripheral;
    case BluetoothClass.Device.Major.PHONE:
      return R.drawable.bt_phone;
    case BluetoothClass.Device.Major.TOY:
      return R.drawable.bt_toy;
    case BluetoothClass.Device.Major.UNCATEGORIZED:
      return R.drawable.bt_uncategorized;
    case BluetoothClass.Device.Major.WEARABLE:
      return R.drawable.bt_wearable;
    default:
      // Class is not recognised. Use the same icon as for uncategorized, and
      // log a warning.
      Log.w(TAG, String.format("Icon for major bluetooth class '%d' was not found.",
                 majorClassId));
      return R.drawable.bt_uncategorized;
    }
  }
  
  private static int getMinorIconId(int minorClassId)
  {
    switch (minorClassId)
    {
    case BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER:
      return R.drawable.bt_audio_video_camcorder;
    case BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO:
      return R.drawable.bt_audio_video_hifi_audio;
    case BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE:
      return R.drawable.bt_audio_video_microphone;
    case BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO:
      return R.drawable.bt_audio_video_portable_audio;
    case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CONFERENCING:
      return R.drawable.bt_audio_video_video_conferencing;
    case BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA:
      return R.drawable.bt_computer_handheld_pc_pda;
    case BluetoothClass.Device.COMPUTER_SERVER:
      return R.drawable.bt_computer_server;
    case BluetoothClass.Device.WEARABLE_HELMET:
      return R.drawable.bt_wearable_helmet;
    default:
      // Class is not recognised. Use the same icon as for uncategorized, and
      // log a warning.
      Log.w(TAG, String.format("Icon for minor bluetooth class '%d' was not found.",
                 minorClassId));
      return INVALID_CLASS_ICON;
    }
  }
}
