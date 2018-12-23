package com.forter.hernanarber.fortersdk;

import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
// for the JSON Serialization:
import com.google.gson.Gson;
// for the Wifi Configurations:
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;

import static com.forter.hernanarber.fortersdk.NetworkConnectionReceiver.checkConnectionStatus;

public class ForterSDK {

    // Utility Constants:
    private static final String DEFAULT_EXPORT_SERVER_URL = "http://127.0.0.1:8889";
    private static final String TEST_EXPORT_SERVER_URL = "http://requestbin.fullcontact.com/u55espu5"; // request.bin Test Server

    private static final int DEFAULT_MAX_QUEUE_SIZE = 20;  // Assuming a maximum of 20 Events per 10 seconds period...
    private static final long FLUSH_TIMEOUT = 10*1000;  // 10 Seconds

    // Basic Properties:
    private String apiKey;
    private String deviceUID;

    // The Data Queue for sending Events in Batches:
    private final Queue<EventData> dataQ = new LinkedList<>();

    // for processing the Queue:
    private long flushTime = FLUSH_TIMEOUT;
    private Timer fTimer;

    //  private Timer timer;

    // the Singleton:
    private static ForterSDK forter;

    // for internal use:
    private String exportServerUrl = DEFAULT_EXPORT_SERVER_URL;
    private int maxQueueSize = DEFAULT_MAX_QUEUE_SIZE;

    // json fields
    private static final String APP_FOREGROUND = "InForeground";
    private static final String NET_ONLINE = "NetworkConnected";
    private static final String NET_WIFI = "WifiConnected";
    private static final String NET_IP_ADDR = "IpAddress";

    // Network States:
    private volatile boolean isOnline;
    private volatile boolean wifiConnected;
    private volatile String ipAddress;

    // App Lifecycle:
    private volatile boolean inForeground;

    // The Internal Initializer:
    private ForterSDK() {}

    // The External Initializer / getter:
    public static ForterSDK get() {
        if (forter == null) {
            forter = new ForterSDK();
        }
        return forter;
    }

    // Alternative Initializer (Auto-Generate DeviceID)
    public void init(Context context, String apiKey) {
        init(context, apiKey, null);
    }

    public void init(Context context, String apiKey, String deviceUID) {
        if (deviceUID == null) {
            deviceUID = getAndroidId(context);
        }
        this.apiKey = apiKey;
        this.deviceUID = deviceUID;

        if (!isValidApiKey()) { //gggFix
            String err = "Error: invalid API key: "+ this.apiKey;
            System.out.println(err);
            throw new RuntimeException(err);
        }
        // Setting the Export URL to requestbin for TESTING PURPOSES:
        exportServerUrl = TEST_EXPORT_SERVER_URL;
        // get initial connection status and Check for Changes:
        checkConnectionStatus(context);
        listenForConnectionChanges(context);
        // Initialize Queue Timer:
        setupTimer();
        System.out.println("ForterSDK: Forter has been Initialized..");
    }

    //Queue data events for sending in JSON format
    public void track(String actionKey, Object jsonData) {
        final EventData event = new EventData(actionKey, jsonData, deviceUID);
        synchronized (dataQ) {
            dataQ.add(event);
            // Removing Last Object in case the Queue is OverLoaded:
            if (dataQ.size() > maxQueueSize) {
                dataQ.remove();
            }
        }
    }

    // Helper Methods:

    public static String getAndroidId(Context context) {
        ContentResolver cr = context.getContentResolver();
        // we are aware that this is not a Recommended Method...
        // Pending: get a better Unique ID:
        return Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
    }

    //setupTimer
    private void setupTimer() {
        stopTimer(); // stop prior instance
        fTimer = new Timer();
        fTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                processQueue();
            }
        }, 0, flushTime);
    }

    private void stopTimer() {
        if (fTimer != null) {
            fTimer.cancel();
            fTimer = null;
        }
    }

    private boolean isValidApiKey() {
        //TODO: Validate API Key (Pending Security Parameters)
        return true;
    }

    private void processQueue() {
        if (!isOnline) {
            System.out.println("flushEvents failed: disconnected");
            return;
        }
        // we Create a Temporary queue to Process so that the original dataQ is not Locked for new Events:
        LinkedList<EventData> toSend = new LinkedList<>();
        synchronized (dataQ) {
            toSend.addAll(dataQ);
            dataQ.clear();
        }
        // Sending the Events:
        if (toSend.isEmpty()) {
            System.out.println("ForterSDK: flushEvents failed - No pending events");
            return;
        }
        System.out.println("ForterSDK: Events to sent: " + toSend.toString());
        sendEventsToServer(toSend);
        System.out.println("ForterSDK: flushEvents success - Events sent");
    }

    private void sendEventsToServer(final LinkedList<EventData> toSend) {
        new Thread() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String json = gson.toJson(toSend);
                JsonTransmitter.send(json, exportServerUrl);
            }
        }.start();
    }

    // State Tracking Utilities:

    public void updateNetworkState(boolean _isOnline, boolean _wifiConnected, String _ipAddress) {
        isOnline = _isOnline;
        wifiConnected = _wifiConnected;
        ipAddress = _ipAddress;

        //gggFix create separate events
        track(NET_ONLINE, isOnline);
        track(NET_WIFI, wifiConnected);
        track(NET_IP_ADDR, ipAddress);
    }

    private void listenForConnectionChanges(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(new NetworkConnectionReceiver(), intentFilter);
    }

    public void updateForegroundState(boolean _inForeground) {
        inForeground = _inForeground;
        track(APP_FOREGROUND, inForeground);
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isWifiConnected() {
        return wifiConnected;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean appIsInForeground() {
        return inForeground;
    }

    public void setExportServerUrl(String exportServerUrl) {
        this.exportServerUrl = exportServerUrl;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    // BONUS: track Configured Networks:
    // Code Partly taken from: https://www.codota.com/code/java/methods/android.net.wifi.WifiManager/getConfiguredNetworks
    public void trackPreviousNetworks(Context context) {
        WifiConfiguration wifiConf = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration conf : configuredNetworks){
            track("Network-ID", conf.networkId);
        }
    }

}
