package com.bignerdranch.android.photogallery2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.List;

/**
 * Created by My on 1/30/2016.
 */
// this class represents a service used by the app to poll for search results. it acts like an
// activity and responds to intents. a service's intents are called commands. each command is an
// instruction to the service to do something.
public class PollService extends IntentService {
   private static final String TAG = "PollService";

   public static Intent newIntent(Context context) {
      return new Intent(context, PollService.class);
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
