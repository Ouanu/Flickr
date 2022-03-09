package com.moment.photogallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT_ID = "lastResultId";
    private static final String PREF_IS_POLLING = "isPolling";

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

    public String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_RESULT_ID, "");
    }

    public void setLastResultId(Context context, String lastResultId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_LAST_RESULT_ID, lastResultId);
    }

    public Boolean isPolling(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_POLLING, false);
    }

    public void setPolling(Context context, Boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_IS_POLLING, isOn).apply();
    }
}
