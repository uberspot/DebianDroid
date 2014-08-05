
package net.debian.debiandroid;

import java.util.Timer;
import java.util.TimerTask;

import net.debian.debiandroid.broadcastreceivers.NetworkChangeReceiver;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DDNotifyService extends Service {

    public static int updateIntervalTime = 60000; // 60000 ms = 1 minutes

    public static boolean isForeground;

    private Timer timer;

    @Override
    public void onCreate() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new CheckConnectivityTask(), 1000, updateIntervalTime);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        super.onDestroy();
        timer.cancel();
        timer = null;
    }

    private class CheckConnectivityTask extends TimerTask {

        @Override
        public void run() {
            NetworkChangeReceiver.handleConnectivityChange(getApplicationContext());
        }
    }

    public static synchronized void activityResumed() {
        isForeground = true;
    }

    public static synchronized void activityPaused() {
        isForeground = false;
    }
}