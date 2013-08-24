package com.debian.debiandroid;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.RemoteViews;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
	 
	 @Override
	 public void onReceive(Context context, Intent intent) {
	  PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	  PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getString(R.string.debiandroid_widget));
	  //Acquire the lock
	  wl.acquire();
	 
	  //You can do the processing here update the widget/remote views.
	  RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
	    R.layout.ddwidget);
	  remoteViews.setTextViewText(R.id.widget_textview, DebianDroidWidgetProvider.getWidgetText(context));
	  ComponentName thiswidget = new ComponentName(context, DebianDroidWidgetProvider.class);
	  AppWidgetManager manager = AppWidgetManager.getInstance(context);
	  manager.updateAppWidget(thiswidget, remoteViews);
	  //Release the lock
	  wl.release();
	 }
 }
