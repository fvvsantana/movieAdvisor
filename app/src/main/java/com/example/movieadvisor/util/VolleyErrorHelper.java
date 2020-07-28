package com.example.movieadvisor.util;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.movieadvisor.R;

public class VolleyErrorHelper {

    // Provide user-readable error message from VolleyError
    public static String getMessage(VolleyError error, Context context) {
        if (isNetworkProblem(error)) {
            return context.getResources().getString(R.string.error_noInternetConnection);
        }
        if (isTimeoutProblem(error)) {
            return context.getResources().getString(R.string.error_timeoutError);
        }
        if (isServerProblem(error)) {
            return handleServerError(error, context);
        }
        return context.getResources().getString(R.string.error_genericServerError);
    }

    // Check if it's a network problem (user connection is down)
    public static boolean isNetworkProblem(VolleyError error) {
        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
    }

    // Check if it's a timeout problem
    public static boolean isTimeoutProblem(VolleyError error) {
        return error instanceof TimeoutError;
    }

    // Check if it's a server problem
    public static boolean isServerProblem(VolleyError error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }

    // Generate string for server error
    private static String handleServerError(VolleyError error, Context context) {
        NetworkResponse response = error.networkResponse;

        if (response != null) {
            return context.getResources()
                    .getString(R.string.error_genericServerError + ' ' +  response.statusCode);
        }
        return context.getResources().getString(R.string.error_genericServerError);
    }
}
