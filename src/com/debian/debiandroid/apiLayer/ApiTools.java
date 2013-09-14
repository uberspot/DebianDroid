package com.debian.debiandroid.apiLayer;

import com.debian.debiandroid.apiLayer.soaptools.SoapCaller;

public class ApiTools {
	
	public static void enableUseOfNet() {
		// Activate searches via api
		SoapCaller.netEnabled = true;
		HTTPCaller.netEnabled = true;
	}
	public static void disableUseOfNet() {
		// Deactivate searches via api
		SoapCaller.netEnabled = false;
		HTTPCaller.netEnabled = false;
	}
	public static boolean isNetEnabled() {
		return SoapCaller.netEnabled && HTTPCaller.netEnabled;
	}
}
