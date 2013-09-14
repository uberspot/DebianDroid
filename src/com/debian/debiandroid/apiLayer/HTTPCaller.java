package com.debian.debiandroid.apiLayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import androidStorageUtils.Cacher;

public class HTTPCaller {

	protected Cacher cacher;
	public static boolean netEnabled = true;
	
	public HTTPCaller(Context context) {
		cacher = new Cacher(context);
	}
	
	public String doQueryRequest(String queryURL) {
		// if (fresh) cached string exists then return it, otherwise 
        // continue with the normal retrieval
		String fileName = queryURL.substring(queryURL.lastIndexOf("/")).replace("/", "");
		System.out.println(fileName);
		String cached = cacher.getCachedString(fileName);
        if(cached!=null && 
        		(!netEnabled || cacher.getTimeFromLastCache(fileName) <= Cacher.cacheLimit) ) {
        	System.out.println("returning: " + cached);
        	return cached;
        }
        if(netEnabled) {
			HttpURLConnection urlConnection = null;
			StringBuilder htmlPage = new StringBuilder();
			try {
				URL url = new URL(queryURL);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.connect();
	
				if (urlConnection.getResponseCode() == 200) {
					// Retrieve html page
					BufferedReader in = new BufferedReader(new InputStreamReader(
							urlConnection.getInputStream(), "UTF-8"), 20000);
					String inputLine;
	
					while ((inputLine = in.readLine()) != null) {
						htmlPage.append(inputLine);
						htmlPage.append("\n");
					}
					cacher.cacheString(fileName, htmlPage.toString());
					return htmlPage.toString();
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
			} finally {
				if (urlConnection != null)
					urlConnection.disconnect();
			}
        }
		//if any errors occured return the cached string (or "" if no cached version exists)
		return (cached!=null)?cached:"";
	}
}
