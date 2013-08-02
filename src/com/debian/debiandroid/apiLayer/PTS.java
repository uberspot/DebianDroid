package com.debian.debiandroid.apiLayer;

import android.content.Context;
import androidStorageUtils.StorageUtils;

public class PTS {

	private StorageUtils ptsStorage;
	
	public static final String PTSSUBSCRIPTIONS = "PTSSubscriptions";
	
	public PTS(Context context) {
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
}
