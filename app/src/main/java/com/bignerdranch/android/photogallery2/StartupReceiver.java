package com.bignerdranch.android.photogallery2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by My on 1/31/2016.
 */
// this class is a standalone receiver, which can be activated even if the app process is dead. a
// standalone receiver is a broadcast receiver that's declared in the manifest. a broadcast receiver
// is a component that receives intents, just like a service or activity. when an intent is issued
// to StartupReceiver, its onReceive() method will be called.
public class StartupReceiver extends BroadcastReceiver {
   private static final String   TAG = "StartupReceiver";

   @Override
   public void onReceive(Context context, Intent intent) {
      Log.i(TAG, "Received broadcast intent: " + intent.getAction());
      // use SharedPreferences to turn the alarm on at boot
      boolean isOn = QueryPreferences.isAlarmOn(context);
      PollService.setServiceAlarm(context, isOn);
   }
}
