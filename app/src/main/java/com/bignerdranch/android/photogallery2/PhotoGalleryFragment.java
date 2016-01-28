package com.bignerdranch.android.photogallery2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * Created by My on 1/27/2016.
 */
public class PhotoGalleryFragment extends Fragment {
   private RecyclerView    mPhotoRecyclerView;
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

      return view;
   }

   private class FetchItemsTask extends AsyncTask<Void, Void, Void> {
      @Override
      protected Void doInBackground(Void... params) {
         new FlickrFetchr().fetchItems();
         return null;
      }
   }
}
