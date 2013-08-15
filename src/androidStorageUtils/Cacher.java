package androidStorageUtils;

import android.content.Context;

/***
	Example method caching the result
	//In constructor
	Cacher cacher = new Cacher(context); 
	//In method
	public void retrieveResult() {
			// if (fresh) cached string exists then return it, otherwise 
		    // continue with the normal retrieval
		    String cached = cacher.getCachedString(reqFileName);
		    if(cached!=null && 
		    		cacher.getTimeFromLastCache(reqFileName) <= Cacher.cacheLimit) {
		    		return cached;
		    }
		    try {
			    //Do normal retrieval and cache the result of that...
			    cacher.cacheString(reqFileName, response.toString());
			    return response.toString();
		    } catch(Exception e) { }
		    //if any errors occured return the cached string (or "" if no cached version exists)
        	return (cached!=null)?cached:"";
	}
 */
public class Cacher extends StorageUtils{

	public static final long defaultCacheLimit = 172800000 ; //48 hours in ms
	
	/** The time limit in milliseconds for which to keep a file cached in memory. */
	public static long cacheLimit = defaultCacheLimit ; //the default for now
	
	private boolean enabledCache;
	
	private static final String cacheExtension = ".cache";
	
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
    	Object obj = loadObjectFromInternalStorage(fileName + cacheExtension);
    	if(obj!=null) {
    		String string = obj.toString();
        	int firstSpace = string.indexOf(' ');
		        return string.substring(firstSpace);
        }
        return null;
	}
	
	public long getTimeFromLastCache(String fileName) {
		Object obj = loadObjectFromInternalStorage(fileName + cacheExtension);
    	if(obj!=null) {
    		String string = obj.toString();
        	int firstSpace = string.indexOf(' ');
        	try {
		        return System.currentTimeMillis() - 
		        				Long.parseLong(string.substring(0, firstSpace));
	        } catch (NumberFormatException e) { e.printStackTrace(); }
        }
        return Long.MAX_VALUE;
	}

	/** Caches given string to given filename in internal memory. Also prepends the current timestamp
	 * in the file for later usage.
	 * @param fileName the name of the file in the internal memory to cache into.
	 * @param string
	 */
	public void cacheString(String fileName, String string) {
		if(enabledCache)
			saveObjectToInternalStorage(System.currentTimeMillis() + " " + string, fileName + cacheExtension);
	}
	
	public void enableCache(){ enabledCache = true; }
	
	public void disableCache(){ enabledCache = false; }

	public static void disableCacheTimeLimit() { cacheLimit = Long.MAX_VALUE; }
	
	public static void enableCacheTimeLimit() { cacheLimit = defaultCacheLimit; }
	
	public static void setCacheLimitByHours(int hours) { cacheLimit = hours*60*60*1000; }
	
	public void clearCache() { 
		String[] files = fileList();
		for(String file: files) {
			if(file.endsWith(cacheExtension)){
				deleteFile(file);
			}
		}
	}
}
