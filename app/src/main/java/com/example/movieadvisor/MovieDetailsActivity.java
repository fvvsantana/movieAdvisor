package com.example.movieadvisor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieadvisor.util.IPAddresses;
import com.example.movieadvisor.util.RequestState;
import com.example.movieadvisor.util.VolleyErrorHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String TAG = "MovieDetailsActivity";


    private TextView mTvMovieTitle;
    private ImageView mImMoviePoster;
    private TextView mTvMovieGenres;
    private TextView mTvMovieSynopsis;


    private JSONObject mMovieData;
    private static final String movieTitleJsonKey = "title";
    private static final String moviePosterUrlJsonKey = "poster_url";
    private static final String movieGenresJsonKey = "genres";
    private static final String movieSynopsisJsonKey = "overview";

    private RequestQueue mRequestQueue;

    private ProgressBar mProgressBar;
    private TextView mTvError;
    private TextView mTvTouchToReload;

    // Variable to track the states of the request for the movie details
    private RequestState mMovieDetailsRequestState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mProgressBar = findViewById(R.id.activity_movie_details_progressBar);
        mTvError = findViewById(R.id.activity_movie_details_tvError);
        mTvTouchToReload = findViewById(R.id.activity_movie_details_tvTouchToReload);

        mTvMovieTitle = findViewById(R.id.activity_movie_details_tvMovieTitle);
        mImMoviePoster = findViewById(R.id.activity_movie_details_imMoviePoster);
        mTvMovieGenres = findViewById(R.id.activity_movie_details_tvMovieGenres);
        mTvMovieSynopsis = findViewById(R.id.activity_movie_details_tvMovieSynopsis);

        // Update request state
        mMovieDetailsRequestState = RequestState.NOT_REQUESTED;

        // Get movie id to be displayed
        Bundle data = getIntent().getExtras();
        int movieId;
        if(data == null){
            // TODO: add error treatment here
            return;
        }
        movieId = data.getInt(MainActivity.movieIdIntentKey);

        mRequestQueue = Volley.newRequestQueue(this);

        // Fetch movie data and call showMovieDetails to show the data.
        requestMovieDetails(movieId);
    }

    /*
        Make an asynchronous request to get a JSONObject with the movie details.
        When it receives the response:
            Remove the ProgressBar from the screen
            Call the method showMovieDetails
        If it receives an error:
            Remove the ProgressBar from the screen
            Treat error
     */
    private void requestMovieDetails(int movieId){
        String movieURL = IPAddresses.MOVIES_API_URL + '/' + movieId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                movieURL,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        // Update request state
                        mMovieDetailsRequestState = RequestState.SUCCESSFUL;

                        mMovieData = response;

                        /* Remove progress bar because at this point we already have the JSONObject
                            with the details of the movie */
                        removeProgressBar();

                        Log.d(TAG, "Movie details response: " + response.toString());
                        showMovieDetails();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e(TAG, "Error on fetching movie details: " + error.toString());

                        // Remove progress bar
                        removeProgressBar();

                        // Show information about the error and how to reload the screen
                        showErrorInformation(error);

                        // Update request state
                        mMovieDetailsRequestState = RequestState.ERROR;
                        // TODO: treat errors
                    }
                }
        );
        mRequestQueue.add(jsonObjectRequest);

        // Update request state
        mMovieDetailsRequestState = RequestState.REQUESTED;
    }

    // Remove progress bar from the screen
    private void removeProgressBar(){
        mProgressBar.setVisibility(View.GONE);

    }

    // Show information about the error and how to reload the screen
    private void showErrorInformation(VolleyError error){
        // Show error information
        mTvError.setText(VolleyErrorHelper.getMessage(error, MovieDetailsActivity.this));
        mTvError.setVisibility(View.VISIBLE);
        // Inform the user that he can reload the screen by touching it
        mTvTouchToReload.setVisibility(View.VISIBLE);
    }

    // Show movie title, poster, genres and synopsis to the screen
    private void showMovieDetails(){
        try{
            // Title
            String movieTitle = mMovieData.getString(movieTitleJsonKey);
            mTvMovieTitle.setText(movieTitle);
            // Genres
            JSONArray movieGenres = mMovieData.getJSONArray(movieGenresJsonKey);
            mTvMovieGenres.setText(getGenresText(movieGenres));
            // Synopsis
            String movieSynopsis = mMovieData.getString(movieSynopsisJsonKey);
            mTvMovieSynopsis.setText(movieSynopsis);
            // Poster
            String moviePosterUrl = mMovieData.getString(moviePosterUrlJsonKey);
            loadImage(moviePosterUrl, mImMoviePoster);

        }catch(JSONException e){
            e.printStackTrace();
            // TODO: treat errors
        }
    }

    // Convert the JSONArray of genres to a string
    private String getGenresText(JSONArray movieGenres) throws JSONException {
        int nGenres = movieGenres.length();
        StringBuilder aux = new StringBuilder();
        for(int i = 0; i < nGenres - 1; i++){
            aux.append(movieGenres.getString(i));
            aux.append(", ");
        }
        if(nGenres > 0){
            aux.append(movieGenres.getString(nGenres - 1));
        }
        return aux.toString();
    }

    private void loadImage(String imageURL, ImageView imageView){
        // TODO: read the documentation of this method to see if I need to do something more (Ctrl+Q)
        Picasso.get().load(imageURL).error(R.mipmap.no_image_100).into(imageView, new Callback() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                // TODO: treat errors
                e.printStackTrace();
            }
        });

    }
}