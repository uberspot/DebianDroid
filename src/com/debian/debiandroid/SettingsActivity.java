package com.debian.debiandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
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
}
