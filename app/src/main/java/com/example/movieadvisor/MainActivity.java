package com.example.movieadvisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.example.movieadvisor.adapters.MovieListAdapter;
import com.example.movieadvisor.fragments.ErrorFragment;
import com.example.movieadvisor.util.CacheRequest;
import com.example.movieadvisor.util.IPAddresses;
import com.example.movieadvisor.util.RequestState;
import com.example.movieadvisor.util.VolleyErrorHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity implements MovieListAdapter.ViewHolder.MovieOnClickListener{
    private static final String TAG = "MainActivity";
    public static final String movieIdIntentKey = "movieId";
    private static final String movieIdJsonKey = "id";

    private RecyclerView mRvMoviesList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private JSONArray mMoviesData;

    private RequestQueue mRequestQueue;

    private View mContent;
    private ProgressBar mProgressBar;
    private ErrorFragment mErrorFragment;
    // Variable to track the states of the request for the movies list
    private RequestState mMoviesRequestState;
    private String mMoviesURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Uncomment this line to enable Picasso's debugging
        //Picasso.get().setIndicatorsEnabled(true);

        // Get reference for the error fragment and setup retry button
        setupErrorFragment();

        mContent = findViewById(R.id.activity_main_content);
        // Show content layout only when the data is ready
        removeContent();
        mProgressBar = findViewById(R.id.activity_main_progressBar);

        // Update request state
        mMoviesRequestState = RequestState.NOT_REQUESTED;

        // Get reference to RecyclerView and set its parameters
        setupRecyclerView();

        // Queue for adding the network requests
        mRequestQueue = Volley.newRequestQueue(this);

        // Request the list of movies asynchronously and show movies when responded
        requestMovies();
    }

    // Get reference for the error fragment and setup retry button
    private void setupErrorFragment() {
        mErrorFragment = (ErrorFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_errorFragment);
        if (mErrorFragment != null) {
            // Don't show error fragment
            mErrorFragment.remove();

            /*
            This is called when the user clicks the "retry button".
            Remove error fragment and request movies again. */
            mErrorFragment.setRetryButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mMoviesRequestState == RequestState.ERROR) {
                        mErrorFragment.remove();
                        requestMovies();
                    }
                }
            });
        }else{
            Toast.makeText(this, R.string.error_internalError, Toast.LENGTH_SHORT).show();
        }
    }

    // Get reference to RecyclerView and set its parameters
    private void setupRecyclerView(){
        // RecyclerView for the list of movies
        mRvMoviesList = findViewById(R.id.activity_main_rvMoviesList);
        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRvMoviesList.setHasFixedSize(true);
        // Use a linear layout manager for the layout of the list of movies
        mLayoutManager = new LinearLayoutManager(this);
        mRvMoviesList.setLayoutManager(mLayoutManager);
    }

    /*
        Make an asynchronous request to get a JSONArray with the movies.
        When it receives the response:
            Remove the ProgressBar from the screen
            Call the method showMoviesList.
        If it receives an error:
            Remove the ProgressBar from the screen
            Treat error
     */
    private void requestMovies(){
        showProgressBar();
        mMoviesURL = IPAddresses.MOVIES_API_URL;

        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.GET,
                mMoviesURL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        // Update request state
                        mMoviesRequestState = RequestState.SUCCESSFUL;

                        // Parse JSONArray
                        try {
                            final String jsonArrayString = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers));
                            // Store the list of movies
                            mMoviesData = new JSONArray(jsonArrayString);
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, R.string.error_parsingError, Toast.LENGTH_SHORT).show();
                        }

                        // Remove progress bar because at this point we already have the JSONArray of movies
                        removeProgressBar();

                        showMoviesList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // If the user is offline and there is cache to be shown
                        boolean thereIsCache = mRequestQueue.getCache().get(mMoviesURL) != null;
                        if(VolleyErrorHelper.isNetworkProblem(error) && thereIsCache){
                            return;
                        }

                        error.printStackTrace();

                        // Remove undesired views
                        removeProgressBar();
                        removeContent();

                        // Show error message + retry button
                        mErrorFragment.show(VolleyErrorHelper.getMessage(error, MainActivity.this));

                        // Update request state
                        mMoviesRequestState = RequestState.ERROR;
                    }
                }
        );
        mRequestQueue.add(cacheRequest);
        // Update request state
        mMoviesRequestState = RequestState.REQUESTED;
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

    // Show movies to the screen
    private void showMoviesList(){
        showContent();
        mAdapter = new MovieListAdapter(this, mMoviesData, this);
        mRvMoviesList.setAdapter(mAdapter);
    }

    // This method is called whenever a movie is clicked, receiving the movie position in the RecyclerView
    @Override
    public void movieOnClick(int moviePosition) {
        goToMovieDetails(moviePosition);
    }

    /*
        Pick the id of the movie at moviePosition and send to the next intent to show details of the
        selected movie.
    */
    private void goToMovieDetails(int moviePosition){
        try{
            int movieId = mMoviesData.getJSONObject(moviePosition).getInt(movieIdJsonKey);

            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(movieIdIntentKey, movieId);
            startActivity(intent);
        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(this, R.string.error_parsingError, Toast.LENGTH_SHORT).show();
        }
    }

}