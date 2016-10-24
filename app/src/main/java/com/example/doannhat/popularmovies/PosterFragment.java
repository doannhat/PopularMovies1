package com.example.doannhat.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.doannhat.popularmovies.data.Movie;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PosterFragment extends Fragment {

    Context context;
    ImageAdapter imageAdapter;
    FetchMovieInfoTask fetchMovieInfoTask;

    public PosterFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.posterfragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(context, new ArrayList<Movie>());
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Movie movie = imageAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailMovieActivity.class)
                        .putExtra(getString(R.string.detail_title), movie.getOriginalTitle())
                        .putExtra(getString(R.string.detail_posterUrl), movie.getPoster_path())
                        .putExtra(getString(R.string.detail_overview), movie.getOverview())
                        .putExtra(getString(R.string.detail_rating), movie.getVoteAverage())
                        .putExtra(getString(R.string.detail_releaseDate), movie.getReleaseDate());
                startActivity(intent);
            }
        });
        return rootView;
    }


    private void updateMovie() {
        fetchMovieInfoTask = new FetchMovieInfoTask(context, imageAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String api = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));
        fetchMovieInfoTask.execute(api);
    }
}
