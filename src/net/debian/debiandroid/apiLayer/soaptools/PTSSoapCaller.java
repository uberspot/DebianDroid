package net.debian.debiandroid.apiLayer.soaptools;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;

public class PTSSoapCaller extends SoapCaller{

    public PTSSoapCaller(Context context) {
    	super(context);
    	URL = "http://packages.qa.debian.org/cgi-bin/soap-alpha.cgi";
    }
    
    public String getSOAPAPIVersion() {
        try {
			return doRequest("version", "version", null).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }

    public String getLatestVersion(String packageName) {
    	PropertyInfo[] properties = convertToSourceProperty(packageName);
        try {
			return doRequest("latest_version", "latest_version", properties).toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }
    
    public String versions(String packageName) {
    	PropertyInfo[] properties = convertToSourceProperty(packageName);
        try {
			return doRequest("versions", "versions", properties).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }
    
    public String getMaintainerName(String packageName) {
    	PropertyInfo[] properties = convertToSourceProperty(packageName);
        try {
			return doRequest("maintainer_name", "maintainer_name", properties).toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }
    
    public String getMaintainerEmail(String packageName) {
    	PropertyInfo[] properties = convertToSourceProperty(packageName);
        try {
			return doRequest("maintainer_email", "maintainer_email", properties).toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }
    
    public String[] getBinaryNames(String packageName) {
    	PropertyInfo[] properties = convertToSourceProperty(packageName);
        try {
        	String response = doRequest("binary_names", "binary_names", properties).toString();
        	return response.trim().replace("binary_namesResponse{", "")
        	.replaceAll("^.{5,12}=|;.{5,12}=|;.{1,8}$", " ").trim().split(" ");
		} catch (Exception e) {
			e.printStackTrace();
		}
        return new String[]{};
    }
    
    public String getLintianSummary(String packageName) {
    	PropertyInfo[] properties = convertToSourceProperty(packageName);
        try {
			return doRequest("lintian", "lintian", properties).toString()
					.replace("lintianResponse{", "").replace("}", "").trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }
    
    public String[] getUploaderNames(String packageName) {
    	PropertyInfo[] properties = convertToSourceProperty(packageName);
        try {
			String response = doRequest("uploader_names", "uploader_names", properties).toString();
        	return response.trim().replace("uploader_namesResponse{", "")
        	.replaceAll("^.{5,12}=|;.{5,12}=|;.{1,8}$", " ").trim().split(" [^ ] ");
		} catch (Exception e) {
			e.printStackTrace();
		}
        return new String[]{};
    }
    
    public String getBugCounts(String packageName) {
    	PropertyInfo[] properties = convertToSourceProperty(packageName);
        try {
			return doRequest("bug_counts", "bug_counts", properties).toString()
					.replace("bug_countsResponse{", "").replace("}", "").trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }

	private PropertyInfo[] convertToSourceProperty(String packageName) {
		PropertyInfo[] properties = new PropertyInfo[1];
        properties[0] = new PropertyInfo();
        properties[0].setName("source");
        properties[0].setValue(packageName);
        properties[0].setType(String.class);
		return properties;
	}

}