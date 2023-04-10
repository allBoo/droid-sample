package com.example.myapplication.database.fine;

import androidx.paging.rxjava3.RxPagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface FineDao {
    @Query("select * from fines order by datetime(issue_date_time) desc")
    RxPagingSource<Integer, Fine> getAll();

    @Query("select count(*) from fines")
    Single<Integer> getFinesCount();

    @Insert
    Completable insert(Fine... fines);

    @Update
    Completable update(Fine fine);

    @Delete
    Completable delete(Fine... fines);
}
