
package net.debian.debiandroid.apiLayer.soaptools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;

public class BTSSoapCaller extends SoapCaller {

    public BTSSoapCaller(Context context) {
        super(context);
        NAMESPACE = "Debbugs/SOAP";
        URL = "https://bugs.debian.org/cgi-bin/soap.cgi";
    }

    /** Key values for 'key' parameter in getBugs method*/
    public static final String PACKAGE = "package", SUBMITTER = "submitter", MAINT = "maint", SRC = "src",
            SEVERITY = "severity", BUGNUMBER = "bugnum", STATUS = "status", STATUS_DONE = "done",
            STATUS_FORWARDED = "forwarded", STATUS_OPEN = "open", OWNER = "owner", ARCHIVE = "archive",
            ARCHIVE_TRUE = "true", ARCHIVE_FALSE = "false", ARCHIVE_BOTH = "both", TAG = "tag";

    public ArrayList<String> getBugs(String keys[], String values[]) {
        if (keys.length != values.length) {
            return new ArrayList<String>();
        }
        PropertyInfo[] properties = new PropertyInfo[keys.length * 2];
        for (int i = 0, j = 0; (i < keys.length) && (j < (keys.length * 2)); i++) {
            properties[j] = new PropertyInfo();
            properties[j].setName("key");
            properties[j].setValue(keys[i]);
            properties[j].setType(String.class);
            j++;
            properties[j] = new PropertyInfo();
            properties[j].setName("value");
            properties[j].setValue(values[i]);
            properties[j].setType(String.class);
            j++;
        }
        try {
            String response = doRequest("get_bugs", "get_bugs", properties).toString();
            response = response.replaceAll("get_bugsResponse\\{Array=\\[|^\\s+|\\s+$|\\]; \\}$", "");
            if ("".equals(response)) {
                return new ArrayList<String>();
            }
            return new ArrayList<String>(Arrays.asList(response.split(", ")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    public ArrayList<HashMap<String, String>> getStatus(int[] bugNumbers) {
        PropertyInfo[] properties = new PropertyInfo[bugNumbers.length];
        for (int i = 0; i < bugNumbers.length; i++) {
            properties[i] = new PropertyInfo();
            properties[i].setName("bugnumber");
            properties[i].setValue(bugNumbers[i]);
            properties[i].setType(int.class);
        }
        try {
            String response = doRequest("get_status", "get_status", properties).toString();
            response = response.replace("get_statusResponse{s-gensym3=Map{", "").trim();
            return parseStatuses(response.substring(0, response.length() - 4), bugNumbers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<HashMap<String, String>>();
    }

    private static ArrayList<HashMap<String, String>> parseStatuses(String statusString, int[] bugNumbers) {
        ArrayList<HashMap<String, String>> statuses = new ArrayList<HashMap<String, String>>();
        int[] statusPositions = new int[bugNumbers.length];
        for (int i = 0; i < bugNumbers.length; i++) {
            statusPositions[i] = statusString.indexOf("item=anyType{key=" + bugNumbers[i]);
        }
        Arrays.sort(statusPositions);
        for (int i = 0; i < (statusPositions.length - 1); i++) {
            statuses.add(parseStatus(statusString.substring(statusPositions[i], statusPositions[i + 1])
                    .trim()));
        }
        statuses.add(parseStatus(statusString.substring(statusPositions[statusPositions.length - 1]).trim()));
        return statuses;
    }

    private static HashMap<String, String> parseStatus(String statusString) {
        HashMap<String, String> status = new HashMap<String, String>();
        status.put("bug_num", getTagValue("bug_num", statusString));
        status.put("date", getTagValue("date", statusString));
        status.put("originator", getTagValue("originator", statusString));
        status.put("msgid", getTagValue("msgid", statusString));
        status.put("subject", getTagValue("subject", statusString));
        status.put("source", getTagValue("source", statusString));
        status.put("severity", getTagValue("severity", statusString));
        status.put("tags", getTagValue("tags", statusString));
        status.put("last_modified", getTagValue("last_modified", statusString));
        status.put("pending", getTagValue("pending", statusString));
        status.put("archived", getTagValue("archived", statusString));
        return status;
    }

    private static String getTagValue(String tag, String statusString) {
        StringBuilder value = new StringBuilder("");
        // do a for from the start position of the tag + the length of the tag + the char '='
        // till the first occurence of the char ';' to extract the value of the tag
        for (int i = statusString.indexOf(tag) + tag.length() + 1; i < statusString.length(); i++) {
            char ch = statusString.charAt(i);
            if (ch == ';') {
                break;
            }
            value.append(ch);
        }
        return value.toString();
    }

    public ArrayList<HashMap<String, String>> getBugLog(int bugNumber) {
        PropertyInfo[] properties = new PropertyInfo[1];
        properties[0] = new PropertyInfo();
        properties[0].setName("bugnumber");
        properties[0].setValue(bugNumber);
        properties[0].setType(int.class);
        try {
            String response = doRequest("get_bug_log", "get_bug_log", properties).toString();

            return parseStatusLog(response.replaceAll("get_bug_logResponse\\{Array=\\[|^\\s+|\\s+$|\\]; \\}",
                    ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<HashMap<String, String>>();
    }

    private static ArrayList<HashMap<String, String>> parseStatusLog(String statusLog) {
        ArrayList<HashMap<String, String>> logMails = new ArrayList<HashMap<String, String>>();
        String[] logs = statusLog.split("ur-type\\{body=");
        for (String log : logs) {
            if ((log != null) && (log.length() != 0)) {
                logMails.add(parseLogMail(log));
            }
        }
        return logMails;
    }

    private static HashMap<String, String> parseLogMail(String log) {
        HashMap<String, String> logMail = new HashMap<String, String>();
        int indexOfBodyEnd = log.indexOf("; msg_num");
        logMail.put("body", log.substring(0, indexOfBodyEnd).trim());
        String[] lines = log.substring(indexOfBodyEnd).split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Date:")) {
                logMail.put("date", line.replace("Date: ", ""));
            }
            if (line.startsWith("From:")) {
                logMail.put("from", line.replace("From: ", ""));
            }
            if (line.startsWith("To:")) {
                logMail.put("to", line.replace("To: ", ""));
            }
            if (line.startsWith("Cc:")) {
                logMail.put("cc", line.replace("Cc: ", ""));
            }
            if (line.startsWith("Subject:")) {
                logMail.put("subject", line.replace("Subject: ", ""));
            }
        }
        return logMail;
    }

    public HashMap<String, int[]> getUserTag(String email, String[] tags) {
        PropertyInfo[] properties = new PropertyInfo[1 + tags.length];
        properties[0] = new PropertyInfo();
        properties[0].setName("email");
        properties[0].setValue(email);
        properties[0].setType(String.class);

        for (int i = 1; i < tags.length; i++) {
            properties[i] = new PropertyInfo();
            properties[i].setName("tag");
            properties[i].setValue(tags[i]);
            properties[i].setType(String.class);
        }
        try {
            String response = doRequest("get_usertag", "get_usertag", properties).toString();
            String[] tagsInResponse = response.replace("get_usertagResponse{s-gensym3=anyType{", "")
                    .replace("}; }", "").trim().split(";");
            HashMap<String, int[]> tagsAndBugNums = new HashMap<String, int[]>();
            for (String tagInResponse : tagsInResponse) {
                int indexOfEquals = tagInResponse.indexOf('=');
                String tag = tagInResponse.substring(0, indexOfEquals);
                String[] nums = tagInResponse.substring(indexOfEquals + 1).replace("[", "").replace("]", "")
                        .split(", ");
                int[] bugNums = new int[nums.length];
                for (int i = 0; i < nums.length; i++) {
                    try {
                        bugNums[i] = Integer.parseInt(nums[i]);
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                    ;
                }
                tagsAndBugNums.put(tag, bugNums);
            }

            return tagsAndBugNums;
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
            String[] nums = response.trim().replace("newest_bugsResponse{Array=[", "").replace("]; }", "")
                    .trim().split(", ");
            int[] bugNums = new int[nums.length];
            for (int i = 0; i < nums.length; i++) {
                try {
                    bugNums[i] = Integer.parseInt(nums[i]);
                } catch (NumberFormatException nfe) {
                }
            }
            return bugNums;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[] {};
    }

}
