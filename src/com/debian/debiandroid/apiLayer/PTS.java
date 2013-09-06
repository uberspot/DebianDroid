package com.debian.debiandroid.apiLayer;

import java.util.Set;

import com.debian.debiandroid.apiLayer.soaptools.PTSSoapCaller;

import android.content.Context;
import android.net.Uri;
import androidStorageUtils.StorageUtils;

public class PTS extends PTSSoapCaller{

	private StorageUtils ptsStorage;
	
	public static final String PTSSUBSCRIPTIONS = "PTSSubscriptions";
	
	public PTS(Context context) {
		super(context);
		ptsStorage = StorageUtils.getInstance(context);
	}

	public boolean isSubscribedTo(String pckgName) {
		return ptsStorage.getPreferenceSet(PTSSUBSCRIPTIONS).contains(pckgName);
	}

	public boolean removeSubscriptionTo(String pckgName) {
		return ptsStorage.removePreferenceFromSet(PTSSUBSCRIPTIONS, pckgName);
	}

	public boolean addSubscriptionTo(String pckgName) {
		return ptsStorage.addPreferenceToSet(PTSSUBSCRIPTIONS, pckgName);
	}
	
	public Set<String> getSubscriptions() {
		return ptsStorage.getPreferenceSet(PTSSUBSCRIPTIONS);
	}
	
	public static boolean isPTSHost(String host) {
		return host.equalsIgnoreCase("packages.qa.debian.org");
	}
	
	public static String PTSURIToPckgName(Uri uri) {
		return uri.getLastPathSegment().replace(".html", "");
	}
}
