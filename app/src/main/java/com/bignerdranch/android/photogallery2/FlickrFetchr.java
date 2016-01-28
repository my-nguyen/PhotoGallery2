package com.bignerdranch.android.photogallery2;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by My on 1/27/2016.
 */
public class FlickrFetchr {
   private static final String   TAG = "FlickrFetchr";
   private static final String   API_KEY = "0a02556fb3e435d2bc1ea50f79f594b9";

   // this method fetches raw data from a URL and returns it as an array of bytes
   public byte[] getUrlBytes(String urlSpec) throws IOException {
      // create a URL object from a string, like https://www.bignerdranch.com
      URL url = new URL(urlSpec);
      // create a connection object pointed at the URL
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      try {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         // the connection object will not connect to the endpoint until getInputStream()
         InputStream in = connection.getInputStream();
         if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);

         int bytesRead = 0;
         byte[] buffer = new byte[1024];
         // read() bytes from InputStream repeatedly until the connection runs out of data
         while ((bytesRead = in.read(buffer)) > 0)
            out.write(buffer, 0, bytesRead);

         out.close();
         return out.toByteArray();
      }
      finally {
         connection.disconnect();
      }
   }

   // this method converts an array of bytes (fetched by getUrlBytes()) to a String
   public String getUrlString(String urlSpec) throws IOException {
      return new String(getUrlBytes(urlSpec));
   }

   public void fetchItems() {
      try {
         // use Uri.Builder to build the complete URL for the Flickr API request. values are added
         // for the parameters method, api_key, format, nojsoncallback, and extras. value url_s for
         // extras tells Flickr to include the URL for the small version of the picture if it's
         // available
         String url = Uri.parse("https://api.flickr.com/services/rest")
               .buildUpon()
               .appendQueryParameter("method", "flickr.photos.getRecent")
               .appendQueryParameter("api_key", API_KEY)
               .appendQueryParameter("format", "json")
               .appendQueryParameter("nojsoncallback", "1")
               .appendQueryParameter("extras", "url_s")
               .build().toString();
         String jsonString = getUrlString(url);
         Log.i(TAG, "Received JSON: " + jsonString);
      }
      catch (IOException ioe) {
         Log.e(TAG, "Failed to fetch items", ioe);
      }
   }
}