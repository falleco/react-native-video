package com.videoplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.DrawableRes;

import com.brentvatne.exoplayer.events.BackgroundEnterMessage;
import com.brentvatne.exoplayer.events.BackgroundLeaveMessage;
import com.facebook.react.ReactActivity;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.annotation.Nullable;

public class MainActivity extends ReactActivity {

    private static final String TAG = "MainActivity";


    public static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    public static final int PLAYBACK_NOTIFICATION_ID = 1;
    public static final String MEDIA_SESSION_TAG = "audio_demo";

    private PlayerNotificationManager notificationManager;
    private MediaSessionCompat mediaSession;

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
        // EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onBackgroundEnter(BackgroundEnterMessage event) {
        Log.d(TAG, "************ BACKGROUND ENTER");

        PlayerNotificationManager.MediaDescriptionAdapter adapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
            @Override
            public String getCurrentContentTitle(Player player) {
                return "DEFAULT TITLE";
            }

            @Nullable
            @Override
            public PendingIntent createCurrentContentIntent(Player player) {
                Log.d(TAG, "************ CLICK ON CREATE INTENT");
                Intent gotoIntent = new Intent();
                gotoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                gotoIntent.setClassName(MainActivity.this, "com.videoplayer.MainActivity");
                Intent intent = new Intent();

                PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this,
                        (int) (Math.random() * 100),
                        gotoIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                return contentIntent;
            }

            @Nullable
            @Override
            public String getCurrentContentText(Player player) {
                return "DEFAULT DESCRIPTION";
            }

            @Nullable
            @Override
            public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                return getBitmap(MainActivity.this, R.drawable.landscape);
            }

            public Bitmap getBitmap(Context context, @DrawableRes int bitmapResource) {
                return ((BitmapDrawable) context.getResources().getDrawable(bitmapResource)).getBitmap();
            }
        };

        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
                MainActivity.this,
                PLAYBACK_CHANNEL_ID,
                com.brentvatne.react.R.string.exo_download_notification_channel_name,
                com.brentvatne.react.R.string.alert_description,
                PLAYBACK_NOTIFICATION_ID,
                adapter
        );

        notificationManager.setSmallIcon(R.drawable.ic_stat_onesignal_default);
        notificationManager.setUseNavigationActions(false);
        notificationManager.setUseStopAction(true);
        notificationManager.setUseChronometer(true);

        notificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                Log.d(TAG, "notification posted");
            }

            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                Log.d(TAG, "notification cancelled");
            }
        });

        notificationManager.setPlayer(event.getPlayer());

        mediaSession = new MediaSessionCompat(MainActivity.this, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        notificationManager.setMediaSessionToken(mediaSession.getSessionToken());

        Log.d(TAG, "************ BACKGROUND ENTER DONE");
    }

    @Subscribe
    public void onBackgroundExit(BackgroundLeaveMessage event) {
        Log.d(TAG, "************ BACKGROUND LEAVE");

        notificationManager.setPlayer(null);

        if(mediaSession != null) {
            mediaSession.release();
        }

        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNM.cancelAll();
    }
}
