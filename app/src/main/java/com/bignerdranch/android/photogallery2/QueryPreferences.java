package com.bignerdranch.android.photogallery2;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by My on 1/30/2016.
 */
// this class serves as a convenient interface for reading and writing the query to and from shared
// preferences
public class QueryPreferences {
   // used as the key for the query preference.
   private static final String PREF_SEARCH_QUERY = "searchQuery";
   private static final String PREF_LAST_RESULT_ID = "lastResultId";

   // this method returns the query value stored in shared preferences by first acquiring the
   // default SharedPreferences for the given context.
   public static String getStoredQuery(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY, null);
   }

   // this method method writes the input query to the default shared preferences for the given context
   public static void setStoredQuery(Context context, String query) {
      // SharedPreferences.edit() returns an instance of SharedPreferences.Editor, which is the
      // class used to stash values in SharedPreferences. it allows you to group sets of changes
      // together in transactions, much like you do with FragmentTransaction. once you are done
      // making all of your changes, call apply() on the editor to make them visible to other users
      // of that SharedPreferences file. the apply() method makes the change in memory immediately
      // and then does the actual file writing on a background thread.
      PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SEARCH_QUERY, query).apply();
   }

   // this method is used by PollService to query for the last fetched result
   public static String getLastResultId(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_RESULT_ID, null);
   }

   public static void setLastResultId(Context context, String lastResultId) {
      PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_LAST_RESULT_ID, lastResultId).apply();
   }
}
