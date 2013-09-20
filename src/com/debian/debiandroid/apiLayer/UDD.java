package com.debian.debiandroid.apiLayer;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;

public class UDD extends HTTPCaller {
	
	public UDD(Context context) {
		super(context);
	}
		
	private static final String UDD_CGI_URL = "http://udd.debian.org/cgi-bin/";
	
	public ArrayList<String> getLastUploads() {
		String[] response = doQueryRequest(UDD_CGI_URL + "last-uploads.cgi?out=csv").replaceAll(",", " ").split("\n");
		return new ArrayList<String>(Arrays.asList(response));
	}
	
	public ArrayList<String> getRCBugs() {
		String[] response = doQueryRequest(UDD_CGI_URL + "rcbugs.cgi?out=csv").replaceAll(",", " ").split("\n");
		return new ArrayList<String>(Arrays.asList(response));
	}
	
	public ArrayList<String> getNewMaintainers() {
		String[] response = doQueryRequest(UDD_CGI_URL + "new-maintainers.cgi?out=csv").replaceAll(",", " ").split("\n");
		return new ArrayList<String>(Arrays.asList(response));
	}
	
	public ArrayList<String> getOverlappingInterests(String devamail, String devbmail) {
		String[] response = doQueryRequest(UDD_CGI_URL + 
						"overlapping_interests.cgi?deva="+devamail+"&devb="+devbmail).replaceAll(",", " ")
						.split("\n");
		return new ArrayList<String>(Arrays.asList(response));
	}
}
