package com.debian.debiandroid.apiLayer;

import java.util.HashMap;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;

public class BTSSoapCaller extends SoapCaller{

    public BTSSoapCaller(Context context) {
    	super(context);
    	NAMESPACE = "Debbugs/SOAP";
    	URL = "http://bugs.debian.org/cgi-bin/soap.cgi";
    }
    
    /** Key values for 'key' parameter in getBugs method*/
    public enum BUGKEY {PACKAGE, SUBMITTER,MAINT, SRC, SEVERITY, STATUS, OWNER};

    public int[] getBugs(String key, String value) {
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
        	String response = doRequest("get_bugs", "get_bugs", properties).toString();
        	String[] nums = response.trim().replace("get_bugsResponse{Array=[", "")
        			.replace("]; }", "").trim().split(", ");
        	int[] bugNums = new int[nums.length];
        	for (int i = 0; i < nums.length; i++) {
        	    try {
        	    	bugNums[i] = Integer.parseInt(nums[i]);
        	    } catch (NumberFormatException nfe) {};
        	}
        	return bugNums;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return new int[]{};
    }
    
    public String getStatus(int[] bugNumbers) {
    	PropertyInfo[] properties = new PropertyInfo[bugNumbers.length];
    	for(int i=0; i<bugNumbers.length; i++) {
	        properties[i] = new PropertyInfo();
	        properties[i].setName("bugnumber");
	        properties[i].setValue(bugNumbers[i]);
	        properties[i].setType(int.class);
    	}
        try {
        	String response = doRequest("get_status", "get_status", properties).toString();
        	response = response.replace("get_statusResponse{s-gensym3=Map{","")
        			.trim();
        	return response.substring(0, response.length()-4); 
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }
    
    public String getBugLog(int bugNumber) {
    	PropertyInfo[] properties = new PropertyInfo[1];
        properties[0] = new PropertyInfo();
        properties[0].setName("bugnumber");
        properties[0].setValue(bugNumber);
        properties[0].setType(int.class);
        try {
        	String response = doRequest("get_bug_log", "get_bug_log", properties).toString();
        	
        	return response.replace("get_bug_logResponse{Array=[","").replace("]; }", "")
        			.trim(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }
    
    public HashMap<String, int[]> getUserTag(String email, String[] tags) {
    	PropertyInfo[] properties = new PropertyInfo[1+tags.length];
        properties[0] = new PropertyInfo();
        properties[0].setName("email");
        properties[0].setValue(email);
        properties[0].setType(String.class);
        
        for(int i=1; i<tags.length; i++) {
	        properties[i] = new PropertyInfo();
	        properties[i].setName("tag");
	        properties[i].setValue(tags[i]);
	        properties[i].setType(String.class);
    	}
        try {
        	String response = doRequest("get_usertag", "get_usertag", properties).toString();
        	String[] tagsInResponse = response.replace("get_usertagResponse{s-gensym3=anyType{","")
			.replace("}; }","").trim().split(";");
        	HashMap<String, int[]> tagsAndBugNums = new HashMap<String, int[]>();
        	for(String tagInResponse: tagsInResponse) {
        		int indexOfEquals = tagInResponse.indexOf('=');
        		String tag = tagInResponse.substring(0, indexOfEquals);
        		String[] nums = tagInResponse.substring(indexOfEquals+1).replace("[", "").replace("]", "").split(", ");
        		int[] bugNums = new int[nums.length];
            	for (int i = 0; i < nums.length; i++) {
            	    try {
            	    	bugNums[i] = Integer.parseInt(nums[i]);
            	    } catch (NumberFormatException nfe) {nfe.printStackTrace();};
            	}
            	tagsAndBugNums.put(tag, bugNums);
        	}
        	
        	return  tagsAndBugNums;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return new HashMap<String, int[]>();
    }
    
    public int[] getNewestBugs(int numOfBugs) {
    	PropertyInfo[] properties = new PropertyInfo[1];
        properties[0] = new PropertyInfo();
        properties[0].setName("amount");
        properties[0].setValue(numOfBugs);
        properties[0].setType(int.class);
        try {
        	String response = doRequest("newest_bugs", "newest_bugs", properties).toString();
        	String[] nums = response.trim().replace("newest_bugsResponse{Array=[", "")
        			.replace("]; }", "").trim().split(", ");
        	int[] bugNums = new int[nums.length];
        	for (int i = 0; i < nums.length; i++) {
        	    try {
        	    	bugNums[i] = Integer.parseInt(nums[i]);
        	    } catch (NumberFormatException nfe) {};
        	}
        	return bugNums;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return new int[]{};
    }

}