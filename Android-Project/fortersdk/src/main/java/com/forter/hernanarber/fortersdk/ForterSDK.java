package com.forter.hernanarber.fortersdk;

import android.util.Log;
import org.json.JSONObject;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class ForterSDK {

    String aKey;
    String dUID;

    Queue<DataObject> dataQ = new LinkedList<>();

    // for processing the Queue:
    private long flushTime = 10000; // 10 Seconds
    private Timer fTimer;


    // the initializer:
    public ForterSDK (String apiKey, String deviceUID) {
        aKey = apiKey;
        dUID = deviceUID;
        // Initializing the timer:
        setupTimer();
        Log.d("forter", "Forter has been Initialized..");
    }

    public void track (String actionKey, JSONObject data) {

        DataObject dataObj = new DataObject();
        dataObj.key = actionKey;
        dataObj.data = data;
        // Adding Data Object to the Queue:
        dataQ.add(dataObj);
        Log.d("forter", "Added Event for Tracking: Current Queue has " + dataQ.size() + " Objects");
    }

    // Helper Methods:

    private void processQueue() {
        
        Log.d("forter", "Event Queue Processed: Current Queue has " + dataQ.size() + " Objects");
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
