package com.moment.photogallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";

    private volatile static QueryPreferences INSTANCE = null;

    public QueryPreferences() {
    }

    public static QueryPreferences getINSTANCE() {
        if (INSTANCE == null) {
            synchronized (QueryPreferences.class) {
                if (INSTANCE == null) {
                    return new QueryPreferences();
                }
            }
        }
        return INSTANCE;
    }

    public String getStoredQuery(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_SEARCH_QUERY, "");
    }

    public void setStoredQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }
}
