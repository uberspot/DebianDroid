package com.debian.debiandroid.apiLayer.soaptools;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import androidStorageUtils.Cacher;

public class SoapCaller {
	
	protected String NAMESPACE;
	protected String URL;
	protected Cacher cacher;
	
	public SoapCaller(Context context){
		NAMESPACE = "";
		URL = "";
		cacher = new Cacher(context);
	}

    public String doRequest(String methodName, String soapAction, PropertyInfo[] properties) 
    					throws IllegalArgumentException {
    	//for some reason if i use .isEmpty() i get an android api warning
    	if(methodName == null || methodName.equals("") || soapAction==null || soapAction.equals("")
    			 || URL.equals("")) { 
    		throw new IllegalArgumentException();
    	}
    	String reqFileName = methodName + soapAction;
        SoapObject request = new SoapObject(NAMESPACE, methodName);
        if(properties != null) {
	    	for(PropertyInfo property: properties) {
	        	request.addProperty(property);
	        	reqFileName += property.getValue().toString();
	        }
        }
        // if (fresh) cached string exists then return it, otherwise 
        // continue with the normal retrieval
        String cachedString = cacher.getCachedString(reqFileName);
        if(cachedString!=null) {
        	return cachedString;
        }
        SoapSerializationEnvelope envelope = new
        SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        //androidHttpTransport.debug = true;
        try {
            androidHttpTransport.call(soapAction, envelope);
            //Log.i("Debian", androidHttpTransport.requestDump);
            //Cache new response before returning it
            Object response = envelope.bodyIn;
            cacher.cacheString(reqFileName, response.toString());
            return response.toString();
        } catch(Exception e) {
        	e.printStackTrace();
        }
        return ""; //Inform upper levels about errors
    }
}
