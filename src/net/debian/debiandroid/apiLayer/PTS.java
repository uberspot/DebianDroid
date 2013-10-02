package net.debian.debiandroid.apiLayer;

import java.util.HashSet;
import java.util.Set;

import net.debian.debiandroid.apiLayer.soaptools.PTSSoapCaller;


import android.content.Context;
import android.net.Uri;
import androidStorageUtils.StorageUtils;

public class PTS extends PTSSoapCaller implements Subscribable {

	private StorageUtils ptsStorage;
	
	public static final String PTSSUBSCRIPTIONS = "PTSSubscriptions";
	
	public PTS(Context context) {
		super(context);
		ptsStorage = StorageUtils.getInstance(context);
	}

	public boolean isSubscribedTo(String subcriptionID) {
		return ptsStorage.getPreferenceSet(PTSSUBSCRIPTIONS, new HashSet<String>()).contains(subcriptionID);
	}

	public boolean removeSubscriptionTo(String subcriptionID) {
		return ptsStorage.removePreferenceFromSet(PTSSUBSCRIPTIONS, subcriptionID);
	}

	public boolean addSubscriptionTo(String subcriptionID) {
		return ptsStorage.addPreferenceToSet(PTSSUBSCRIPTIONS, subcriptionID);
	}
	
	public Set<String> getSubscriptions() {
		return ptsStorage.getPreferenceSet(PTSSUBSCRIPTIONS, new HashSet<String>());
	}
	
	public static boolean isPTSHost(String host) {
		return host.equalsIgnoreCase("packages.qa.debian.org");
	}
	
	public static String PTSURIToPckgName(Uri uri) {
		return uri.getLastPathSegment().replace(".html", "");
	}
}
