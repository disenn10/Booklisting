package com.example.disen.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by disen on 7/6/2017.
 */

public class BooksLoader extends AsyncTaskLoader<ArrayList<Books>> {
    String queryUrl;

    public BooksLoader(Context context, String query) {
        super(context);
        queryUrl = query;
    }

    @Override
    public ArrayList<Books> loadInBackground() {
        if (queryUrl == null) {
            Log.e(BooksLoader.class.getSimpleName(), "query is null ");
            return null;
        }
        Log.e(BooksLoader.class.getSimpleName(), "loadInBackground: ");
        ArrayList<Books> books = Utils.fetchData(queryUrl);
        return books;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
