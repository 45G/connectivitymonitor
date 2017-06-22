package com.a45g.athena.connectivitymonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, ConfigService.class);
            context.startService(serviceIntent);
        }
    }
}