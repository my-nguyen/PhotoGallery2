package com.bignerdranch.android.photogallery2;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 1/27/2016.
 */
public class PhotoGalleryFragment extends Fragment {
   private RecyclerView       mPhotoRecyclerView;
   private List<GalleryItem>  mItems = new ArrayList<>();
   private static final String   TAG = "PhotoGalleryFragment";

   public static PhotoGalleryFragment newInstance() {
      return new PhotoGalleryFragment();
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // retain the PhotoGalleryFragment instance across rotation
      setRetainInstance(true);
      // start the AsyncTask, which will fire up its background thread and call doInBackGround()
      new FetchItemsTask().execute();
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

   private void setupAdapter() {
      // make sure this PhotoGalleryFragment has been attached to an Activity so getActivity() is
      // not null. the reason is that AsyncTask may trigger callbacks from the background thread
      // when the Fragment is not attached to an Activity.
      if (isAdded())
         // set up an Adapter based on the list of GalleryItem's
         mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
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
      }

      @Override
      public int getItemCount() {
         return mGalleryItems.size();
      }
   }

   private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
      @Override
      // this method is run in the background thread. it returns a List of GalleryItem's from Flickr
      protected List<GalleryItem> doInBackground(Void... params) {
         return new FlickrFetchr().fetchItems();
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
