package com.example.myapplication.database.article;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ArticleDao {
    @Query("select * from articles")
    Single<List<Article>> getAll();

    @Insert
    Completable insert(Article... articles);

    @Insert
    void insertSync(Article... articles);

    @Query("DELETE FROM articles")
    void deleteAllSync();
}
