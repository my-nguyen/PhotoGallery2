package com.bignerdranch.android.photogallery2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
      Log.i(TAG, "Received an intent: " + intent);
   }
}
