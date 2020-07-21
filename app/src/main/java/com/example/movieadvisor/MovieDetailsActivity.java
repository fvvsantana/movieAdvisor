package com.example.movieadvisor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MovieDetailsActivity extends AppCompatActivity {
    private TextView mTvMovieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mTvMovieDetails = (TextView) findViewById(R.id.tvMovieDetails);

        // Set text view
        Bundle data = getIntent().getExtras();
        int movieId;
        if (data != null) {
            movieId = data.getInt(MainActivity.movieIdKey);
            mTvMovieDetails.setText(Integer.toString(movieId));
        }

    }
}