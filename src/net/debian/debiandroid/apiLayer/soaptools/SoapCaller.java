package net.debian.debiandroid.apiLayer.soaptools;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;

import com.uberspot.storageutils.Cacher;

public class SoapCaller {

	protected String NAMESPACE;
	protected String URL;
	protected static Cacher cacher;
	public static boolean netEnabled = true;

	public SoapCaller(Context context) {
		NAMESPACE = "";
		URL = "";
		cacher = new Cacher(context);
	}

    public String doRequest(String methodName, String soapAction, PropertyInfo[] properties)
    					throws IllegalArgumentException {
        if ((methodName == null) || methodName.equals("") || (soapAction == null) || soapAction.equals("")
                || URL.equals("")) {
            throw new IllegalArgumentException();
        }
        String reqFileName = methodName + soapAction;
        SoapObject request = new SoapObject(NAMESPACE, methodName);
        if (properties != null) {
            for (PropertyInfo property : properties) {
                request.addProperty(property);
                reqFileName += property.getValue().toString();
            }
        }
        // if (fresh) cached string exists then return it, otherwise
        // continue with the normal retrieval
        String cached = cacher.getCachedString(reqFileName);
        if ((cached != null) && (!netEnabled || (cacher.getTimeFromLastCache(reqFileName) <= Cacher.cacheLimit))) {
            return cached;
        }
        if (netEnabled) {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            //androidHttpTransport.debug = true;
            try {
                androidHttpTransport.call(soapAction, envelope);
                //Log.i("DebianDebug", androidHttpTransport.requestDump);
                //Cache new response before returning it
                String response = envelope.bodyIn.toString();
                cacher.cacheString(reqFileName, response);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //if any errors occured return the cached string (or "" if no cached version exists)
        return (cached != null) ? cached : "";
    }
}
