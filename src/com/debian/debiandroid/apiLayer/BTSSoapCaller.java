package com.debian.debiandroid.apiLayer;

import org.ksoap2.serialization.PropertyInfo;

import android.util.Log;

public class BTSSoapCaller extends SoapCaller{

    public BTSSoapCaller() {
    	NAMESPACE = "Debbugs/SOAP";
    	URL = "http://bugs.debian.org/cgi-bin/soap.cgi";
    }

    public void getBugs(String key, String value) {
    	PropertyInfo[] properties = new PropertyInfo[2];
        properties[0] = new PropertyInfo();
        properties[0].setName("key");
        properties[0].setValue(key);
        properties[0].setType(String.class);
        properties[1] = new PropertyInfo();
        properties[1].setName("value");
        properties[1].setValue(value);
        properties[1].setType(String.class);
        
        try {
        	//Return this later on...
			Log.i("QRCODE", doRequest("get_bugs", "get_bugs", properties).toString());
		} catch (Exception e) {
			//Handle error better
			e.printStackTrace();
		}
    }

}