package com.example.myapplication.database.article;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "articles")
public class Article implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @NonNull
    private String name;

    public Article(@NonNull Integer id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    @Ignore
    public Article(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
