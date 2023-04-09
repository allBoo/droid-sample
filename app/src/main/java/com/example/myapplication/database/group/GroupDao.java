package com.example.myapplication.database.group;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface GroupDao {
    @Query("select * from groups")
    Single<List<Group>> getAll();

    @Insert
    Completable insert(Group... groups);

    @Insert
    void insertSync(Group... groups);

    @Query("DELETE FROM groups")
    void deleteAllSync();
}
