package com.bignerdranch.android.photogallery2;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 1/27/2016.
 */
public class FlickrFetchr {
   private static final String   TAG = "FlickrFetchr";
   private static final String   API_KEY = "0a02556fb3e435d2bc1ea50f79f594b9";
   private static final String   FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
   private static final String   SEARCH_METHOD = "flickr.photos.search";
   // use Uri.Builder to build the complete URL for the Flickr API request. values are added for the
   // parameters api_key, format, nojsoncallback, and extras. for extras, value url_s tells Flickr
   // to include the URL for the small version of the picture if it's available
   private static final Uri      ENDPOINT = Uri
         .parse("https://api.flickr.com/services/rest")
         .buildUpon()
         .appendQueryParameter("api_key", API_KEY)
         .appendQueryParameter("format", "json")
         .appendQueryParameter("nojsoncallback", "1")
         .appendQueryParameter("extras", "url_s")
         .build();

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

   public List<GalleryItem> fetchRecentPhotos() {
      String url = buildUrl(FETCH_RECENTS_METHOD, null);
      return downloadGalleryItems(url);
   }

   public List<GalleryItem> searchPhotos(String query) {
      String url = buildUrl(SEARCH_METHOD, query);
      return downloadGalleryItems(url);
   }

   private List<GalleryItem> downloadGalleryItems(String url) {
      List<GalleryItem> items = new ArrayList<>();
      try {
         String jsonString = getUrlString(url);
         Log.i(TAG, "Received JSON: " + jsonString);
         // parse jsonString into an object hierarchy that maps to the original JSON text
         JSONObject jsonBody = new JSONObject(jsonString);
         parseItems(items, jsonBody);
      }
      catch (JSONException je) {
         Log.e(TAG, "Failed to parse JSON", je);
      }
      catch (IOException ioe) {
         Log.e(TAG, "Failed to fetch items", ioe);
      }

      return items;
   }

   // this method builds a URL based on its method ("getRecent" or "search") and query
   private String buildUrl(String method, String query) {
      Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", method);
      if (method.equals(SEARCH_METHOD))
         uriBuilder.appendQueryParameter("text", query);
      return uriBuilder.build().toString();
   }

   private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException {
      // the top-level JSONObject maps to the outermost curly braces in the JSON string. this top-
      // level object contains a nested JSONObject named photos.
      JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
      // within the nested JSONObject is a JSONArray named photo
      JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
      // this JSONArray contains a collection of JSONObject's, each representing metadata for one photo
      for (int i = 0; i < photoJsonArray.length(); i++) {
         JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
         // create a GalleryItem for each photo
         GalleryItem item = new GalleryItem();
         item.setId(photoJsonObject.getString("id"));
         item.setCaption(photoJsonObject.getString("title"));
         // ignore images that do not have an image url
         if (photoJsonObject.has("url_s")) {
            item.setUrl(photoJsonObject.getString("url_s"));
            // read in the owner attribute
            item.setOwner(photoJsonObject.getString("owner"));
            // add GalleryItem to a list
            items.add(item);
         }
      }
   }
}
