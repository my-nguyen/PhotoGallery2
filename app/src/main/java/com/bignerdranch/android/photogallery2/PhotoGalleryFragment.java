package com.bignerdranch.android.photogallery2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by My on 1/27/2016.
 */
public class PhotoGalleryFragment extends Fragment {
   private RecyclerView    mPhotoRecyclerView;

   public static PhotoGalleryFragment newInstance() {
      return new PhotoGalleryFragment();
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // retain the PhotoGalleryFragment instance across rotation
      setRetainInstance(true);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
      mPhotoRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_photo_gallery_recycler_view);
      // lay out items in a grid of 3 columns
      mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

      return view;
   }
}
