package com.musss.core.config;

import com.musss.core.entity.MusEntity;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MusApi {
    String baseUrl = "https://musss.vicp.fun/";

    @Multipart
    @POST("/logcat/upload")
    Call<MusEntity> upload(@Part(value = "device") String device, @Part List<MultipartBody.Part> part);
}
