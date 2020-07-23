package com.example.movieadvisor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieadvisor.util.IPAddresses;

import org.json.JSONObject;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String TAG = "MovieDetailsActivity";
    private TextView mTvMovieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mTvMovieDetails = (TextView) findViewById(R.id.tvMovieDetails);

        // Get movie id to be displayed
        Bundle data = getIntent().getExtras();
        int movieId;
        if(data == null){
            return;
        }
        movieId = data.getInt(MainActivity.movieIdKey);

        // Request movie details for the specific movie
        String movieURL = IPAddresses.MOVIES_API_URL + '/' + movieId;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                movieURL,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Movie details response: " + response.toString());
                        showMovie(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: treat errors
                        Log.e(TAG, "Error on fetching movie details: " + error.toString());

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void showMovie(JSONObject movie){
        mTvMovieDetails.setText(movie.toString());
    }
}