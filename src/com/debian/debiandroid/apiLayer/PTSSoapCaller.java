package com.debian.debiandroid.apiLayer;

import org.ksoap2.serialization.PropertyInfo;

import android.util.Log;

public class PTSSoapCaller extends SoapCaller{

    public PTSSoapCaller() {
    	URL = "http://packages.qa.debian.org/cgi-bin/soap-alpha.cgi";
    }

    public void getLatestVersion(String packageName) {
    	PropertyInfo[] properties = new PropertyInfo[1];
        properties[0] = new PropertyInfo();
        properties[0].setName("source");
        properties[0].setValue(packageName);
        properties[0].setType(String.class);
        
        try {
        	//Return this later on...
			Log.i("QRCODE", doRequest("latest_version", "latest_version", properties).toString());
		} catch (Exception e) {
			//Handle error better
			e.printStackTrace();
		}
    }

}