package com.bignerdranch.android.photogallery2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 1/27/2016.
 */
public class PhotoGalleryFragment extends Fragment {
   private RecyclerView                      mPhotoRecyclerView;
   private List<GalleryItem>                 mItems = new ArrayList<>();
   // the generic argument specifies the type of the object that will be used as the identifier for
   // your download. in this case, the PhotoHolder makes for a convenient identifier as it is also
   // the target where the downloaded images will eventually go.
   private ThumbnailDownloader<PhotoHolder>  mThumbnailDownloader;
   private static final String   TAG = "PhotoGalleryFragment";

   public static PhotoGalleryFragment newInstance() {
      return new PhotoGalleryFragment();
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // retain the PhotoGalleryFragment instance across rotation
      setRetainInstance(true);
      // turn on toolbar menu by registering the fragment to receive menu callbacks
      setHasOptionsMenu(true);
      // start the AsyncTask, which will fire up its background thread and call doInBackGround()
      updateItems();

      // by default, the Handler will attach itself to the Looper of the current thread. since this
      // Handler is created in onCreate(), it'll be attached to the main thread's Looper.
      Handler responseHandler = new Handler();
      // create a ThumbnailDownloader thread, passing a Handler attached to main thread to
      // ThumbnailDownloader thread
      mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
      // implement the ThumbnailDownloadListener interface
      mThumbnailDownloader.setThumbnailDownloadListener(
            new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
               @Override
               // this method does the UI work with the downloaded bitmap
               public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                  Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                  // set the Drawable of the originally requested PhotoHolder to the newly downloaded
                  // Bitmap
                  target.bindDrawable(drawable);
               }
            }
      );
      // start the ThumbnailDownloader thread
      mThumbnailDownloader.start();
      // call getLooper() after start() to ensure the thread's guts are ready before proceeding
      mThumbnailDownloader.getLooper();
      Log.i(TAG, "Background thread started");
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
      mPhotoRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_photo_gallery_recycler_view);
      // lay out items in a grid of 3 columns
      mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
      // every time a new RecyclerView is created, it's reconfigured with an appropriate adapter
      setupAdapter();

      return view;
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      super.onCreateOptionsMenu(menu, inflater);
      // inflate the menu XML just created
      inflater.inflate(R.menu.fragment_photo_gallery, menu);

      // pull the MenuItem representing the search box from the menu
      MenuItem searchItem = menu.findItem(R.id.menu_item_search);
      // pull the SearchView object from searchItem using the getActionView() method.
      final SearchView searchView = (SearchView)searchItem.getActionView();
      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
         @Override
         // this callback is executed when the user submits a query. the query the user submitted is
         // passed as input. this is where you will launch a FetchItemsTask to query for new results
         public boolean onQueryTextSubmit(String query) {
            Log.d(TAG, "QueryTextSubmit: " + query);
            // update the stored query whenever the user submits a new query
            QueryPreferences.setStoredQuery(getContext(), query);
            updateItems();
            // returning true signifies to the system that the search request has been handled
            return true;
         }

         @Override
         // this callback is executed any time every time a single character changes in the
         // SearchView text box. do nothing here except log the input string.
         public boolean onQueryTextChange(String newText) {
            Log.d(TAG, "QueryTextChange: " + newText);
            return false;
         }
      });

      searchView.setOnSearchClickListener(new View.OnClickListener() {
         @Override
         // this method pre-populates the search text box with the saved query when the user presses
         // on the search icon to expand the SearchView
         public void onClick(View v) {
            String query = QueryPreferences.getStoredQuery(getActivity());
            searchView.setQuery(query, false);
         }
      });

      // check whether the alarm is on and change the text of menu_item_toggle_polling to show the
      // appropriate label to the user
      MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
      toggleItem.setTitle(PollService.isServiceAlarmOn(getActivity()) ? R.string.stop_polling : R.string.start_polling);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         // the user selects the Clear Search item from the overflow menu
         case R.id.menu_item_clear:
            // clear the stored query whenever
            QueryPreferences.setStoredQuery(getActivity(), null);
            // ensure the images displayed in the RecyclerView reflect the most recent search query
            updateItems();
            return true;
         case R.id.menu_item_toggle_polling:
            // toggle the alarm on and off
            boolean startAlarm = !PollService.isServiceAlarmOn(getActivity());
            PollService.setServiceAlarm(getActivity(), startAlarm);
            // tell PhotoGalleryActivity to update its toolbar options menu.
            getActivity().invalidateOptionsMenu();
            return true;
         default:
            return onOptionsItemSelected(item);
      }
   }
   @Override
   public void onDestroyView() {
      super.onDestroyView();
      // clean out the downloader when the view is destroyed
      mThumbnailDownloader.clearQueue();
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      // it's critical to call quit() to terminate the thread; otherwise the thread never dies.
      mThumbnailDownloader.quit();
      Log.i(TAG, "Background thread destroyed");
   }

   private void setupAdapter() {
      // make sure this PhotoGalleryFragment has been attached to an Activity so getActivity() is
      // not null. the reason is that AsyncTask may trigger callbacks from the background thread
      // when the Fragment is not attached to an Activity.
      if (isAdded())
         // set up an Adapter based on the list of GalleryItem's
         mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
   }

   private void updateItems() {
      // use the stored query and not the previously hardcoded string
      String query = QueryPreferences.getStoredQuery(getActivity());
      new FetchItemsTask(query).execute();
   }

   private class PhotoHolder extends RecyclerView.ViewHolder {
      private ImageView mItemImageView;

      public PhotoHolder(View view) {
         super(view);
         mItemImageView = (ImageView)view.findViewById(R.id.fragment_photo_gallery_image_view);
      }

      public void bindDrawable(Drawable drawable) {
         mItemImageView.setImageDrawable(drawable);
      }
   }

   private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
      private List<GalleryItem>  mGalleryItems;

      public PhotoAdapter(List<GalleryItem> galleryItems) {
         mGalleryItems = galleryItems;
      }

      @Override
      public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
         LayoutInflater inflater = LayoutInflater.from(getActivity());
         View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
         return new PhotoHolder(view);
      }

      @Override
      public void onBindViewHolder(PhotoHolder holder, int position) {
         GalleryItem item = mGalleryItems.get(position);
         Drawable drawable = getResources().getDrawable(R.drawable.bill_up_close);
         holder.bindDrawable(drawable);
         // call the thread’s queueThumbnail() method and pass in the target PhotoHolder where the
         // image will ultimately be placed and the GalleryItem’s URL to download from.
         mThumbnailDownloader.queueThumbnail(holder, item.getUrl());
      }

      @Override
      public int getItemCount() {
         return mGalleryItems.size();
      }
   }

   private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
      private String mQuery;

      public FetchItemsTask(String query) {
         mQuery = query;
      }

      @Override
      // this method is run in the background thread. it returns a List of GalleryItem's from Flickr
      protected List<GalleryItem> doInBackground(Void... params) {
         FlickrFetchr fetcher = new FlickrFetchr();
         return mQuery == null ? fetcher.fetchRecentPhotos() : fetcher.searchPhotos(mQuery);
      }

      @Override
      // this method is run on the main thread
      protected void onPostExecute(List<GalleryItem> items) {
         // save the list of GalleryItem's fetched by doInBackGround()
         mItems = items;
         // set up Adapter with this new set of data
         setupAdapter();
      }
   }
}
