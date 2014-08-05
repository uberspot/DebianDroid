package net.debian.debiandroid.apiLayer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.debian.debiandroid.apiLayer.soaptools.SoapCaller;


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

	public static String getSubstringIn(String original, String startRegex, String endRegex) {
        Pattern pattern = Pattern.compile(startRegex + "\\s*(.*?)\\s*" + endRegex + "\\s*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(original);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
	}
}
