package com.debian.debiandroid.apiLayer;

import android.content.Context;
import androidStorageUtils.StorageUtils;

public class Cacher extends StorageUtils{

	public static final long defaultCacheLimit = 172800000 ; //48 hours in ms
	
	/** The time limit in milliseconds for which to keep a file cached in memory. */
	public static long cacheLimit = defaultCacheLimit ; //the default for now
	
	private boolean enabledCache;
	
	public Cacher(Context base) {
		super(base);
		enabledCache = true;
	}

	/** Retrieves cached string from the given filename. 
	 * If the timestamp of the cached string is > from the cacheLimit or 
	 * if the cached file doesn't exist or if caching is disabled
	 * then null is returned. Otherwise it returns the string.
	 * @param fileName the name of the cached file to retrieve
	 * @return the cached string if it exists, if it's fresher than 
	 * the cacheLimit time and if the cache is enabled (by default it is),
	 * otherwise it returns null
	 */
	public String getCachedString(String fileName) {
		if(!enabledCache)
			return null;
    	Object obj = loadObjectFromInternalStorage(fileName);
    	if(obj!=null) {
    		String string = obj.toString();
        	int firstSpace = string.indexOf(' ');
        	try {
		        long timeFromLastCache = System.currentTimeMillis() - 
		        				Long.parseLong(string.substring(0, firstSpace));
		        if(timeFromLastCache <= cacheLimit) {
		        	return string.substring(firstSpace);
		    	}
	        } catch (NumberFormatException e) { e.printStackTrace(); }
        }
        return null;
	}

	/** Caches given string to given filename in internal memory. Also prepends the current timestamp
	 * in the file for later usage.
	 * @param fileName the name of the file in the internal memory to cache into.
	 * @param string
	 */
	public void cacheString(String fileName, String string) {
		if(enabledCache)
			saveObjectToInternalStorage( System.currentTimeMillis() + " " + string, fileName);
	}
	
	public void enableCache(){ enabledCache = true; }
	
	public void disableCache(){ enabledCache = false; }

	public void disableCacheTimeLimit() { cacheLimit = Long.MAX_VALUE; }
	
	public void enableCacheTimeLimit() { cacheLimit = defaultCacheLimit; }

}
