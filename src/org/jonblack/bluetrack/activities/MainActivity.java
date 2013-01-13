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
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends Activity
{
  private static final String TAG = "MainActivity";
  
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
    
    Resources r = getResources();
    
    ActionBar actionBar = getActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    
    ActionBar.Tab tab1 = actionBar.newTab().setText(r.getText(R.string.ab_tab_live_tracking));
    tab1.setTag("tracking");
    tab1.setTabListener(new TabListener(new LiveTrackingFragment(),
                        "tracking"));
    actionBar.addTab(tab1);
    
    ActionBar.Tab tab2 = actionBar.newTab().setText(r.getText(R.string.ab_tab_sessions));
    tab2.setTag("session");
    tab2.setTabListener(new TabListener(new SessionFragment(),
                        "session"));
    actionBar.addTab(tab2);
    
    ActionBar.Tab tab3 = actionBar.newTab().setText(r.getText(R.string.ab_tab_devices));
    tab3.setTag("devices");
    tab3.setTabListener(new TabListener(new DevicesFragment(),
                        "devices"));
    actionBar.addTab(tab3);
    
    if (savedInstanceState != null)
    {
      // Restore last state for checked position.
      int selectedTabIdx = savedInstanceState.getInt("selectedTabIdx", 0);
      actionBar.setSelectedNavigationItem(selectedTabIdx);
    }
  }
  
  @Override
  protected void onDestroy()
  {
    super.onDestroy();
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    
    ActionBar actionBar = getActionBar();
    outState.putInt("selectedTabIdx", actionBar.getSelectedNavigationIndex());
  }
  
  private class TabListener implements ActionBar.TabListener
  {
    private Fragment mFragment;
    private String mTag;
    
    public TabListener(Fragment fragment, String tag)
    {
      mFragment = fragment;
      mTag = tag;
    }
    
    @Override
    public void onTabReselected(Tab tab, android.app.FragmentTransaction ft)
    {
    }
    
    @Override
    public void onTabSelected(Tab tab, android.app.FragmentTransaction ft)
    {
      Log.i(TAG, String.format("'%s' tab selected.", tab.getText()));
      
      if (!mFragment.isAdded())
      {
        ft.add(R.id.tab_fragment_container, mFragment, (String) tab.getTag());
        Log.d(TAG, "Adding fragment " + mTag);
      }
      else
      {
        Log.d(TAG, "Attaching fragment " + mTag);
        ft.show(mFragment);
      }
    }
    
    @Override
    public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft)
    {
      Log.i(TAG, String.format("'%s' tab unselected.", tab.getText()));
      
      String tag = (String) tab.getTag();
      FragmentManager fm = getFragmentManager();
      Fragment fragment = fm.findFragmentByTag(tag);
      assert(fragment != null);
      ft.hide(fragment);
    }
  }
}
