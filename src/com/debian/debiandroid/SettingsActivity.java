package com.debian.debiandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import androidStorageUtils.Cacher;

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
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new PreferencesFragment())
					.commit();
		} else {
			addPreferencesFromResource(R.xml.preferences);
			findPreference("clearcache").setOnPreferenceClickListener(clearCacheListener);
			findPreference("wupdateinterval").setOnPreferenceChangeListener(numberCheckListener);
			findPreference("cachelimit").setOnPreferenceChangeListener(numberCheckListener);
		}
	}

	public static class PreferencesFragment extends PreferenceFragment {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			findPreference("clearcache").setOnPreferenceClickListener(clearCacheListener);
			findPreference("wupdateinterval").setOnPreferenceChangeListener(numberCheckListener);
			findPreference("cachelimit").setOnPreferenceChangeListener(numberCheckListener);
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
		// reload preferences on exit from settings screen
		Context context = getApplicationContext();
		loadSettings(context);
		// restart widget update to get new interval setting
		DebianDroidWidgetProvider.stopWidgetUpdate(context);
		DebianDroidWidgetProvider.startWidgetUpdate(context);
		super.onDestroy();
	}

	public static void loadSettings(Context context) {
		try {
			DDNotifyService.updateIntervalTime = Integer
					.parseInt(PreferenceManager.getDefaultSharedPreferences(
							context).getString("rinterval", "600")) * 1000; // stored seconds -> milliseconds
			Cacher.setCacheLimitByHours(Integer.parseInt(PreferenceManager
					.getDefaultSharedPreferences(context).getString(
							"cachelimit", "48")));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	private static Preference.OnPreferenceChangeListener numberCheckListener = new OnPreferenceChangeListener() {
	    @Override
	    public boolean onPreferenceChange(Preference preference, Object newValue) {
	    	return !newValue.toString().equals("")  &&  newValue.toString().matches("\\d*");
	    }
	};
	
	private static Preference.OnPreferenceClickListener clearCacheListener = new Preference.OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference pref) {
			new Cacher(pref.getContext()).clearCache(); 
			return true;
		}
	};
}