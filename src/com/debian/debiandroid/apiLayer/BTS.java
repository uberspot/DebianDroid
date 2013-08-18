package com.debian.debiandroid.apiLayer;

import java.util.Set;

import com.debian.debiandroid.apiLayer.soaptools.BTSSoapCaller;

import android.content.Context;
import androidStorageUtils.StorageUtils;

public class BTS extends BTSSoapCaller{

	private StorageUtils btsStorage;

	public static final String BTSSUBSCRIPTIONS = "BTSSubscriptions";
	
	public BTS(Context context) {
		super(context);
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
	
	public Set<String> getSubscriptions() {
		return btsStorage.getPreferenceSet(BTSSUBSCRIPTIONS);
	}
	
	public static boolean isBTSHost(String host) {
		return host.equalsIgnoreCase("bugs.debian.org");
	}
}
