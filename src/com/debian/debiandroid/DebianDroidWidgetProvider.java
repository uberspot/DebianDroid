package com.debian.debiandroid;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/*** Class that gets called when it's time to update the widgets info. 
 * The update period is defined in xml/ddwidget_info.xml
 * and the minimum update time is 30 minutes. If you need more 
 * frequent updates you can use AlarmManager. */
public class DebianDroidWidgetProvider extends AppWidgetProvider {

	private static int calledSum = 0;
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int i=0; i<appWidgetIds.length; i++) {

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ddwidget);
	        remoteViews.setTextViewText( R.id.widget_textview, "Called " + calledSum++ + " times");
	        appWidgetManager.updateAppWidget( appWidgetIds[i], remoteViews );
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
