package com.debian.debiandroid.apiLayer;

import android.content.Context;
import androidStorageUtils.StorageUtils;

public class BTS {

	private StorageUtils btsStorage;

	public static final String BTSSUBSCRIPTIONS = "BTSSubscriptions";

	public BTS(Context context) {
		btsStorage = StorageUtils.getInstance(context);
	}

	public boolean isSubscribedTo(String bugNumber) {
		return btsStorage.getPreferenceSet(BTSSUBSCRIPTIONS).contains(bugNumber);
	}

	public boolean removeSubscriptionTo(String bugNumber) {
		return btsStorage.removePreferenceFromSet(BTSSUBSCRIPTIONS, bugNumber);
	}

	public boolean addSubscriptionTo(String bugNumber) {
		return btsStorage.addPreferenceToSet(BTSSUBSCRIPTIONS, bugNumber);
	}
}
