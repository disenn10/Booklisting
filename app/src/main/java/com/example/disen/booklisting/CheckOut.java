package com.example.disen.booklisting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class CheckOut extends AppCompatActivity {
    TextView author;
    TextView title;
    ImageView cover;
    RatingBar ratingBar;
    TextView desc;
    LinearLayout more;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        Intent intent = getIntent();
        String volume_author = intent.getStringExtra("author");
        String volume_title = intent.getStringExtra("title");
        String volume_cover = intent.getStringExtra("image");
        double volume_rate = intent.getDoubleExtra("ratings", 0);
        String description = intent.getStringExtra("description");
        final String weblink = intent.getStringExtra("weblink");
        ratingBar = (RatingBar) findViewById(R.id.rate);
        desc = (TextView) findViewById(R.id.description);
        author = (TextView) findViewById(R.id.volume_authors);
        title = (TextView) findViewById(R.id.book_title);
        cover = (ImageView) findViewById(R.id.volume_image);
        more = (LinearLayout) findViewById(R.id.more_details_layout);

        //if night mode is activated
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightmode = sharedPreferences.getBoolean(getString(R.string.night_key), false);
        activateNightMode(nightmode);

        desc.setText(description);
        author.setText("By: " + volume_author);
        title.setText(volume_title);
        ratingBar.setRating((float) volume_rate);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri infolink = Uri.parse(weblink);
                Intent intent1 = new Intent(Intent.ACTION_VIEW, infolink);
                startActivity(intent1);
            }
        });
        //if there is image available though
        if (volume_cover != null && volume_cover.length() != 0) {
            Picasso.with(getApplicationContext()).load(volume_cover).into(cover);
        } else {
            Picasso.with(getApplicationContext()).load(R.drawable.no_picture).into(cover);
        }
    }

    public void activateNightMode(boolean activate) {
        TextView rec = (TextView) findViewById(R.id.rec);
        TextView mag = (TextView) findViewById(R.id.mag);
        TextView sport = (TextView) findViewById(R.id.sport);
        ScrollView bg = (ScrollView) findViewById(R.id.activity_check_out);
        if (activate == true) {
            bg.setBackgroundColor(getResources().getColor(R.color.black));
            ratingBar.setBackgroundColor(getResources().getColor(R.color.red));
            title.setTextColor(getResources().getColor(R.color.red));
            author.setTextColor(getResources().getColor(R.color.red));
            desc.setTextColor(getResources().getColor(R.color.orange));
        }
    }
}
