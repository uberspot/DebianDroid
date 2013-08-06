package com.debian.debiandroid.apiLayer;

import android.net.Uri;

/** Class that stores temporarily user searches 
 * so that different fragments can access them */
public class SearchCacher {
	 
	private static String lastPckgName = null;
	private static String lastBugNumber = null;
	
	/** @return the name of the package last searched */
	public static String getLastPckgName() {
		return lastPckgName;
	}
	
	public static void setLastSearchByPckgName(String lastPckgName) {
		SearchCacher.lastPckgName = lastPckgName;
	}
	
	/** @return the name of the package last searched */
	public static String getLastBugNumber() {
		return lastBugNumber;
	}
	
	public static void setLastSearchByBugNumber(String lastBugNumber) {
		SearchCacher.lastBugNumber = lastBugNumber;
	}
	
	public static void setLastSearchByBTSURI(Uri uri) {
		lastPckgName = uri.getQueryParameter("package");
		lastBugNumber = uri.getQueryParameter("bug");
		System.out.println("lastPckgName: " + lastPckgName + " lastBugNumber: " + lastBugNumber);
	}
	
	public static void setLastSearchByPTSURI(Uri uri) {
		lastPckgName = uri.getLastPathSegment().replace(".html", "");
		System.out.println("lastPckgName: " + lastPckgName);
	}
	
	public static boolean hasLastSearch() {
		return lastPckgName!=null && lastBugNumber!=null;
	}
}
