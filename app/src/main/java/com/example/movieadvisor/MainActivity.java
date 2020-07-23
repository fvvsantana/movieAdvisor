package com.example.movieadvisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    public static final String movieIdKey = "movieId";

    private RecyclerView mRvMoviesList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private JSONArray mMoviesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView for the list of movies
        mRvMoviesList = (RecyclerView) findViewById(R.id.activity_main_rvMoviesList);
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
        When it receives the response, call the method showMoviesList passing the JSONArray as argument.
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
                        Log.e(TAG, "Movies response: " + response.toString());
                        mMoviesData = response;
                        showMoviesList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: treat errors
                        Log.e(TAG, "Error on fetching movies: " + error.toString());

                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public void showMoviesList(){
        mAdapter = new MovieListAdapter(mMoviesData, this);
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
    public void goToMovieDetails(int moviePosition){
        try{
            int movieId = mMoviesData.getJSONObject(moviePosition).getInt("id");

            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(movieIdKey, movieId);
            startActivity(intent);
        }catch(JSONException e){
            // TODO: treat error
            e.printStackTrace();
        }
    }

}