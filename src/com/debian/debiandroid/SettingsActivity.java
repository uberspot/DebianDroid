package com.debian.debiandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class SettingsActivity extends SherlockPreferenceActivity {
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
			} else {
				addPreferencesFromResource(R.xml.preferences);
			}
		}
		
		public static class PreferencesFragment extends PreferenceFragment
	    {
	        @Override
	        public void onCreate(final Bundle savedInstanceState)
	        {
	            super.onCreate(savedInstanceState);
	            addPreferencesFromResource(R.xml.preferences);
	        }
	    }
		
		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	                NavUtils.navigateUpTo(this, new Intent(this, ItemListActivity.class));
	                return true;
	       }
	        return super.onOptionsItemSelected(item);
	    }
		
		@Override
		public void onDestroy() {
			//reload preferences on exit from settings screen 
			loadSettings(getApplicationContext());
	    	super.onDestroy();
		}
		
		public static void loadSettings(Context context) {
			try {
				DDNotifyService.updateIntervalTime = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(
						context).getString("rinterval", "600"))*1000; // stored seconds -> milliseconds
			} catch(NumberFormatException e) { e.printStackTrace(); }
		}
}