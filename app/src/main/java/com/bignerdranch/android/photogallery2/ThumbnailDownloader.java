package com.bignerdranch.android.photogallery2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by My on 1/30/2016.
 */
// this class is another background thread but works differently than AsyncTask in that it's more
// suited for repetitive and long-running tasks. here the task is to download 100 thumbnail pictures
// from Flickr in order to display them on the screen.
// this class expects a generic argument <T>. its user is PhotoGalleryFragment, which will need
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
public class ThumbnailDownloader<T> extends HandlerThread {
   private static final String   TAG = "ThumbnailDownloader";
   // to identify messages as download requests
   private static final int      MESSAGE_DOWNLOAD = 0;
   // the Handler responsible for queueing download requests as messages onto the ThumbnailDownloader
   // background thread. this handler will also be in charge of processing download request messages
   // when they are pulled off the queue.
   private Handler                     mRequestHandler;
   // using a download request’s identifying object of type T as a key, you can store and
   // retrieve the URL associated with a particular request. (in this case, the identifying object
   // is a PhotoHolder, so the request response can be easily routed back to the UI element where
   // the downloaded image should be placed.)
   private ConcurrentMap<T, String>    mRequestMap = new ConcurrentHashMap<>();

   public ThumbnailDownloader() {
      super(TAG);
   }

   @Override
   protected void onLooperPrepared() {
      mRequestHandler = new Handler() {
         @Override
         // this method will be called when a download message is pulled off the queue and ready to
         // be processed
         public void handleMessage(Message message) {
            // check the message type
            if (message.what == MESSAGE_DOWNLOAD) {
               // retrieve the obj value, which is of type T and serves as the identifier for the
               // request
               T target = (T)message.obj;
               Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
               // download bytes from target's URL and transform them into a bitmap
               handleRequest(target);
            }
         }
      };
   }
   // this method expects an object of type T to use as the identifier for the download and a String
   // containing the URL to download. this is the method you will have GalleryItemAdapter call in
   // its onBindViewHolder() implementation.
   public void queueThumbnail(T target, String url) {
      Log.i(TAG, "Got a URL: " + url);
      if (url == null)
         mRequestMap.remove(target);
      else {
         // the message itself does not include the URL. instead you update mRequestMap with a
         // mapping between the request identifier (PhotoHolder) and the URL for the request. later
         // you will pull the URL from mRequestMap to ensure that you are always downloading the
         // most recently requested URL for a given PhotoHolder instance. (this is important because
         // ViewHolder objects in RecyclerViews are recycled and reused.)
         mRequestMap.put(target, url);
         // A message is an instance of Message and contains several fields. three are relevant to
         // your implementation:
         // what: a user-defined int that describes the message
         // obj: a user-specified object to be sent with the message
         // target: the Handler that will handle the message
         // obtain a message directly from mRequestHandler, which automatically sets the new Message
         // object’s target field to mRequestHandler. this means mRequestHandler will be in charge
         // of processing the message when it is pulled off the message queue. the message’s what
         // field is set to MESSAGE_DOWNLOAD. its obj field is set to the T target value (a
         // PhotoHolder in this case) that is passed to queueThumbnail().
         mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
      }
   }

   // this helper method uses FlickrFetchr to download bytes from the URL and then turns these bytes
   // into a bitmap
   private void handleRequest(final T target) {
      try {
         final String url = mRequestMap.get(target);
         // check for the existence of a URL
         if (url != null) {
            // pass the url to a new instance of FlickrFetch, and call getUrlBytes()
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            // use BitmapFactory to construct a bitmap with the array of bytes returned from getUrlBytes()
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");
         }
      }
      catch (IOException ioe) {
         Log.e(TAG, "Error downloading image", ioe);
      }
   }
}
