package com.example.disen.booklisting;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchBooks extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Books>> {
    private String query = "https://www.googleapis.com/books/v1/volumes?q=";
    EditText search_author;
    String author;
    String title;
    EditText search_title;
    ListView listView;
    BooksAdapter booksAdapter;
    Button searchButton;
    LinearLayout goto_search;
    LinearLayout go_home;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    int results;
    LoaderManager loaderManager;
    ArrayList<Books> books_copy;
    TextView num_of_results;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        //if night mode is activated
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightmode = sharedPreferences.getBoolean(getString(R.string.night_key), false);
        activateNightMode(nightmode);


        //intialize variables
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        search_author = (EditText) findViewById(R.id.search_author);
        search_title = (EditText) findViewById(R.id.search_title);
        searchButton = (Button) findViewById(R.id.search_button);
        listView = (ListView) findViewById(R.id.list_books_found);
        num_of_results = (TextView) findViewById(R.id.results);
        progressbar = (ProgressBar) findViewById(R.id.progres);

        // Hide the keyboard when the app starts

        //On rotation screen
        if (savedInstanceState != null) {
            Log.e(SearchBooks.class.getSimpleName(), "not null saved instance ");
            ArrayList values = (ArrayList) savedInstanceState.getSerializable("myKey");
            results = savedInstanceState.getInt("results");
            if (values != null) {
                Log.e(SearchBooks.class.getSimpleName(), "not null values ");
                updateUI(values);
            }
        }

        //allow user to navigate to home activity
        go_home = (LinearLayout) findViewById(R.id.gohome);
        go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchBooks.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //allows the user to go to the search activity
        goto_search = (LinearLayout) findViewById(R.id.gotosearch);
        //allows user to perform a new search by navigating back to the search activity
        goto_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchBooks.this, SearchBooks.class);
                startActivity(intent);
            }
        });
        //allows user to perform a search
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
                //get author and title input by the user
                author = search_author.getText().toString().trim().replace(" ", "+");
                title = search_title.getText().toString().trim().replace(" ", "+");
                //if the user fills out at least one of these fields
                if (author.length() != 0 || title.length() != 0) {
                    //check connectivity
                    if (networkInfo != null && networkInfo.isConnected()) {
                        progressbar.setVisibility(View.VISIBLE);
                        progressbar.setIndeterminate(true);
                        loaderManager = getLoaderManager();
                        loaderManager.initLoader(1, null, SearchBooks.this);
                    } else {
                        Toast.makeText(SearchBooks.this, "Connect your device to internet", Toast.LENGTH_LONG).show();
                    }
                }
                //tell user to fill out at least one of the required fields
                else {
                    search_title.setBackgroundColor(getResources().getColor(R.color.whitepurple));
                    search_author.setBackgroundColor(getResources().getColor(R.color.whitepurple2_0));
                    Toast.makeText(SearchBooks.this, "Fill out at least one of these highlighted fields", Toast.LENGTH_LONG).show();
                }
            }

        });


    }

    public void updateUI(final ArrayList<Books> books) {
        booksAdapter = new BooksAdapter(this, books);
        books_copy = books;
        listView.setAdapter(booksAdapter);
        display_num_of_results(results);
        //add an onitemclislistener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchBooks.this, CheckOut.class);
                openVolume(books, position, intent);
            }
        });

    }

    public void display_num_of_results(int results) {
        search_title.setVisibility(View.GONE);
        search_author.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        num_of_results.setText(results + " books have been found");
    }

    //Once user clicks on a volume send him/her to the details page
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Search_settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public String buildQuery(String query) {
        StringBuilder newQuery = new StringBuilder();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String max = sharedPreferences.getString(getString(R.string.max_key), getString(R.string.forty));
        if (author != null && title != null && author.length() > 0 && title.length() > 0) {
            newQuery.append(query).append("inauthor:" + author).append("&intitle:" + title + "&orderBy=newest");
            Log.e(SearchBooks.class.getSimpleName(), "author and title");
        } else if (author != null && author.length() != 0 && title.length() == 0) {
            newQuery.append(query).append("inauthor:" + author + "&orderBy=newest&maxResults=" + max);
            Log.e(SearchBooks.class.getSimpleName(), "author only");
        } else if (author.length() == 0 && title != null && title.length() != 0) {
            newQuery.append(query).append("intitle:" + title + "&orderBy=newest&maxResults" + max);
            Log.e(SearchBooks.class.getSimpleName(), "title only " + newQuery.toString());
        } else {
            //tell user to enter something
            Log.e(SearchBooks.class.getSimpleName(), "NONE");
        }
        return newQuery.toString();
    }

    @Override
    public Loader<ArrayList<Books>> onCreateLoader(int id, Bundle args) {
        String search_query = buildQuery(query);
        return new BooksLoader(this, search_query);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Books>> loader, ArrayList<Books> data) {
        results = data.size();
        if (data != null && !data.isEmpty()) {
            progressbar.setVisibility(View.GONE);
            updateUI(data);
        } else {
            display_num_of_results(results);
            progressbar.setVisibility(View.GONE);
            Toast.makeText(SearchBooks.this, "Sorry, try another search", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(SearchBooks.class.getSimpleName(), "onDestroy: ");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Note: getValues() is a method in your ArrayAdapter subclass
        ArrayList values = books_copy;
        int num_of_books = results;
        //outState.putCharSequenceArrayList("myKey", values);
        outState.putSerializable("myKey", (ArrayList<? extends Parcelable>) values);
        outState.putInt("results", num_of_books);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(SearchBooks.class.getSimpleName(), "onStop: ");
    }

    public void activateNightMode(boolean activate) {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_search_books);
        TextView title = (TextView) findViewById(R.id.search_title);
        Button button = (Button) findViewById(R.id.search_button);
        TextView author = (TextView) findViewById(R.id.search_author);
        if (activate == true) {
            title.setTextColor(getResources().getColor(R.color.red));
            button.setTextColor(getResources().getColor(R.color.grass));
            author.setTextColor(getResources().getColor(R.color.red));
            author.setBackgroundColor(getResources().getColor(R.color.whitepurple));
            title.setBackgroundColor(getResources().getColor(R.color.whitepurple));
            button.setBackgroundColor(getResources().getColor(R.color.red));
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Books>> loader) {
        if (booksAdapter != null) {
            booksAdapter.clear();
        }

    }
}
