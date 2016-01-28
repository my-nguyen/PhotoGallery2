package com.bignerdranch.android.photogallery2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by My on 1/27/2016.
 */
public class FlickrFetchr {
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
}
