package com.debian.debiandroid;

import org.joda.time.DateTime;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/*** Class that gets called when it's time to update the widgets info. 
 * The update period is defined in xml/ddwidget_info.xml
 * and the minimum update time is 30 minutes. If you need more 
 * frequent updates you can use AlarmManager. */
public class DebianDroidWidgetProvider extends AppWidgetProvider {
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		//Find time of next dinstall and time interval to it and display them
		DateTime now = new DateTime();
		long nextMS = getRemainingMsToDInst(now); 
		
		for (int i=0; i<appWidgetIds.length; i++) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ddwidget);
	        remoteViews.setTextViewText( R.id.widget_textview, 
	        		"Next DInstall at:\n" + msToTimeStamp(now.getMillis() + nextMS) + " (UTC)\n" +
	        		" or in " + msToRemainingTimeStamp(nextMS) + " hours");
	        appWidgetManager.updateAppWidget( appWidgetIds[i], remoteViews );
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	private static String msToRemainingTimeStamp(long nextMS) {
		nextMS /=1000;
		long hours = Double.valueOf(Math.floor(nextMS/3600)).longValue();
		nextMS %= 3600;

		long minutes = Double.valueOf(Math.floor(nextMS/60)).longValue();
	    nextMS %= 60;
	    return hours+":"+minutes+":"+nextMS;
	}
	 
	private static String msToTimeStamp(long nextMS) {
		DateTime timestamp = new DateTime(nextMS);
		return timestamp.getHourOfDay() + ":" + 
			   timestamp.getMinuteOfHour() + ":" + 
			   timestamp.getSecondOfMinute();
	}
	
	private static long getRemainingMsToDInst(DateTime now) {
		//.withZone(DateTimeZone.UTC) for computing based on the devices timezone
		DateTime dinstall = new DateTime().withHourOfDay(19) 
				  .withMinuteOfHour(52)
				  .withSecondOfMinute(0);
		double rest = Math.floor((dinstall.getMillis() - now.getMillis())) % 21600000; // mod 6 hours in milliseconds
		if(now.isAfter(dinstall)) rest+=21600000;
			
		return Double.valueOf(rest).longValue();
	}
}
