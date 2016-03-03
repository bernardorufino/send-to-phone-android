package com.brufino.sendtophone.app.services;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public class AppService {

    private static String ENDPOINT = "https://send-to-phone.herokuapp.com";

    private static Api API = null;

    public static Api getApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(Api.class);
    }

    public interface Api {

        @POST("devices")
        Call<Map<String, String>> registerDevice(@Body Map<String, String> data);
    }
}
