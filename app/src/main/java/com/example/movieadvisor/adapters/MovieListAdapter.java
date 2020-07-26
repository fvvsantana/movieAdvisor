package com.example.movieadvisor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieadvisor.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    private static final String TAG = "MovieListAdapter";

    private JSONArray mMoviesData;
    private static final String movieTitleJsonKey = "title";
    private static final String moviePosterUrlJsonKey = "poster_url";

    private ViewHolder.MovieOnClickListener mMovieListener;

    // A ViewHolder object can be seen as the representation of an item (a movie) in the list
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public interface MovieOnClickListener{
            void movieOnClick(int moviePosition);
        }

        private MovieOnClickListener mMovieOnClickListener;

        private TextView mTvMovieTitle;
        private ImageView mIvMoviePoster;
        public ViewHolder(@NonNull View itemView, MovieOnClickListener movieOnClickListener){
            super(itemView);
            mTvMovieTitle = itemView.findViewById(R.id.movie_list_item_tvMovieTitle);
            mIvMoviePoster = itemView.findViewById(R.id.movie_list_item_ivMoviePoster);

            // Get the main activity as listener to this viewholder
            mMovieOnClickListener = movieOnClickListener;

            // Set this view holder as listener of the view item
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Inform the main activity that this ViewHolder was clicked
            mMovieOnClickListener.movieOnClick(getAdapterPosition());
        }

    }

    /*
     Receive the data about the movies to be shown.
     Receive an object that will listen to clicks on the movies.
    */
    public MovieListAdapter(JSONArray moviesData, ViewHolder.MovieOnClickListener movieListener){
        mMoviesData = moviesData;
        mMovieListener = movieListener;
        // TODO: treat errors when looping through the JSONArray, because a parsing error could happen
    }

    // Inflate layout from xml and create a ViewHolder to be bound to a position by the adapter
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new ViewHolder(view, mMovieListener);
    }

    // Fill a ViewHolder's fields from the JSONArray information
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try{
            JSONObject jsonObject = mMoviesData.getJSONObject(position);

            // Set title
            String movieTitle = jsonObject.getString(movieTitleJsonKey);
            holder.mTvMovieTitle.setText(movieTitle);

            // Set poster
            String moviePosterURL = jsonObject.getString(moviePosterUrlJsonKey);
            // TODO: treat errors on image loading
            //Picasso.get().load(moviePosterURL).into(holder.mIvMoviePoster);
            loadImage(moviePosterURL, holder.mIvMoviePoster);
        }catch(JSONException e){
            e.printStackTrace();
            // TODO: treat this parsing error
        }
    }

    // Return the size of the data set
    @Override
    public int getItemCount() {
        return mMoviesData.length();
    }

    // Load image from the passed imageURL to an ImageView
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
