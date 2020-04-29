package com.videoplayer;

import android.util.Log;

import com.brentvatne.exoplayer.events.BackgroundEnterMessage;
import com.brentvatne.exoplayer.events.BackgroundLeaveMessage;
import com.facebook.react.ReactActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends ReactActivity {

    private static final String TAG = "MainActivity";

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "VideoPlayer";
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onBackgroundEnter(BackgroundEnterMessage event) {
        Log.d(TAG, "************ BACKGROUND ENTER");
    }

    @Subscribe
    public void onBackgroundExit(BackgroundLeaveMessage event) {
        Log.d(TAG, "************ BACKGROUND LEAVE");
    }
}
