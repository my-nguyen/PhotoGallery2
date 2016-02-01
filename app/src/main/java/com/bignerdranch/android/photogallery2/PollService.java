package com.bignerdranch.android.photogallery2;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by My on 1/30/2016.
 */
// this class represents a service used by the app to poll for search results. it acts like an
// activity and responds to intents. a service's intents are called commands. each command is an
// instruction to the service to do something.
public class PollService extends IntentService {
   private static final String   TAG = "PollService";
   private static final int      POLL_INTERVAL = 1000 * 60; // 60 seconds
   // private static final long     POLL_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

   public static Intent newIntent(Context context) {
      return new Intent(context, PollService.class);
   }

   // this method turns an alarm ON or OFF
   public static void setServiceAlarm(Context context, boolean on) {
      Intent intent = PollService.newIntent(context);
      // construct a PendingIntent that starts a PollService via
      // getService(Context, request-code, Intent, flags);
      PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
      AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
      if (on)
         // set alarm via setInexactRepeating(time-basis, start-time, repeat-interval, PendingIntent);
         // for time basis, there are 2 main options: AlarmManager.ELAPSED_REALTIME and AlarmManager.RTC.
         // (1) AlarmManager.ELAPSED_REALTIME uses the amount of time that has passed since the last
         // boot of the device (including sleep time) as the basis for interval calculations.
         // ELAPSED_REALTIME is the best choice for your alarm in PhotoGallery because it is based
         // on the relative passage of time and thus does not depend on wall clock time. (also, the
         // documentation recommends you use ELAPSED_REALTIME instead of RTC if at all possible.)
         // (2) AlarmManager.RTC uses “wall clock time” in terms of UTC. UTC should only be used for
         // wall-clock basis alarms. however, UTC does not respect locale, whereas the user’s idea
         // of wall-clock time includes locale. wall-clock basis alarms should respect locale
         // somehow. this means you must implement your own locale handling in conjunction with
         // using the RTC time basis if you want to set a wall-clock time alarm. otherwise, use
         // ELAPSED_REALTIME as the time basis.
      alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
               SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
      else {
         // cancel the alarm and the PendingIntent
         alarmManager.cancel(pendingIntent);
         pendingIntent.cancel();
      }

      // write to SharedPreferences when the alarm is set
      QueryPreferences.setAlarmOn(context, on);
   }

   // this method uses PendingIntent.FLAG_NO_CREATE to tell whether the alarm is on or not.
   public static boolean isServiceAlarmOn(Context context) {
      Intent intent = PollService.newIntent(context);
      PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
      return pendingIntent != null;
   }

   public PollService() {
      super(TAG);
   }

   @Override
   protected void onHandleIntent(Intent intent) {
      if (isNetworkAvailableAndConnected()) {
         // pull out the current query and the last result ID from the default SharedPreferences.
         String query = QueryPreferences.getStoredQuery(this);
         String lastResultId = QueryPreferences.getLastResultId(this);
         // fetch the latest result set with FlickrFetchr.
         FlickrFetchr fetcher = new FlickrFetchr();
         List<GalleryItem> items = query == null ? fetcher.fetchRecentPhotos() : fetcher.searchPhotos(query);
         if (items.size() != 0) {
            // if there are results, grab the first one.
            String resultId = items.get(0).getId();
            // check to see whether it is different from the last result ID.
            if (resultId.equals(lastResultId))
               Log.i(TAG, "Got an old result");
            else {
               Log.i(TAG, "Got a new result");
               Resources resources = getResources();
               Intent intent1 = PhotoGalleryActivity.newIntent(this);
               PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
               Notification notification = new NotificationCompat.Builder(this)
                     // set ticker text
                     .setTicker(resources.getString(R.string.new_pictures_title))
                     // set small icon which comes from Android
                     .setSmallIcon(android.R.drawable.ic_menu_report_image)
                     // set title
                     .setContentTitle(resources.getString(R.string.new_pictures_title))
                     // set text
                     .setContentText(resources.getString(R.string.new_pictures_text))
                     // specify what happens when the user presses Notification: the PendingIntent
                     // will be fired
                     .setContentIntent(pendingIntent)
                     // the notification will also be deleted from the notification drawer when the
                     // user presses it
                     .setAutoCancel(true)
                     .build();
               // get an instance of NotificationManagerCompat from the current context
               NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
               // post the notification
               notificationManager.notify(0, notification);
            }
            // store the first result back in SharedPreferences.
            QueryPreferences.setLastResultId(this, resultId);
         }
      }
   }

   private boolean isNetworkAvailableAndConnected() {
      ConnectivityManager connectivity = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
      boolean isNetworkAvailable = connectivity.getActiveNetworkInfo() != null;
      boolean isNetworkConnected = connectivity.getActiveNetworkInfo().isConnected();
      return isNetworkAvailable && isNetworkConnected;
   }
}
