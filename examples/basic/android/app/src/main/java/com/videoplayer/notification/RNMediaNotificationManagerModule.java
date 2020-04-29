package com.videoplayer.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.DrawableRes;

import com.brentvatne.exoplayer.events.BackgroundEnterMessage;
import com.brentvatne.exoplayer.events.BackgroundLeaveMessage;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.views.imagehelper.ImageSource;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.videoplayer.MainActivity;
import com.videoplayer.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RNMediaNotificationManagerModule extends ReactContextBaseJavaModule {

    public static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    public static final int PLAYBACK_NOTIFICATION_ID = 1;
    public static final String MEDIA_SESSION_TAG = "audio_demo";

    private static final String TAG = "MainActivity";

    private PlayerNotificationManager notificationManager;
    private MediaSessionCompat mediaSession;


    private String title;

    private String description;

    private String cover;


    private final ReactApplicationContext reactContext;

    public RNMediaNotificationManagerModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public void initialize() {
        super.initialize();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nonnull
    @Override
    public String getName() {
        return "RNMediaNotificationManager";
    }

    @ReactMethod
    public void metadata(String title, String description, ReadableMap cover) {
        this.title = title;
        this.description = description;
        Log.d(TAG, "************ COVER");
        this.cover = cover.getString("uri");
    }

    @Subscribe
    public void onBackgroundEnter(BackgroundEnterMessage event) {
        Log.d(TAG, "************ BACKGROUND ENTER");

        PlayerNotificationManager.MediaDescriptionAdapter adapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
            @Override
            public String getCurrentContentTitle(Player player) {
                if(title != null) {
                    return title;
                }

                return "DEFAULT TITLE";
            }

            @Nullable
            @Override
            public PendingIntent createCurrentContentIntent(Player player) {
                Log.d(TAG, "************ CLICK ON CREATE INTENT");
                Intent gotoIntent = new Intent();
                gotoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                gotoIntent.setClassName(reactContext, "com.videoplayer.MainActivity");
                Intent intent = new Intent();

                PendingIntent contentIntent = PendingIntent.getActivity(reactContext,
                        (int) (Math.random() * 100),
                        gotoIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                return contentIntent;
            }

            @Nullable
            @Override
            public String getCurrentContentText(Player player) {
                if(description != null) {
                    return description;
                }
                return "DEFAULT DESCRIPTION";
            }

            @Nullable
            @Override
            public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                try {

                    return MediaStore.Images.Media.getBitmap(reactContext.getContentResolver(), Uri.parse(cover));
                } catch (IOException e) {
                    e.printStackTrace();
                    return getBitmap(reactContext, R.drawable.cover);
                }
            }

            public Bitmap getBitmap(Context context, @DrawableRes int bitmapResource) {
                return ((BitmapDrawable) context.getResources().getDrawable(bitmapResource)).getBitmap();
            }
        };

        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
                reactContext,
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

        mediaSession = new MediaSessionCompat(reactContext, MEDIA_SESSION_TAG);
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
    }

}
