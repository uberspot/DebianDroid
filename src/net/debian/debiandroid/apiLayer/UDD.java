package net.debian.debiandroid.apiLayer;

import java.util.ArrayList;

import android.content.Context;

public class UDD extends HTTPCaller {
	
	public UDD(Context context) {
		super(context);
	}
		
	private static final String UDD_CGI_URL = "http://udd.debian.org/cgi-bin/";
	
	public ArrayList<ArrayList<String>> getLastUploads() {
		String[] response = doQueryRequest(UDD_CGI_URL + "last-uploads.cgi?out=csv").split("\n");
		ArrayList<String> description = new ArrayList<String>();
		ArrayList<String> fullDesc = new ArrayList<String>();
		for(String element: response) {
			element = element.trim();
			int lastCommaPos = element.lastIndexOf(',');
			if(lastCommaPos!=-1 && lastCommaPos+1<element.length()) {
				description.add(element.substring(lastCommaPos+1)); 
				fullDesc.add(element.replaceAll(",", "\n"));
			}
		}
		ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
		items.add(description);
		items.add(fullDesc);
		return items;
	}
	
	public ArrayList<ArrayList<String>> getRCBugs() {
		String[] response = doQueryRequest(UDD_CGI_URL + "rcbugs.cgi?out=csv").split("\n");
		ArrayList<String> description = new ArrayList<String>();
		ArrayList<String> fullDesc = new ArrayList<String>();
		for(String element: response) {
			element = element.trim();
			if(element.charAt(0)!='#') {
				String[] details = element.split(",");
				if(details.length>2) {
					description.add(details[0] + " " + details[1]);
					fullDesc.add(element.replaceAll(",", "\n"));
				}
			}
		}
		ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
		items.add(description);
		items.add(fullDesc);
		return items;
	}
	
	public ArrayList<ArrayList<String>> getNewMaintainers() {
		String[] response = doQueryRequest(UDD_CGI_URL + "new-maintainers.cgi?out=csv").split("\n");
		ArrayList<String> description = new ArrayList<String>();
		ArrayList<String> fullDesc = new ArrayList<String>();
		for(String element: response) {
				element = element.trim();
				String[] details = element.split(",");
				if(details.length>2) {
					description.add(details[1]);
					fullDesc.add(element.replaceAll(",", "\n"));
				}
		}
		ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
		items.add(description);
		items.add(fullDesc);
		return items;
	}
	
	public String[] getOverlappingInterests(String devamail, String devbmail) {
		return doQueryRequest(UDD_CGI_URL + 
						"overlapping_interests.cgi?deva="+devamail+"&devb="+devbmail).trim().replaceAll(",", " ")
						.split("\n");
	}
	
	public static String getBugNumFromRCBugTitle(String title) {
		if(title!=null && title.length()!=0)
			return title.split(" ")[0];
		return title;
	}
	
	public static String getPckgNameFromRCBugTitle(String title) {
		if(title!=null && title.length()!=0 && title.contains(" "))
			return title.split(" ")[1];
		return title;
	}
	
	public static String getPckgNameFromUploadsTitle(String title) {
		if(title!=null && title.length()!=0)
			return title.split(" ")[0];
		return title;
	}
}
