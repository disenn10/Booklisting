package com.example.disen.booklisting;

import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Books>> {
    Gallery gallery_recommended;
    Gallery magazines;
    Gallery sports_gallery;
    BooksAdapter booksAdapter;
    ProgressBar progressbar;
    String current_query;
    TextView internet;
    LoaderManager loaderManager;
    LinearLayout recommended;
    LinearLayout magazine;
    LinearLayout sports;
    LinearLayout goto_search;
    TextView r_empty;
    TextView m_empty;
    TextView s_empty;
    Bundle bundle;
    RelativeLayout bg;
    NetworkInfo networkInfo;
    private static final int Books_LOADER_ID = 1;
    private static final int Mags_LOADER_ID = 2;
    private static final int sports_LOADER_ID = 3;
    private static final String query = "https://www.googleapis.com/books/v1/volumes?q=subject:android";
    private static final String mag_query = "https://www.googleapis.com/books/v1/volumes?q=time&printType=magazines";
    private static final String sports_query = "https://www.googleapis.com/books/v1/volumes?q=books+about+sports";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        //allows the user to go to the search activity
        goto_search = (LinearLayout) findViewById(R.id.gotosearch);
        goto_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchBooks.class);
                startActivity(intent);
            }
        });
        //if night mode is activated
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightmode = sharedPreferences.getBoolean(getString(R.string.night_key), false);
        activateNightMode(nightmode);

        //Initialize variables
        internet = (TextView) findViewById(R.id.internet);
        gallery_recommended = (Gallery) findViewById(R.id.recommended_gallery);
        magazines = (Gallery) findViewById(R.id.magazines_gallery);
        sports_gallery = (Gallery) findViewById(R.id.sports_gallery);
        progressbar = (ProgressBar) findViewById(R.id.progress);
        progressbar.setIndeterminate(true);
        loaderManager = getLoaderManager();
        r_empty = (TextView) findViewById(R.id.empty_recommended);
        s_empty = (TextView) findViewById(R.id.empty_sports);
        m_empty = (TextView) findViewById(R.id.empty_mags);
        magazine = (LinearLayout) findViewById(R.id.magazine);
        recommended = (LinearLayout) findViewById(R.id.recomended);
        sports = (LinearLayout) findViewById(R.id.sports);
        //Check for connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //Initialize books loader
            loaderManager.initLoader(Books_LOADER_ID, null,this);
            //Initialize magazines loader
            loaderManager.initLoader(Mags_LOADER_ID, null, this);
            //Initialize sports loader
            loaderManager.initLoader(sports_LOADER_ID, null, this);
        } else {
            noInternet();
            //not connected to the internet
        }

    }

    public void noBooks(int id) {
        switch (id) {
            case 1:
                r_empty.setText("No books available");
                break;
            case 2:
                m_empty.setText("No magazines available");
                break;
            case 3:
                s_empty.setText("No sports books available");
                break;
        }
    }

    public void noInternet() {
        recommended.setVisibility(View.GONE);
        magazine.setVisibility(View.GONE);
        sports.setVisibility(View.GONE);
        gallery_recommended.setVisibility(View.GONE);
        magazines.setVisibility(View.GONE);
        sports_gallery.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
        internet.setText("No internet connection");
        Toast.makeText(getApplicationContext(), "Connect your device to an internet connection", Toast.LENGTH_LONG).show();
    }

    //Update user interface
    public void updateUI(final ArrayList<Books> books, int ID) {
        booksAdapter = new BooksAdapter(this, books);
        switch (ID) {
            case 1:
                recommended.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, BooksListview.class);
                        intent.putExtra("query", query);
                        startActivity(intent);
                    }
                });
                gallery_recommended.setAdapter(booksAdapter);
                gallery_recommended.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, CheckOut.class);
                        openVolume(books, position, intent);
                    }
                });
                break;
            case 2:
                magazine.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, BooksListview.class);
                        intent.putExtra("query", mag_query);
                        startActivity(intent);
                    }
                });
                magazines.setAdapter(booksAdapter);
                magazines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, CheckOut.class);
                        openVolume(books, position, intent);
                    }
                });
                break;
            case 3:
                sports.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, BooksListview.class);
                        intent.putExtra("query", sports_query);
                        startActivity(intent);
                    }
                });

                sports_gallery.setAdapter(booksAdapter);
                sports_gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, CheckOut.class);
                        openVolume(books, position, intent);
                    }
                });

                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main) {
            Intent intent = new Intent(this, Main_settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //Once user clicks on a volume send him/her to the details page
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void openVolume(final ArrayList<Books> books, int position, Intent intent) {
        Books selected_book = books.get(position);
        intent.putExtra("author", selected_book.getAuthor());
        intent.putExtra("title", selected_book.getTitle());
        intent.putExtra("image", selected_book.getImage());
        intent.putExtra("ratings", selected_book.getRatings());
        intent.putExtra("description", selected_book.getDescription());
        intent.putExtra("weblink", selected_book.getWeblink());
        startActivity(intent,bundle);

    }

    @Override
    public Loader<ArrayList<Books>> onCreateLoader(int id, Bundle args) {
        if (id == Books_LOADER_ID) {
            current_query = query;
        }
        if (id == Mags_LOADER_ID) {
            current_query = mag_query;
        }
        if (id == sports_LOADER_ID) {
            current_query = sports_query;
        }
        return new BooksLoader(this, current_query);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Books>> loader, ArrayList<Books> data) {
        Log.e(MainActivity.class.getSimpleName(), "onLoadFinished: ");
        progressbar.setVisibility(View.GONE);
        if (data != null && !data.isEmpty()) {
            updateUI(data, loader.getId());
        } else {
            if (networkInfo != null && networkInfo.isConnected()) {
                noInternet();
            } else {
                noBooks(loader.getId());
            }
        }
    }

    public void activateNightMode(boolean activate) {
        TextView rec = (TextView) findViewById(R.id.rec);
        TextView mag = (TextView) findViewById(R.id.mag);
        TextView sport = (TextView) findViewById(R.id.sport);
//        bg = (RelativeLayout) findViewById(R.id.activity_main);
        if (activate == true) {
         //   bg.setBackgroundColor(getResources().getColor(R.color.black));
            rec.setTextColor(getResources().getColor(R.color.red));
            mag.setTextColor(getResources().getColor(R.color.red));
            sport.setTextColor(getResources().getColor(R.color.red));
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        booksAdapter.clear();
    }
}
