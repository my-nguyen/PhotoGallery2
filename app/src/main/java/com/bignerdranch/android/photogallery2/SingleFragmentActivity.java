package com.bignerdranch.android.photogallery2;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by My on 1/25/2016.
 */
// extends from AppCompatActivity (as opposed to FragmentActivity previously) to have a toolbar
public abstract class SingleFragmentActivity extends AppCompatActivity {
   protected abstract Fragment createFragment();

   @LayoutRes
   protected int getLayoutResId() {
      return R.layout.activity_fragment;
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(getLayoutResId());

      // FragmentManager maintains a back stack of fragment transactions that you can navigate
      FragmentManager manager = getSupportFragmentManager();
      // ask the FragmentManager for the fragment with a container view ID of R.id.fragment_container
      Fragment fragment = manager.findFragmentById(R.id.fragment_container);
      // if there is no fragment with the given container view ID
      if (fragment == null) {
         fragment = createFragment();
         // create a new fragment transaction, include one add operation in it, and then commit it
         manager.beginTransaction().add(R.id.fragment_container, fragment).commit();
      }
   }
}
