package com.example.movieadvisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieadvisor.adapters.MovieListAdapter;
import com.example.movieadvisor.util.IPAddresses;

import org.json.JSONArray;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity implements MovieListAdapter.ViewHolder.MovieOnClickListener{
    private static final String TAG = "MainActivity";
    public static final String movieIdIntentKey = "movieId";
    private static final String movieIdJsonKey = "id";

    private ProgressBar mProgressBar;

    private RecyclerView mRvMoviesList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private JSONArray mMoviesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.activity_main_progressBar);

        // RecyclerView for the list of movies
        mRvMoviesList = findViewById(R.id.activity_main_rvMoviesList);
        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRvMoviesList.setHasFixedSize(true);
        // Use a linear layout manager for the layout of the list of movies
        mLayoutManager = new LinearLayoutManager(this);
        mRvMoviesList.setLayoutManager(mLayoutManager);

        // Request the list of movies asynchronously and show movies when responded
        requestMovies();
    }

    /*
        Make an asynchronous request to get a JSONArray with the movies.
        When it receives the response, it calls the method showMoviesList.
     */
    private void requestMovies(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                IPAddresses.MOVIES_API_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Movies response: " + response.toString());
                        mMoviesData = response;
                        showMoviesList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // TODO: treat errors
                        Log.e(TAG, "Error on fetching movies: " + error.toString());

                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    // Show movies to the screen
    public void showMoviesList(){
        mAdapter = new MovieListAdapter(mMoviesData, this);
        mRvMoviesList.setAdapter(mAdapter);
        // TODO: stop loading when you get an error also, then show some error information
        // Remove progress bar because at this point we already have the JSONArray of movies
        mProgressBar.setVisibility(View.GONE);
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
    public void goToMovieDetails(int moviePosition){
        try{
            int movieId = mMoviesData.getJSONObject(moviePosition).getInt(movieIdJsonKey);

            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(movieIdIntentKey, movieId);
            startActivity(intent);
        }catch(JSONException e){
            // TODO: treat error
            e.printStackTrace();
        }
    }

}