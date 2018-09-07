package com.example.disen.booklisting;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class BooksListview extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Books>> {
    ListView listView;
    BooksAdapter booksAdapter;
    private static String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_listview);
        Intent intent = getIntent();
        query = intent.getStringExtra("query");
        listView = (ListView) findViewById(R.id.books_listview);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, this);

    }

    public void updateUI(final ArrayList<Books> books) {
        booksAdapter = new BooksAdapter(this, books);
        listView.setAdapter(booksAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BooksListview.this, CheckOut.class);
                openVolume(books, position, intent);
            }
        });
    }

    public void openVolume(final ArrayList<Books> books, int position, Intent intent) {
        Books selected_book = books.get(position);
        intent.putExtra("author", selected_book.getAuthor());
        intent.putExtra("title", selected_book.getTitle());
        intent.putExtra("image", selected_book.getImage());
        intent.putExtra("ratings", selected_book.getRatings());
        intent.putExtra("description", selected_book.getDescription());
        intent.putExtra("weblink", selected_book.getWeblink());
        startActivity(intent);

    }

    @Override
    public Loader<ArrayList<Books>> onCreateLoader(int id, Bundle args) {
        return new BooksLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Books>> loader, ArrayList<Books> data) {
        if (data != null && !data.isEmpty()) {
            updateUI(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Books>> loader) {
        if (booksAdapter != null && !booksAdapter.isEmpty()) {
            booksAdapter.clear();
        }
    }
}
