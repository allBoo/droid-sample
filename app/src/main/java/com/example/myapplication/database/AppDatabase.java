package com.example.myapplication.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myapplication.database.article.ArticleDao;
import com.example.myapplication.database.fine.FineDao;
import com.example.myapplication.database.article.Article;
import com.example.myapplication.database.fine.Fine;

@Database(entities = {Fine.class, Article.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FineDao fineDao();

    public abstract ArticleDao articleDao();
}
