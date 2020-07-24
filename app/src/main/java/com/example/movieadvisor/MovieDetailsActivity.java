package com.example.movieadvisor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieadvisor.util.IPAddresses;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mTvMovieTitle = findViewById(R.id.activity_movie_details_tvMovieTitle);
        mImMoviePoster = findViewById(R.id.activity_movie_details_imMoviePoster);
        mTvMovieGenres = findViewById(R.id.activity_movie_details_tvMovieGenres);
        mTvMovieSynopsis = findViewById(R.id.activity_movie_details_tvMovieSynopsis);

        // Get movie id to be displayed
        Bundle data = getIntent().getExtras();
        int movieId;
        if(data == null){
            return;
        }
        movieId = data.getInt(MainActivity.movieIdIntentKey);

        requestMovieDetails(movieId);
    }

    /*
        Make an asynchronous request to get a JSONObject with the movie details.
        When it receives the response, it calls the method showMovieDetails.
     */
    private void requestMovieDetails(int movieId){
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
                        showMovieDetails();
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


    // Show movie title, poster, genres and synopsis to the screen
    public void showMovieDetails(){
        /*


        try{

        }catch(JSONException e){
            e.printStackTrace();
            // TODO: treat errors
        }

         */
    }
}