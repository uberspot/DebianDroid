
package net.debian.debiandroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/** Various methods related to networking on android. You need to add
 *  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *  in your apps Manifest.xml file for them to work.
 */
public class NetUtils {

    public final static String WIFI = "WIFI", MOBILE = "MOBILE";

    /** @param context
     *  @return true if the device has any type of connectivity established (or establishing at the moment), false otherwise
     */
    public static boolean hasConnectivity(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null) && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * @param context
     * @param netProvider possible values are NetUtils.WIFI or NetUtils.MOBILE
     * @return true if the given network provider of the device is connected, false otherwise
     */
    public static boolean hasNetProviderConnection(Context context, String netProvider) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase(netProvider) && ni.isConnected()) {
                return true;
            }
        }
        return false;
    }
}