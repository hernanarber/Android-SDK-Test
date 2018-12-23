package com.forter.hernanarber.fortersdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;


public class AppLifecycleListener extends Application implements Application.ActivityLifecycleCallbacks {

    // This is the Best Practices for Tracking Android's App LifeCycle According to:
    // https://medium.com/@iamsadesh/android-how-to-detect-when-app-goes-background-foreground-fd5a4d331f8a

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            ForterSDK.get().updateForegroundState(true);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!ForterSDK.get().appIsInForeground()) {
            ForterSDK.get().updateForegroundState(true);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            ForterSDK.get().updateForegroundState(false);
        }
    }

    // Pending: Track Other APP States (not a Requirement for now)
    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}