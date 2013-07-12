package com.debian.debiandroid.apiLayer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class SoapCaller {
	
	protected String NAMESPACE;
	protected String URL;
	
	public SoapCaller(){
		NAMESPACE = "";
		URL = "";
	}

    public Object doRequest(String methodName, String soapAction, PropertyInfo[] properties) 
    					throws IllegalArgumentException {
    	//for some reason if i use .isEmpty() i get an android api warning
    	if(methodName == null || methodName.equals("") || soapAction==null || soapAction.equals("")
    			 || URL.equals("")) { 
    		throw new IllegalArgumentException();
    	}
        SoapObject request = new SoapObject(NAMESPACE, methodName);
        if(properties != null){
	    	for(PropertyInfo property: properties) {
	        	request.addProperty(property);
	        }
        }
        SoapSerializationEnvelope envelope = new
        SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        //androidHttpTransport.debug = true;
        try {
            androidHttpTransport.call(soapAction, envelope);
            //Log.i("QRCODE", androidHttpTransport.requestDump);
            return envelope.bodyIn; //return response
        }
        catch(Exception e) {
        	e.printStackTrace();
        	//throw new IOException();
        }
        return -1;
    }
}
