package com.debian.debiandroid.apiLayer;

import android.content.Context;

public class UDDCaller extends HTTPCaller {
	
	public UDDCaller(Context context) {
		super(context);
	}
		
	private static final String UDD_CGI_URL = "http://udd.debian.org/cgi-bin/";
	
	public String getLastUploadsCSV() {
		return doQueryRequest(UDD_CGI_URL + "last-uploads.cgi?out=csv");
	}
	
	public String getRCBugsCSV() {
		return doQueryRequest(UDD_CGI_URL + "rcbugs.cgi?out=csv");
	}
	
	public String getNewMaintainersCSV() {
		return doQueryRequest(UDD_CGI_URL + "new-maintainers.cgi?out=csv");
	}
	
	public String getOverlappingInterests(String devamail, String devbmail) {
		return doQueryRequest(UDD_CGI_URL + "overlapping_interests.cgi?deva="+devamail+"&devb="+devbmail);
	}
}
