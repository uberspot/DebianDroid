package com.debian.debiandroid.apiLayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UDDCaller {
		
	private static final String UDD_CGI_URL = "http://udd.debian.org/cgi-bin/";
	
	public String getOrphanedPackages() {
		return doQueryRequest(UDD_CGI_URL + "bapase.cgi?t=o");
	}
	
	public String getLastUploads() {
		return doQueryRequest(UDD_CGI_URL + "last-uploads.cgi");
	}
	
	public String getNewMaintainers() {
		return doQueryRequest(UDD_CGI_URL + "new-maintainers.cgi");
	}

	public String doQueryRequest(String queryURL) {
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
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return htmlPage.toString();
	}
}
