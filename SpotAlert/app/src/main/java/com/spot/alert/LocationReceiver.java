package com.spot.alert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {

    OnLocationStateListener onLocationStateListener;
    public LocationReceiver(LocationReceiver.OnLocationStateListener onLocationStateListener) {
        this.onLocationStateListener = onLocationStateListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(this.onLocationStateListener!=null)
        {
            this.onLocationStateListener.onLocationStateChange();
        }

    }

    public interface OnLocationStateListener
    {
        void onLocationStateChange();
    }

}