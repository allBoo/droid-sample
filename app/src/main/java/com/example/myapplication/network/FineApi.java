package com.example.myapplication.network;

import com.example.myapplication.database.article.Article;
import com.example.myapplication.database.fine.Fine;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FineApi {
    @GET("/fines")
    Observable<List<Fine>> getFines(@Query("_page") int page,
                                    @Query("_limit") int limit);

    @POST("/fines")
    Observable<Fine> createFine(@Body Fine fine);

    @PUT("/fines/{id}")
    Observable<Fine> updateFine(@Path("id") int id, @Body Fine fine);

    @DELETE("/fines/{id}")
    Observable<Fine> deleteFine(@Path("id") int id);

    @GET("/articles")
    Observable<List<Article>> getArticles();
}
