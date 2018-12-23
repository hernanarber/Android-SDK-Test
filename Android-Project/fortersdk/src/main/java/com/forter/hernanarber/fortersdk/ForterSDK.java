package com.forter.hernanarber.fortersdk;

import android.util.Log;
import org.json.JSONObject;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import android.arch.lifecycle.LifecycleOwner;


public class ForterSDK {

    // Basic Properties:
    String aKey;
    String dUID;

    // The Data Queue for sending Events in Batches:
    private Queue<EventData> dataQ = new LinkedList<>();
    // for processing the Queue:
    private long flushTime = 10000; // 10 Seconds
    private Timer fTimer;

    // for LifeCycle Tracking:
    LifecycleOwner appOwner;

    // the initializer:
    public ForterSDK (String apiKey, String deviceUID) {
        aKey = apiKey;
        dUID = deviceUID;
        // Initializing the timer:
        setupTimer();
        // Tracking App's LifeCycle:
       // appOwner.getLifecycle().addObserver(new ForterLifecycleListener());
        Log.d("forter", "Forter has been Initialized..");
    }

    public void track (String actionKey, JSONObject data) {

        EventData dataObj = new EventData();
        dataObj.key = actionKey;
        dataObj.data = data;
        // Adding Data Object to the Queue:
        if (dataQ.size() >= ) {

            dataQ.remove();
        }
        dataQ.add(dataObj);
        Log.d("forter", "Added Event for Tracking: Current Queue has " + dataQ.size() + " Objects");
    }

    // Helper Methods:

    private boolean isNetworkAvailable() {
        //Pending: Need to Handle App's Context:

//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return true;
    }

    private void processQueue() {
        while (!dataQ.isEmpty()) {
            EventData obj = dataQ.remove();
            sendEvent(obj);
        }
        Log.d("forter", "Event Queue Processed: Current Queue has " + dataQ.size() + " Objects");
    }

    private void sendEvent(EventData event) {
        // Pending SEND Implementation:
        // Server.Send(action: event.key, data: event.data);

    }

    private void setupTimer() {
        TimerTask process = new TimerTask() {
            @Override
            public void run() {
                processQueue();
            }
        };
        fTimer = new Timer();
        long delay = 0;
        // schedules the process Task to be Executed every flushTime / 1000 Seconds:
        fTimer.scheduleAtFixedRate(process, delay, flushTime);
    }

}
