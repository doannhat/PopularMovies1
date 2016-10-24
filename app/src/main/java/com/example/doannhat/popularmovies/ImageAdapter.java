package com.example.doannhat.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.doannhat.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by doannhat on 10/8/16.
 */
public class ImageAdapter extends ArrayAdapter<Movie> {

    public ImageAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poster_grid_item,
                    parent, false);
        }

        ImageView moviePoster = (ImageView) convertView.findViewById(R.id.movie_poster);

        Picasso.with(getContext()).load(movie.getPoster_path()).into(moviePoster);
        return moviePoster;
    }

}
