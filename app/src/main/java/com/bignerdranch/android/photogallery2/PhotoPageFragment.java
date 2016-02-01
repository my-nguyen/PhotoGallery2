package com.bignerdranch.android.photogallery2;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by My on 2/1/2016.
 */
// this class serves to display the WebView
public class PhotoPageFragment extends VisibleFragment {
   private static final String   ARG_URI = "photo_page_url";
   private Uri          mUri;
   private WebView      mWebView;
   private ProgressBar  mProgressBar;

   public static PhotoPageFragment newInstance(Uri uri) {
      Bundle args = new Bundle();
      args.putParcelable(ARG_URI, uri);
      PhotoPageFragment fragment = new PhotoPageFragment();
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      mUri = getArguments().getParcelable(ARG_URI);
   }

   @Override
   @SuppressLint("SetJavaScriptEnabled")
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_photo_page, container, false);
      mProgressBar = (ProgressBar)view.findViewById(R.id.fragment_photo_gallery_progress_bar);
      // WebChromeClient reports in range 0-100
      mProgressBar.setMax(100);
      mWebView = (WebView)view.findViewById(R.id.fragment_photo_page_web_view);
      // turn on Javascript, as required by Flickr
      mWebView.getSettings().setJavaScriptEnabled(true);
      mWebView.setWebChromeClient(new WebChromeClient() {
         public void onProgressChanged(WebView webView, int newProgress) {
            // newProgress is between 0 and 100
            if (newProgress == 100)
               // page is donne loading, so hide the ProgressBar
               mProgressBar.setVisibility(View.GONE);
            else {
               mProgressBar.setVisibility(View.VISIBLE);
               mProgressBar.setProgress(newProgress);
            }
         }
         public void onReceivedTitle(WebView webView, String title) {
            AppCompatActivity activity = (AppCompatActivity)getActivity();
            activity.getSupportActionBar().setSubtitle(title);
         }
      });
      mWebView.setWebViewClient(new WebViewClient() {
         // this method determines what will happen when a new URL is loaded in the WebView.
         // the default implementation fires an implicit intent with the URL. Flickr would redirect
         // you to the mobile version of the website. with the default WebViewClient, you are
         // immediately sent to the user’s default web browser.
         // to load it in this WebView, just override the default implementation and return false.
         public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            return false;
         }
      });
      // load the URI after configuring the WebView
      mWebView.loadUrl(mUri.toString());
      return view;
   }
}
