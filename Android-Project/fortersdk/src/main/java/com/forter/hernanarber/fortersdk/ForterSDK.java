package com.forter.hernanarber.fortersdk;

import android.util.Log;

public class ForterSDK {

    String aKey;
    String dUID;

    // the initializer:
    public ForterSDK (String apiKey, String deviceUID) {
        aKey = apiKey;
        dUID = deviceUID;
        Log.d("forter", "Forter has been Initialized..");
    }

    

}
