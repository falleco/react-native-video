package com.videoplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import androidx.annotation.DrawableRes;

import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RemoteBitmapTask extends AsyncTask<String, Void, Bitmap> {

    private Exception exception;

    private Context context;
    private PlayerNotificationManager.BitmapCallback callback;

    public RemoteBitmapTask(Context context, PlayerNotificationManager.BitmapCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        InputStream is = null;
        try {
            URLConnection con = (new URL(params[0])).openConnection();
            is = con.getInputStream();
            return BitmapFactory.decodeStream(is);
        } catch (Exception ex) {
            exception = ex;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
            callback.onBitmap(bitmap);
        } else {
            callback.onBitmap(getBitmap(this.context, R.drawable.cover));
        }
    }

    private Bitmap getBitmap(Context context, @DrawableRes int bitmapResource) {
        return ((BitmapDrawable) context.getResources().getDrawable(bitmapResource)).getBitmap();
    }
}
