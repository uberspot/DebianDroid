package com.debian.debiandroid.apiLayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import androidStorageUtils.Cacher;

public class UDDCaller {
	
	protected Cacher cacher;
	
	public UDDCaller(Context context) {
		cacher = new Cacher(context);
	}
		
	private static final String UDD_CGI_URL = "http://udd.debian.org/cgi-bin/";
	
	public String getOrphanedPackages() {
		return doQueryRequest("bapase.cgi?t=o");
	}
	
	public String getLastUploads() {
		return doQueryRequest("last-uploads.cgi");
	}
	
	public String getNewMaintainers() {
		return doQueryRequest("new-maintainers.cgi");
	}

	public String doQueryRequest(String queryURL) {
		// if (fresh) cached string exists then return it, otherwise 
        // continue with the normal retrieval
		String cached = cacher.getCachedString(queryURL);
        String cachedString = "";
        if(cached!=null) {
        	cachedString = cached;
        }
		HttpURLConnection urlConnection = null;
		StringBuilder htmlPage = new StringBuilder();
		try {
			URL url = new URL(UDD_CGI_URL + queryURL);
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
				cacher.cacheString(queryURL, htmlPage.toString());
				return htmlPage.toString();
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		//if any errors occured return the cached string (or "" if no cached version exists)
		return cachedString;
	}
}
