package com.example.myapplication.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myapplication.database.group.Group;
import com.example.myapplication.database.student.Student;

import java.util.concurrent.Executors;

public final class Database {
    private static final String DB_NAME = "pmu-demo";
    private static volatile AppDatabase database;

    private Database() {
    }

    private static void populateDatabase(Context applicationContext) {
        Group group1 = new Group(1, "Group1");
        Group group2 = new Group(2, "Group2");
        getDatabase(applicationContext).groupDao().insert(
                group1, group2
        ).subscribe().dispose();
        getDatabase(applicationContext).studentDao().insert(
                new Student("First1", "Last1", group1,
                        "+79998887766", "email1@mail.ru"),
                new Student("First2", "Last2", group1,
                        "+79995553322", "email2@mail.ru"),
                new Student("First3", "Last3", group2,
                        "+79991114466", "email3@mail.ru")
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
//                            .addCallback(populateCallback)
                            .build();
                }
            }
        }
        return database;
    }
}
