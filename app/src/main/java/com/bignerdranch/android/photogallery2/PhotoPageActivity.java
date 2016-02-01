package com.bignerdranch.android.photogallery2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by My on 2/1/2016.
 */
// this class serves as the host for PhotoPageFragment, the WebView fragment
public class PhotoPageActivity extends SingleFragmentActivity {
   public static Intent newIntent(Context context, Uri photoPageUri) {
      Intent intent = new Intent(context, PhotoPageActivity.class);
      intent.setData(photoPageUri);
      return intent;
   }

   @Override
   protected Fragment createFragment() {
      return PhotoPageFragment.newInstance(getIntent().getData());
   }
}
