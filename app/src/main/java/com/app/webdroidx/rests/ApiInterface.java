package com.app.webdroidx.rests;

import com.app.webdroidx.callbacks.CallbackConfig;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiInterface {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "Data-Agent: WebDroidX";

    @Headers({CACHE, AGENT})
    @GET("api.php")
    Call<CallbackConfig> getConfig(
            @Query("application_id") String application_id
    );

}