package org.jonblack.bluetrack.activities;

import org.jonblack.bluetrack.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	  private EditTextPreference mScanDelayPreference;
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	    super.onCreate(savedInstanceState);
	    
	    // Load the preference from xml
	    addPreferencesFromResource(R.xml.preferences);
	    
	    // Get references to preferences
	    mScanDelayPreference = (EditTextPreference) getPreferenceScreen().findPreference("pref_tracking_scan_delay");
	  }
	  
	  @Override
	  public void onResume()
	  {
	    super.onResume();
	    
	    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    // Set initial values by calling the preference change handler manually.
	    onSharedPreferenceChanged(sharedPrefs, "pref_tracking_scan_delay");
	    
	    // Register preference change listener
	    sharedPrefs.registerOnSharedPreferenceChangeListener(this);
	  }
	  
	  @Override
	  public void onPause()
	  {
	    super.onPause();
	    
	    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	    sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
	  }
	  
	  @Override
	  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	                                        String key)
	  {
	    // A preference value has changed. Some options show their value in the
	    // summary. If one of those options has changed, update the summary.
	    if (key.equals("pref_tracking_scan_delay"))
	    {
	      int scanDelay = Integer.parseInt(sharedPreferences.getString(key, ""));
	      String summary = getString(R.string.pref_tracking_scan_delay, scanDelay);
	      mScanDelayPreference.setSummary(summary);
	    }
	  }
}
