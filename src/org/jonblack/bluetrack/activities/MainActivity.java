package org.jonblack.bluetrack.activities;


import org.jonblack.bluetrack.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends Activity
{
  private static final String TAG = "MainActivity";
  
  /**
   * Request code for handling calls to enable bluetooth."
   */
  private static final int REQUEST_ENABLE_BT = 1;
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);
    
    // Get the local bluetooth adapter and check if bluetooth is supported.
    BluetoothAdapter localBtAdapter = BluetoothAdapter.getDefaultAdapter();
    if (localBtAdapter == null)
    {
      Log.w(TAG, "Bluetooth isn't supported on device.");
      
      AlertDialog ad = new AlertDialog.Builder(this).create();
      ad.setCancelable(false);
      Resources r = getResources();
      String msg = r.getString(R.string.error_bluetooth_not_supported,
                               r.getString(R.string.app_name));
      ad.setMessage(msg);
      ad.setButton(AlertDialog.BUTTON_POSITIVE,
                   r.getString(R.string.button_exit),
                   new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
          finish();
          dialog.dismiss();
        }
      });
      ad.show();
      
      return;
    }
    
    // If bluetooth is disabled, ask the user to enable it. Otherwise, start
    // the BluetoothLogService.
    if (!localBtAdapter.isEnabled())
    {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
    
    // Configure the ActionBar tabs
    if (savedInstanceState == null)
    {
      Resources r = getResources();
      
      ActionBar actionBar = getActionBar();
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
      
      ActionBar.Tab tab1 = actionBar.newTab().setText(r.getText(R.string.ab_tab_live_tracking));
      tab1.setTabListener(new TabListener(new LiveTrackingFragment()));
      actionBar.addTab(tab1);
      
      ActionBar.Tab tab2 = actionBar.newTab().setText(r.getText(R.string.ab_tab_sessions));
      tab2.setTabListener(new TabListener(new SessionFragment()));
      actionBar.addTab(tab2);
    }
  }
  
  @Override
  protected void onDestroy()
  {
    super.onDestroy();
  }
  
  /**
   * @see android.app.Activity.onActivityResult
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    // Handle result for starting "enable bluetooth" intent.
    if (requestCode == REQUEST_ENABLE_BT)
    {
      if (resultCode == RESULT_OK)
      {
        Log.d(TAG, "Bluetooth enabled.");
      }
      else
      {
        Log.d(TAG, "Bluetooth not enabled.");
      }
    }
  }
  
  private class TabListener implements ActionBar.TabListener
  {
    private Fragment mFragment;
    
    public TabListener(Fragment fragment)
    {
      mFragment = fragment;
    }
    
    @Override
    public void onTabReselected(Tab tab, android.app.FragmentTransaction unused)
    {
    }
    
    @Override
    public void onTabSelected(Tab tab, android.app.FragmentTransaction unused)
    {
      Log.i(TAG, String.format("'%s' tab selected.", tab.getText()));
      
      FragmentManager fm = getFragmentManager();
      FragmentTransaction ft = fm.beginTransaction();
      ft.replace(R.id.tab_fragment_container, mFragment);
      ft.commit();
    }
    
    @Override
    public void onTabUnselected(Tab tab, android.app.FragmentTransaction unused)
    {
    }
  }
}
