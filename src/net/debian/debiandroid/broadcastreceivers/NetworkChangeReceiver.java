package net.debian.debiandroid.broadcastreceivers;

import net.debian.debiandroid.DDNotifyService;
import net.debian.debiandroid.R;
import net.debian.debiandroid.apiLayer.ApiTools;
import net.debian.debiandroid.utils.NetUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		handleConnectivityChange(context);
	} 

	public static void handleConnectivityChange(Context context) {
		// Check if wifi not enabled and 3g not allowed
		// if wifi and 3g not enabled
		// notify users in both cases and if no3g allowed deactivate requests
		boolean hasConnectivity = NetUtils.hasConnectivity(context);
		
		if(!hasConnectivity) {
			if(ApiTools.isNetEnabled()) {
				// Notify user
				if(DDNotifyService.isForeground)
					Toast.makeText(context, R.string.lost_connectivity, Toast.LENGTH_SHORT).show();
				ApiTools.disableUseOfNet();
			}
		} else {
			boolean hasMobileCon = NetUtils.hasNetProviderConnection(context, NetUtils.MOBILE);
			boolean mobileAllowed = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use3g", true);
			if(hasMobileCon && !mobileAllowed) {
				if(ApiTools.isNetEnabled()) {
					// Notify user
					if(DDNotifyService.isForeground)
						Toast.makeText(context, R.string.lost_connectivity, Toast.LENGTH_SHORT).show();
					ApiTools.disableUseOfNet();
				}
			} else {
				if(!ApiTools.isNetEnabled()) {
					// Notify user
					if(DDNotifyService.isForeground)
						Toast.makeText(context, R.string.connection_restored, Toast.LENGTH_SHORT).show();
					ApiTools.enableUseOfNet();
				}
			}
		}
		
	}
}
