package org.jonblack.bluetrack;

import android.provider.BaseColumns;

public class Device implements BaseColumns
{
  /**
   * Device mac address
   */
  private String mMacAddress;
  
  /**
   * Device name
   */
  private String mName;
  
  /**
   * @return The device's mac address.
   */
  public String getMacAddress()
  {
    return mMacAddress;
  }
  
  /**
   * @return The devices name.
   */
  public String getName()
  {
    return mName;
  }
}
