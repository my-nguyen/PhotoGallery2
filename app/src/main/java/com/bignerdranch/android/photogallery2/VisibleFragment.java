package com.bignerdranch.android.photogallery2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
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
      // register this dynamic broadcast receiver in code and not in the manifest
      getActivity().registerReceiver(mOnShowNotification, filter);
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
         Toast.makeText(getActivity(), "Got a broadcast: " + intent.getAction(), Toast.LENGTH_LONG).show();
      }
   };
}
