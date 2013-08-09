package com.debian.debiandroid;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class DDNotifyService extends Service {

	public static int updateIntervalTime = 600000; // 600000 ms = 10 minutes
	
	private static Timer timer = new Timer();
	private final static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// notify user about update via notification bar
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	  public void onCreate() {
		    timer.scheduleAtFixedRate(new AutoUpdateTask(), 1000, updateIntervalTime);
	  }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Toast.makeText(this, "Notification Service started",
		// Toast.LENGTH_SHORT).show();
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		// Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
	}

	private class AutoUpdateTask extends TimerTask {
		public void run() {
			// Search for updates to subscribed packages/bugs,
			// if found notify user with 
			handler.sendEmptyMessage(0);
		}
	}
}