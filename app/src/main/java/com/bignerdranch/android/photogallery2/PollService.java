package com.bignerdranch.android.photogallery2;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
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
   // 60 seconds
   private static final int      POLL_INTERVAL = 1000 * 60;

   public static Intent newIntent(Context context) {
      return new Intent(context, PollService.class);
   }

   public static void setServiceAlarm(Context context, boolean on) {
      Intent intent = PollService.newIntent(context);
      // construct a PendingIntent that starts a PollService via
      // getService(Context, request-code, Intent, flags);
      PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
      AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
      if (on)
         // set alarm via setInexactRepeating(time-basis, start-time, repeat-interval, PendingIntent);
         alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
               SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
      else {
         // cancel the alarm and the PendingIntent
         alarmManager.cancel(pendingIntent);
         pendingIntent.cancel();
      }
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
            Log.i(TAG, "Got " + (resultId.equals(lastResultId) ? "an old" : "a new") + " result");
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
