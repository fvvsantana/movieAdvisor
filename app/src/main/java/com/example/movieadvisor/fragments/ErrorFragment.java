package com.example.movieadvisor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.movieadvisor.R;

public class ErrorFragment extends Fragment {

    private TextView mTvError;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_error, container, false);
        mTvError =  view.findViewById(R.id.fragment_error_tvError);
        return view;
    }

    public void setErrorMessage(String errorMessage){
        mTvError.setText(errorMessage);
    }


    public void setButtonOnClickListener(View.OnClickListener listener){
        // TODO: Implement this method
    }
}