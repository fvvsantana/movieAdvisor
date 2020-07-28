package com.example.movieadvisor.util;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public class CacheRequest extends Request<NetworkResponse> {
    private static final String TAG = "CacheRequest";
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;

    public CacheRequest(int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }


    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        Log.d(TAG, "parseNetworkResponse: called");
        Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
        if (cacheEntry == null) {
            cacheEntry = new Cache.Entry();
        }

        // TODO: Get these variables back to the original value
        //final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
        final long cacheHitButRefreshed = 2 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
        //final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
        final long cacheExpired = 10 * 1000; // in 24 hours this cache entry expires completely
        long now = System.currentTimeMillis();
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;
        cacheEntry.data = response.data;
        cacheEntry.softTtl = softExpire;
        cacheEntry.ttl = ttl;
        String headerValue;
        headerValue = response.headers.get("Date");
        if (headerValue != null) {
            cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }
        headerValue = response.headers.get("Last-Modified");
        if (headerValue != null) {
            cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }
        cacheEntry.responseHeaders = response.headers;
        return Response.success(response, cacheEntry);
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        Log.d(TAG, "deliverResponse: called");
        mListener.onResponse(response);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        Log.d(TAG, "parseNetworkError: called");
        return super.parseNetworkError(volleyError);
    }

    @Override
    public void deliverError(VolleyError error) {
        Log.d(TAG, "deliverError: called");
        mErrorListener.onErrorResponse(error);

        Cache.Entry cacheEntry = getCacheEntry();
        if(cacheEntry == null) return;
        if(VolleyErrorHelper.isNetworkProblem(error) && cacheEntry.isExpired()){
            NetworkResponse networkResponse = new NetworkResponse(cacheEntry.data);
            mListener.onResponse(networkResponse);
        }
    }
    
}
