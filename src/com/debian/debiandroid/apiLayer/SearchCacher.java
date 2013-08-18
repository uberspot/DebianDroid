package com.debian.debiandroid.apiLayer;

import android.net.Uri;

/** Class that stores temporarily user searches 
 * so that different fragments can access them */
public class SearchCacher {
	 
	private static String lastPckgName = null;
	private static String lastBugSearchValue = null;
	private static String lastBugSearchOption = null;
	
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
		lastPckgName = uri.getQueryParameter("package");
		lastBugSearchValue = uri.getQueryParameter("bug");
		lastBugSearchOption = BTS.PACKAGE;
	}
	
	public static void setLastSearchByPTSURI(Uri uri) {
		setLastSearchByPckgName(uri.getLastPathSegment().replace(".html", ""));
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
}
