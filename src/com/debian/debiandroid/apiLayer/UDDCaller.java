package com.debian.debiandroid.apiLayer;

import android.content.Context;

public class UDDCaller extends HTTPCaller {
	
	public UDDCaller(Context context) {
		super(context);
	}
		
	private static final String UDD_CGI_URL = "http://udd.debian.org/cgi-bin/";
	
	public String getLastUploads() {
		return doQueryRequest(UDD_CGI_URL + "last-uploads.cgi");
	}
	
	public String getNewMaintainers() {
		return doQueryRequest(UDD_CGI_URL + "new-maintainers.cgi");
	}
}
