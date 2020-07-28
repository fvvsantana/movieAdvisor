package com.example.movieadvisor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.example.movieadvisor.fragments.ErrorFragment;
import com.example.movieadvisor.util.CacheRequest;
import com.example.movieadvisor.util.IPAddresses;
import com.example.movieadvisor.util.RequestState;
import com.example.movieadvisor.util.VolleyErrorHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String TAG = "MovieDetailsActivity";

    private TextView mTvMovieTitle;
    private ImageView mImMoviePoster;
    private TextView mTvMovieGenres;
    private TextView mTvMovieSynopsis;

    private int mMovieId;
    private JSONObject mMovieData;
    private static final String movieTitleJsonKey = "title";
    private static final String moviePosterUrlJsonKey = "poster_url";
    private static final String movieGenresJsonKey = "genres";
    private static final String movieSynopsisJsonKey = "overview";

    private RequestQueue mRequestQueue;

    private View mContent;
    private ProgressBar mProgressBar;
    private ErrorFragment mErrorFragment;
    // Variable to track the states of the request for the movie details
    private RequestState mMovieDetailsRequestState;
    private String mMovieURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Get reference for the error fragment and setup retry button
        setupErrorFragment();

        mContent = findViewById(R.id.activity_movie_details_content);
        // Show content layout only when the data is ready
        removeContent();
        mProgressBar = findViewById(R.id.activity_movie_details_progressBar);

        mTvMovieTitle = findViewById(R.id.activity_movie_details_tvMovieTitle);
        mImMoviePoster = findViewById(R.id.activity_movie_details_imMoviePoster);
        mTvMovieGenres = findViewById(R.id.activity_movie_details_tvMovieGenres);
        mTvMovieSynopsis = findViewById(R.id.activity_movie_details_tvMovieSynopsis);

        // Update request state
        mMovieDetailsRequestState = RequestState.NOT_REQUESTED;

        // Get movie id to be displayed
        Bundle data = getIntent().getExtras();

        if(data == null){
            Toast.makeText(this, R.string.error_internalError, Toast.LENGTH_SHORT).show();
            return;
        }
        mMovieId = data.getInt(MainActivity.movieIdIntentKey);

        mRequestQueue = Volley.newRequestQueue(this);

        // Fetch movie data and call showMovieDetails to show the data.
        requestMovieDetails();
    }

    // Get reference for the error fragment and setup retry button
    private void setupErrorFragment() {
        mErrorFragment = (ErrorFragment) getSupportFragmentManager().findFragmentById(R.id.activity_movie_details_errorFragment);
        if (mErrorFragment != null) {
            // Don't show error fragment
            mErrorFragment.remove();

            /*
            This is called when the user clicks the "retry button".
            Remove error fragment and request movies again. */
            mErrorFragment.setRetryButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mMovieDetailsRequestState == RequestState.ERROR) {
                        mErrorFragment.remove();
                        requestMovieDetails();
                    }
                }
            });
        }else{
            Toast.makeText(this, R.string.error_internalError, Toast.LENGTH_SHORT).show();
        }
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
    private void requestMovieDetails(){
        showProgressBar();
        mMovieURL = IPAddresses.MOVIES_API_URL + '/' + mMovieId;

        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.GET,
                mMovieURL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        // Update request state
                        mMovieDetailsRequestState = RequestState.SUCCESSFUL;

                        // Parse JSONArray
                        try {
                            final String jsonObjectString = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers));
                            // Store the movie details
                            mMovieData = new JSONObject(jsonObjectString);
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MovieDetailsActivity.this, R.string.error_parsingError, Toast.LENGTH_SHORT).show();
                        }

                        /* Remove progress bar because at this point we already have the JSONObject
                            with the details of the movie */
                        removeProgressBar();

                        showMovieDetails();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // If the user is offline and there is cache to be shown
                        boolean thereIsCache = mRequestQueue.getCache().get(mMovieURL) != null;
                        if(VolleyErrorHelper.isNetworkProblem(error) && thereIsCache){
                            return;
                        }

                        error.printStackTrace();

                        // Remove undesired views
                        removeProgressBar();
                        removeContent();

                        // Show error message + retry button
                        mErrorFragment.show(VolleyErrorHelper.getMessage(error, MovieDetailsActivity.this));

                        // Update request state
                        mMovieDetailsRequestState = RequestState.ERROR;
                    }
                }
        );
        mRequestQueue.add(cacheRequest);

        // Update request state
        mMovieDetailsRequestState = RequestState.REQUESTED;
    }

    // Show progress bar from the screen
    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    // Remove progress bar from the screen
    private void removeProgressBar(){
        mProgressBar.setVisibility(View.GONE);

    }

    // Show movie details
    private void showContent(){
        mContent.setVisibility(View.VISIBLE);
    }

    // Remove movie details
    private void removeContent(){
        mContent.setVisibility(View.GONE);
    }

    // Show movie title, poster, genres and synopsis to the screen
    private void showMovieDetails(){
        showContent();
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
            Toast.makeText(this, R.string.error_parsingError, Toast.LENGTH_SHORT).show();
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

    // Load image from the passed imageURL to an ImageView
    private void loadImage(String imageURL, ImageView imageView){
        Picasso.get().load(imageURL).error(R.mipmap.no_image_100).into(imageView);
    }
}