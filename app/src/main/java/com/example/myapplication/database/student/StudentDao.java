package com.example.myapplication.database.student;

import androidx.paging.rxjava3.RxPagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface StudentDao {
    @Query("select * from students order by last_name collate nocase asc")
    RxPagingSource<Integer, Student> getAll();

    @Query("select count(*) from students")
    Single<Integer> getStudentsCount();

    @Insert
    Completable insert(Student... students);

    @Update
    Completable update(Student student);

    @Delete
    Completable delete(Student... students);
}
