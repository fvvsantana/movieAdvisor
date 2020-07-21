package com.example.movieadvisor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static final String movieIdKey = "movieId";
    public final String TAG = getClass().getSimpleName();

    private TextView mTvMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvMovies = (TextView) findViewById(R.id.tvMovies);

        // Request movies
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                IPAddresses.MOVIES_API_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, "Movies response: " + response.toString());
                        showMovies(response);
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

    public void showMovies(JSONArray movies){
        mTvMovies.setText(movies.toString());
    }

    public void goToDetails(View view){
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        int movieId = 240;
        intent.putExtra(movieIdKey, movieId);
        startActivity(intent);
    }
}