package com.example.doannhat.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.doannhat.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doannhat on 10/16/16.
 */
public class FetchMovieInfoTask extends AsyncTask<String, Void, List<Movie>> {
    private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();
    private final Context context;
    private String responseString = null;
    private ImageAdapter imageAdapter;
    private List<Movie> movieList;

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }

    public FetchMovieInfoTask(Context context, ImageAdapter imageAdapter) {
        this.context = context;
        this.imageAdapter = imageAdapter;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            final String POPPULAR_MOVIES_URL =
                    "http://api.themoviedb.org/3/movie/" + params[0] + "?";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(POPPULAR_MOVIES_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, context.getString(R.string.my_token))
                    .build();

            URL url = new URL(builtUri.toString());
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());
            Log.v(LOG_TAG, "RESPONSE from MovieAPI: " + urlConnection.getResponseCode());
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                responseString = null;
            }
            responseString = buffer.toString();
            Log.v(LOG_TAG, "Movies string: " + responseString);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error ", ex);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            this.movieList = extractMovieListFromResponse(responseString);
            return movieList;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> movieList) {
        if (movieList != null) {
            if (imageAdapter != null) {
                imageAdapter.clear();
                for (Movie movie : movieList) {
                    imageAdapter.add(movie);
                }
            }
        }
    }

    private List<Movie> extractMovieListFromResponse(String responseString) throws JSONException {
        List<Movie> moviesList = new ArrayList<>();
        // Name of Json object to be extracted
        final String RESULTS = "results";

        JSONObject grandResponse = new JSONObject(responseString);
        JSONArray resultsArray = grandResponse.getJSONArray(RESULTS);

        for (int i = 0; i < resultsArray.length(); i ++) {
            JSONObject jsonMovie = resultsArray.getJSONObject(i);
            Movie extractedMovie = getMovieDataFromJSON(jsonMovie);

            moviesList.add(extractedMovie);
        }


        return moviesList;
    }

    private Movie getMovieDataFromJSON(JSONObject jsonMovie) throws JSONException {
        final String POSTER_PATH = "poster_path";
        final String POPULARITY = "popularity";
        final String VOTE_AVERAGE = "vote_average";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";


        Movie movie = new Movie();
        movie.setPoster_path(buildImageUrlsFromPosterPath(jsonMovie.getString(POSTER_PATH)));
        movie.setPopularity(jsonMovie.getDouble(POPULARITY));
        movie.setVoteAverage(jsonMovie.getDouble(VOTE_AVERAGE));
        movie.setOriginalTitle(jsonMovie.getString(ORIGINAL_TITLE));
        movie.setOverview(jsonMovie.getString(OVERVIEW));
        movie.setReleaseDate(jsonMovie.getString(RELEASE_DATE));
        return movie;
    }

    private String buildImageUrlsFromPosterPath(String posterPath) {
        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE = "w185";

        return IMAGE_BASE_URL + SIZE + posterPath;
    }
}
