package com.forter.hernanarber.fortersdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;
import static android.net.ConnectivityManager.TYPE_WIFI;

public class NetworkConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        checkConnectionStatus(context);
    }

    public static void checkConnectionStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isOnline = netInfo != null && netInfo.isConnected();

        NetworkInfo mWifi = cm.getNetworkInfo( TYPE_WIFI);

        String ipAddress = "";
        if (mWifi.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        }

        // and update sdk object
        ForterSDK.get().updateNetworkState(isOnline, mWifi.isConnected(), ipAddress);
    }

}
