package com.bignerdranch.android.photogallery2;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by My on 1/30/2016.
 */
// this class is another background thread but works differently than AsyncTask in that it's more
// suited for repetitive and long-running tasks. here the task is to download 100 thumbnail pictures
// from Flickr in order to display them on the screen.
// this class expects a generic argument <Type>. its user is PhotoGalleryFragment, which will need
// to use some object to identify each download and to determine which UI element to update with the
// image once it is downloaded.
// from stackoverflow.com:
// 1. Use AsyncTask if you need to run a short task communicating with the UI thread
// 2. Use a Thread and Handler to run longer tasks that requires communication between the worker
// thread and the main thread(caller thread)
// 3. Use Thread, Handler and Looper (or HandlerThread, which is class for starting a thread that
// already has a Looper) for longer tasks that require communication between the worker thread and
// the caller thread(not the main thread).
// 4. Use IntentService for longer task that does not requires user interaction and needs only one
// worker thread.
public class ThumbnailDownloader<Type> extends HandlerThread {
   private static final String TAG = "ThumbnailDownloader";

   public ThumbnailDownloader() {
      super(TAG);
   }

   // this method expects an object of type T to use as the identifier for the download and a String
   // containing the URL to download. this is the method you will have GalleryItemAdapter call in
   // its onBindViewHolder() implementation.
   public void queueThumbnail(Type target, String url) {
      Log.i(TAG, "Got a URL: " + url);
   }
}
