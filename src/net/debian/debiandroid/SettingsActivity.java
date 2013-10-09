package net.debian.debiandroid;

import net.debian.debiandroid.apiLayer.ApiTools;
import net.debian.debiandroid.utils.NetUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.widget.Toast;
import androidStorageUtils.Cacher;
import androidStorageUtils.StorageUtils;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import net.debian.debiandroid.R;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class SettingsActivity extends SherlockPreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// If in android 3+ use a preference fragment which is the new recommended way
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new PreferencesFragment())
					.commit();
		} else {
			// Otherwise load the preferences.xml in the Activity like in previous android versions
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

	/** Loads user settings to app. Called when settings change and users exits from 
	 *  settings screen or when the app first starts. 
	 *  */
	public static void loadSettings(Context context) {
		try {
			StorageUtils storage = StorageUtils.getInstance(context);
			
			DDNotifyService.updateIntervalTime = Integer
					.parseInt(storage.getPreference("rinterval", "600")) * 1000; // stored seconds -> milliseconds
			Cacher.setCacheLimitByHours(Integer.parseInt(storage.getPreference(
							"cachelimit", "48")));
			
			boolean hasMobileCon = NetUtils.hasNetProviderConnection(context, NetUtils.MOBILE);
			boolean mobileAllowed = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use3g", true);
			if(hasMobileCon && !mobileAllowed && ApiTools.isNetEnabled()) {
					ApiTools.disableUseOfNet();
			} else if(!ApiTools.isNetEnabled()) {
					ApiTools.enableUseOfNet();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void onPause() {
        super.onPause();
        DDNotifyService.activityPaused();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	DDNotifyService.activityResumed();
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
			Toast.makeText(pref.getContext(), pref.getContext().getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show();
			return true;
		}
	};
}