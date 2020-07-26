package com.example.movieadvisor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.movieadvisor.R;

public class ErrorFragment extends Fragment {

    private View mFragmentView;
    private TextView mTvError;
    private Button mBtnRetry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_error, container, false);
        mTvError =  mFragmentView.findViewById(R.id.fragment_error_tvError);
        mBtnRetry = mFragmentView.findViewById(R.id.fragment_error_btnRetry);
        return mFragmentView;
    }

    public void show(String errorMessage){
        mTvError.setText(errorMessage);
        mFragmentView.setVisibility(View.VISIBLE);
    }

    public void remove(){
        mFragmentView.setVisibility(View.GONE);
    }



    public void setRetryButtonOnClickListener(View.OnClickListener listener){
        mBtnRetry.setOnClickListener(listener);
    }
}