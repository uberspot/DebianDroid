package com.debian.debiandroid.utils;

import com.debian.debiandroid.apiLayer.BTS;

import android.net.Uri;

/** Class that stores temporarily user searches 
 * so that fragments can access them on reload/screen orientation change
 * of so that different fragments can access them as well without passing 
 * everything like a parameter. */
public class SearchCacher {
	 
	private static String lastPckgName = null;
	private static String lastBugSearchValue = null;
	private static String lastBugSearchOption = null;
	private static String lastPckgVersion = null;
	
	/** @return the name of the package last searched */
	public static String getLastPckgName() {
		return lastPckgName;
	}
	
	public static void setLastSearchByPckgName(String lastPckgName) {
		SearchCacher.lastPckgName = lastPckgName;
		SearchCacher.lastBugSearchOption = BTS.PACKAGE;
		SearchCacher.lastBugSearchValue = lastPckgName;
	}
	
	public static void setLastSearchByBTSURI(Uri uri) {
		try {
			String pckgName = BTS.BTSURIToPckgName(uri);
			String bugnumber = BTS.BTSURIToBugNum(uri);
			if(pckgName!=null)
				setLastSearchByPckgName(pckgName);
			else if(bugnumber!=null) {
				SearchCacher.setLastBugSearch(BTS.BUGNUMBER, bugnumber);
			}
		} catch(UnsupportedOperationException e) { e.printStackTrace(); }
	}
	
	public static boolean hasLastPckgSearch() {
		return lastPckgName!=null;
	}

	public static String getLastBugSearchValue() {
		return lastBugSearchValue;
	}

	public static void setLastBugSearch(String searchOption, String searchValue) {
		SearchCacher.lastBugSearchValue = searchValue;
		SearchCacher.lastBugSearchOption = searchOption;
		if(searchOption.equals(BTS.PACKAGE)) {
			lastPckgName = searchValue;
		}
	}

	public static String getLastBugSearchOption() {
		return lastBugSearchOption;
	}

	public static boolean hasLastBugsSearch() {
		return lastBugSearchOption!=null && lastBugSearchValue!=null;
	}

	public static boolean hasAnyLastSearch() {
		return hasLastBugsSearch() || hasLastPckgSearch();
	}

	public static String getLastPckgVersion() {
		return lastPckgVersion;
	}

	public static void setLastPckgVersion(String lastPckgVersion) {
		SearchCacher.lastPckgVersion = lastPckgVersion;
	}
}
