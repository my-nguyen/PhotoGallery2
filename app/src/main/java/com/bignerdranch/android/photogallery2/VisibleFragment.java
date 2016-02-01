package com.bignerdranch.android.photogallery2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by My on 1/31/2016.
 */
// this class is a dynamic broadcast receiver for PollService.ACTION_SHOW_NOTIFICATION broadcast
// intent. it's a generic fragment that hides foreground notifications.
public abstract class VisibleFragment extends Fragment {
   private static final String TAG = "VisibleFragment";

   @Override
   public void onStart() {
      super.onStart();
      // create a new IntentFilter, which is equivalent to the following in the manifest:
      // <intent-filter> <action android:name="com.bignerdranch.android.photogallery.SHOW_NOTIFICATION" /> </intent-filter>
      IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
      // register this dynamic broadcast receiver in code and not in the manifest. this receiver
      // only listens to permission PollService.PERM_PRIVATE
      getActivity().registerReceiver(mOnShowNotification, filter, PollService.PERM_PRIVATE, null);
   }

   @Override
   public void onStop() {
      super.onStop();
      // clean up by unregistering receiver in the lifecycle shutdown method corresponding to
      // onStart() where the receiver was registered
      getActivity().unregisterReceiver(mOnShowNotification);
   }

   private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         // if we receive this, we're visible, so cancel the notification
         Log.i(TAG, "canceling notification");
         // only result code is necessary to signal here
         setResultCode(Activity.RESULT_CANCELED);
      }
   };
}
