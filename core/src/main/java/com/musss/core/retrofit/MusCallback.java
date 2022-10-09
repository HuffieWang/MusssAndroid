package com.musss.core.retrofit;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class MusCallback<T> implements Callback<T> {

    public static final String NETWORK_ERROR = "网络错误";

    public abstract void onResponse(Response<T> response);

    public abstract void onFailure(Throwable t);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        onResponse(response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        String msg = t.getLocalizedMessage();
        if(!(t instanceof MusException)){
            onFailure(new IOException(NETWORK_ERROR));
        } else {
            onFailure(t);
        }
    }
}
