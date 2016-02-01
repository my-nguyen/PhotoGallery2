package com.bignerdranch.android.photogallery2;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by My on 1/31/2016.
 */
// The result receiver is a special receiver that runs after all the other recipients of your
// ordered broadcast intent. here, the broadcast intent will often be sent right before PollService
// dies. that means that your broadcast receiver might be dead, too. thus, your final broadcast
// receiver will need to be a standalone receiver, and you will need to enforce that it runs after
// the dynamically registered receiver by different means.
public class NotificationReceiver extends BroadcastReceiver {
   private static final String   TAG = "NotificationReceiver";

   @Override
   public void onReceive(Context context, Intent intent) {
      Log.i(TAG, "received result: " + getResultCode());
      // no foreground activity cancelled the broadcast
      if (getResultCode() == Activity.RESULT_OK) {
         int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
         Notification notification = (Notification)intent.getParcelableExtra(PollService.NOTIFICATION);
         NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
         notificationManager.notify(requestCode, notification);
      }
   }
}
