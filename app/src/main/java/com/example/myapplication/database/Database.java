package com.example.myapplication.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myapplication.database.article.Article;
import com.example.myapplication.database.fine.Fine;

import java.util.concurrent.Executors;

public final class Database {
    private static final String DB_NAME = "fines-demo";
    private static volatile AppDatabase database;

    private Database() {
    }

    private static void populateDatabase(Context applicationContext) {
        Article article1 = new Article(1, "p.18.16 q.1");
        Article article2 = new Article(2, "p.18.16 q.2");
        getDatabase(applicationContext).articleDao().insert(
                article1, article2
        ).subscribe().dispose();
        getDatabase(applicationContext).fineDao().insert(
                new Fine("A555AT73", "2023-03-17T07:23:19.120+00:00",
                        article1, 80000, "email1@mail.ru"),
                new Fine("A555AT73", "2023-03-18T15:44:55.155+00:00",
                        article1, 100000, "email2@mail.ru"),
                new Fine("X123XA99", "2023-04-01T12:51:19.198+00:00",
                        article2, 150000, "email3@mail.ru")
        ).subscribe().dispose();
        Log.d(Database.class.getSimpleName(), "Populated");
    }

    public static AppDatabase getDatabase(Context applicationContext) {
        if (database == null) {
            synchronized (Database.class) {
                if (database == null) {
                    RoomDatabase.Callback populateCallback = new RoomDatabase.Callback() {
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor()
                                    .execute(() -> populateDatabase(applicationContext));
                        }
                    };
                    database = Room
                            .databaseBuilder(applicationContext, AppDatabase.class, DB_NAME)
//                            .fallbackToDestructiveMigration()
//                            .addCallback(populateCallback)
                            .build();
                }
            }
        }
        return database;
    }
}
