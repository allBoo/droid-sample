package com.example.myapplication.network;

import android.util.Log;

import com.example.myapplication.network.converters.NetworkDateTimeConverter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.squareup.moshi.Moshi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

class RetrofitFactory {
    private static final String URL = "http://10.0.2.2:8079";

    private static volatile RetrofitFactory instance;

    private final Retrofit retrofit;

    private RetrofitFactory(String URL) {
        final Moshi moshi = new Moshi.Builder().build();
        final HttpLoggingInterceptor logger = new HttpLoggingInterceptor(
                (message) -> Log.d(this.getClass().getSimpleName(), message));
        logger.setLevel(HttpLoggingInterceptor.Level.BASIC);
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logger)
                .build();
        final Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .registerTypeAdapter(OffsetDateTime.class, new NetworkDateTimeConverter())
                .create();
        retrofit = new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
//                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(URL)
                .build();
    }

    public static Retrofit getRetrofit() {
        if (instance == null) {
            synchronized (FineService.class) {
                if (instance == null) {
                    instance = new RetrofitFactory(URL);
                }
            }
        }
        return instance.retrofit;
    }
}
