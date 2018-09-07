package com.example.disen.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by disen on 7/7/2017.
 */

public class BooksAdapter extends ArrayAdapter {
    public BooksAdapter(Context context, ArrayList resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View galleryView = convertView;
        if (galleryView == null) {
            galleryView = LayoutInflater.from(getContext()).inflate(R.layout.list_books, parent, false);
        }
        Books current = (Books) getItem(position);
        TextView author = (TextView) galleryView.findViewById(R.id.author);
        TextView title = (TextView) galleryView.findViewById(R.id.title);
        TextView date = (TextView) galleryView.findViewById(R.id.date);
        ImageView image = (ImageView) galleryView.findViewById(R.id.book_image);
        date.setText("published in " + current.getDate());
        author.setText("by " + current.getAuthor());
        title.setText(current.getTitle());
        if (current.getImage() != null && current.getImage().length() > 0) {
            Picasso.with(getContext()).load(current.getImage()).into(image);
        } else {
            Picasso.with(getContext()).load(R.drawable.no_picture).into(image);
        }


        return galleryView;
    }
}
