package com.example.doannhat.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by doannhat on 10/23/16.
 */
public class DetailMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    public static class DetailFragment extends Fragment {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        public DetailFragment() {
            setHasOptionsMenu(true);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(getString(R.string.detail_title))
                    && intent.hasExtra(getString(R.string.detail_posterUrl))
                    && intent.hasExtra(getString(R.string.detail_overview))
                    && intent.hasExtra(getString(R.string.detail_rating))
                    && intent.hasExtra(getString(R.string.detail_releaseDate))) {
                String title = intent.getStringExtra(getString(R.string.detail_title));
                String posterUrl = intent.getStringExtra(getString(R.string.detail_posterUrl));
                String overview = intent.getStringExtra(getString(R.string.detail_overview));
                Double rating = intent.getDoubleExtra(getString(R.string.detail_rating), 0);
                String releaseDate = intent.getStringExtra(getString(R.string.detail_releaseDate));


                ((TextView) rootView.findViewById(R.id.detail_movie_title))
                        .setText(title);
                ((TextView) rootView.findViewById(R.id.detail_movie_overview))
                        .setText("Overview: " + overview);
                ((TextView) rootView.findViewById(R.id.detail_movie_rating))
                        .setText("Rating: " + Double.toString(rating));
                ((TextView) rootView.findViewById(R.id.detail_movie_release_date))
                        .setText("Release Date: " + releaseDate);

                ImageView thumbnail = (ImageView) rootView.findViewById(R.id.detail_movie_thumbnail);

                Picasso.with(getContext()).load(posterUrl).into(thumbnail);
            }
            return rootView;
        }
    }
}
