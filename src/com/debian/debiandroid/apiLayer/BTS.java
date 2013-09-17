package com.debian.debiandroid.apiLayer;

import java.util.HashSet;
import java.util.Set;

import com.debian.debiandroid.apiLayer.soaptools.BTSSoapCaller;

import android.content.Context;
import android.net.Uri;
import androidStorageUtils.StorageUtils;

public class BTS extends BTSSoapCaller{

	private StorageUtils btsStorage;

	public static final String BTSSUBSCRIPTIONS = "BTSSubscriptions";
	
	public static final String NEWBUGREPORTMAIL = "submit@bugs.debian.org";
	
	public BTS(Context context) {
		super(context);
		btsStorage = StorageUtils.getInstance(context);
	}

	public boolean isSubscribedTo(String bugNumber) {
		return btsStorage.getPreferenceSet(BTSSUBSCRIPTIONS, new HashSet<String>()).contains(bugNumber);
	}

	public boolean removeSubscriptionTo(String bugNumber) {
		return btsStorage.removePreferenceFromSet(BTSSUBSCRIPTIONS, bugNumber);
	}

	public boolean addSubscriptionTo(String bugNumber) {
		return btsStorage.addPreferenceToSet(BTSSUBSCRIPTIONS, bugNumber);
	}
	
	public Set<String> getSubscriptions() {
		return btsStorage.getPreferenceSet(BTSSUBSCRIPTIONS, new HashSet<String>());
	}
	
	public static boolean isBTSHost(String host) {
		return host.equalsIgnoreCase("bugs.debian.org");
	}

	public static String getNewBugReportBody(String pckgName, String pckgVersion) {
		return  "Package: " + pckgName +
				"\nVersion: " + pckgVersion + 
				"\nSeverity: <Optional: Choose between important, normal, minor, wishlist>" + 
				"\nTags: <Optional: tags>" + 
				"\nX-Debbugs-CC: <Optional: add mails to send copies of this bug report to>\n" + 
				"Dear Maintainer,\n *** Please consider answering these questions, where appropriate ***\n\n" + 
				"* What led up to the situation?\n* What exactly did you do (or not do) that was effective (or" +
				"ineffective)?\n* What was the outcome of this action?\n* What outcome did you expect instead?\n\n" + 
				"*** End of the template - remove these lines ***\n";
	}

	public static String getNewBugReportSubject(String pckgName) {
		return "[" + pckgName + "] <Insert Bug Report Subject here>";
	}
	
	public static String BTSURIToPckgName(Uri uri) {
		return uri.getQueryParameter("package");
	}
	
	public static String BTSURIToBugNum(Uri uri) {
		return uri.getQueryParameter("bug");
	}
}
