package com.example.movieadvisor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private ArrayList<String> mTestArray;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView mTvMovieTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvMovieTitle = (TextView) itemView.findViewById(R.id.movie_list_item_tvMovieTitle);
        }
    }

    public MovieListAdapter(JSONArray movies){
        mTestArray = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            mTestArray.add(Integer.toString(i));
        }
        // TODO: treat errors when looping through the JSONArray, because a parsing error could happen
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTvMovieTitle.setText(mTestArray.get(position));
    }

    @Override
    public int getItemCount() {
        return mTestArray.size();
    }

}
