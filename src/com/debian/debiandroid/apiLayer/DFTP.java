package com.debian.debiandroid.apiLayer;

import java.util.ArrayList;

import android.content.Context;

public class DFTP extends HTTPCaller {

	public DFTP(Context context) {
		super(context);
	}

	private static final String NEW_PACKAGES_URL = "http://ftp-master.debian.org/new.822";
	private static final String REMOVALS_URL = "http://ftp-master.debian.org/removals.822";
	private static final String DEFERRED_URL = "http://ftp-master.debian.org/deferred/status";
	
	public String[] getRawNewPackages() {
		return doQueryRequest(NEW_PACKAGES_URL).split("\n\n");
	}
	
	public String[] getRawRemovedPackages() {
		return doQueryRequest(REMOVALS_URL).split("\n\n");
	}
	
	public String[] getRawDeferredPackages() {
		return doQueryRequest(DEFERRED_URL).split("\n\n");
	}
	public ArrayList<ArrayList<String>> getNewPackages() {
		String[] raw = getRawNewPackages();
		ArrayList<ArrayList<String>> formatted = new ArrayList<ArrayList<String>>();
		ArrayList<String> descriptions = new ArrayList<String>();
		ArrayList<String> fullDesc = new ArrayList<String>();
		for(String rawInfo: raw) {
			String source = ApiTools.getSubstringIn(rawInfo, "Source:", "Binary:");
			String version = ApiTools.getSubstringIn(rawInfo, "Version:", "Architectures:");
			String distribution = ApiTools.getSubstringIn(rawInfo, "Distribution:", "Fingerprint:"); 
			descriptions.add(source.trim() + " " + version.trim() + " " + distribution.trim());
			fullDesc.add(rawInfo);
		}
		formatted.add(descriptions);
		formatted.add(fullDesc);
		return formatted;
	}
	
	public ArrayList<ArrayList<String>> getRemovedPackages() {
		String[] raw = getRawRemovedPackages();
		ArrayList<ArrayList<String>> formatted = new ArrayList<ArrayList<String>>();
		ArrayList<String> descriptions = new ArrayList<String>();
		ArrayList<String> fullDesc = new ArrayList<String>();
		for(String rawInfo: raw) {
			String binaries = ApiTools.getSubstringIn(rawInfo, "Binaries:", "Reason:");
			descriptions.add(binaries.replace("\\[(.*?)\\]", "").trim());
			fullDesc.add(rawInfo);
		}
		formatted.add(descriptions);
		formatted.add(fullDesc);
		return formatted;
	}
	
	public ArrayList<ArrayList<String>> getDeferredPackages() {
		String[] raw = getRawDeferredPackages();
		ArrayList<ArrayList<String>> formatted = new ArrayList<ArrayList<String>>();
		ArrayList<String> descriptions = new ArrayList<String>();
		ArrayList<String> fullDesc = new ArrayList<String>();
		for(String rawInfo: raw) {
			String source = ApiTools.getSubstringIn(rawInfo, "Source:", "Binary:");
			String version = ApiTools.getSubstringIn(rawInfo, "Version:", "Distribution:");
			descriptions.add(source.trim() + " " + version.trim());
			fullDesc.add(rawInfo);
		}
		formatted.add(descriptions);
		formatted.add(fullDesc);
		return formatted;
	}
}