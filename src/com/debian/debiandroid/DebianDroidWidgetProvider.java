package com.debian.debiandroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import androidStorageUtils.StorageUtils;

/*** Class that gets called when it's time to update the widgets info. 
 * The update period is defined in xml/ddwidget_info.xml
 * and the minimum update time is 30 minutes. If you need more 
 * frequent updates you can use AlarmManager. */
public class DebianDroidWidgetProvider extends AppWidgetProvider {
	
	public static final int widgetUpdateInterval = 600; //in seconds
	public static final String wUpdateIntervalKey = "wupdateinterval";
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int i=0; i<appWidgetIds.length; i++) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ddwidget);
	        remoteViews.setTextViewText( R.id.widget_textview, getWidgetText(context));
	        appWidgetManager.updateAppWidget( appWidgetIds[i], remoteViews );
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	public static String getWidgetText(Context context) {
		//Find time of next dinstall and time interval to it and display them
		DateTime now = new DateTime(DateTimeZone.UTC);
		DateTime nextDInstall = getNextDInst().withZone(DateTimeZone.UTC);
		long remainingMS = nextDInstall.getMillis() - now.getMillis();
		return context.getString(R.string.next_dinstall_at) + ":\n" + nextDInstall.toString("HH:mm:ss(z)") + "\n" +
		" " + context.getString(R.string.or_in)+" " + msToRemainingTimeStamp(remainingMS) + " " + 
		context.getString(R.string.hours) + " (" + DateTimeZone.getDefault() +")";
	}
	
	private static String msToRemainingTimeStamp(long nextMS) {
		nextMS /=1000;
		long hours = Double.valueOf(Math.floor(nextMS/3600)).longValue();
		nextMS %= 3600;

		long minutes = Double.valueOf(Math.floor(nextMS/60)).longValue();
	    nextMS %= 60;
	    return ((hours<10)?"0" + hours:hours) + ":" + 
	    	   ((minutes<10)?"0"+ minutes:minutes) + ":" +
	    	   ((nextMS<10)?"0" + nextMS:nextMS);
	}
	
	private static DateTime getNextDInst() {
		DateTime dinstall = new DateTime(DateTimeZone.UTC).withHourOfDay(19) 
				  .withMinuteOfHour(52)
				  .withSecondOfMinute(0);
		DateTime now = new DateTime();
		double rest = Math.floor((dinstall.getMillis() - now.getMillis())) % 21600000; // mod 6 hours in milliseconds
		if(now.isAfter(dinstall)) rest+=21600000;
		return now.plusMillis(Double.valueOf(rest).intValue());
	}
	 
	 @Override
	 public void onDisabled(Context context) {
		  stopWidgetUpdate(context);
		  super.onDisabled(context);
	 }
	 
	 public static void stopWidgetUpdate(Context context) {
		  Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		  PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		  AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		  alarmManager.cancel(sender);
	 }
	 
	 public static void startWidgetUpdate(Context context) {
		  AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		  Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		  PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		  //After after widgetUpdateInterval seconds
		  int upInterval = widgetUpdateInterval;
		  try {
			  upInterval = Integer.parseInt(StorageUtils.getInstance(context).getPreference(wUpdateIntervalKey, ""+widgetUpdateInterval));
		  } catch(NumberFormatException e) { e.printStackTrace(); }
		  am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 5), upInterval*1000 , pi);
	 }
	 
	 @Override
	 public void onEnabled(Context context) {
		  startWidgetUpdate(context);
		  super.onEnabled(context);
	 }
	 
}
