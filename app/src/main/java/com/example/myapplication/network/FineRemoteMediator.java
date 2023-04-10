package com.example.myapplication.network;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.LoadType;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxRemoteMediator;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.article.Article;
import com.example.myapplication.database.fine.Fine;
import com.example.myapplication.database.fine.FineDao;
import com.example.myapplication.database.article.ArticleDao;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

@OptIn(markerClass = ExperimentalPagingApi.class)
public class FineRemoteMediator extends RxRemoteMediator<Integer, Fine> {
    private static final int LIMIT = 10;

    private final AtomicBoolean isEnd = new AtomicBoolean(false);

    private final FineApi fineApi;
    private final AppDatabase database;
    private final FineDao fineDao;
    private final ArticleDao articleDao;

    public FineRemoteMediator(FineApi fineApi, AppDatabase database) {
        this.fineApi = fineApi;
        this.database = database;
        this.fineDao = database.fineDao();
        this.articleDao = database.articleDao();
    }

    @NonNull
    @Override
    public Single<MediatorResult> loadSingle(@NonNull LoadType loadType,
                                             @NonNull PagingState<Integer, Fine> pagingState) {
        switch (loadType) {
            case REFRESH:
                isEnd.set(false);
                break;
            case APPEND:
                break;
            case PREPEND:
                return Single.just(new MediatorResult.Success(true));
        }

        return fineApi.getArticles()
                .subscribeOn(Schedulers.io())
                .flatMap((Function<List<Article>, ObservableSource<List<Fine>>>) articleResponse -> {
                    final AtomicInteger page = new AtomicInteger(1);
                    database.runInTransaction(() -> {
                        if (loadType == LoadType.REFRESH) {
                            articleDao.deleteAllSync();
                            articleDao.insertSync(articleResponse.toArray(new Article[0]));
                        }
                        if (loadType == LoadType.APPEND) {
                            page.set(fineDao.getFinesCount().blockingGet() / LIMIT + 1);
                        }
                        if (isEnd.get()) {
                            page.getAndIncrement();
                        }
                    });
                    return fineApi.getFines(page.get(), LIMIT);
                })
                .map((Function<List<Fine>, MediatorResult>) response -> {
                    database.runInTransaction(() -> {
                        if (loadType == LoadType.REFRESH) {
                            fineDao.delete(response.toArray(new Fine[0])).blockingAwait();
                        }
                        fineDao.insert(response.toArray(new Fine[0])).blockingAwait();
                        isEnd.set(response.size() < LIMIT);
                    });
                    return new MediatorResult.Success(response.size() == 0);
                })
                .singleOrError()
                .onErrorResumeNext(e -> {
                    if (e instanceof IOException || e instanceof HttpException) {
                        return Single.just(new MediatorResult.Error(e));
                    }
                    return Single.error(e);
                });
    }

    public Observable<Fine> create(Fine fine) {
        return fineApi.createFine(fine);
    }

    public Observable<Fine> update(Fine fine) {
        return fineApi.updateFine(fine.getId(), fine);
    }

    public Observable<Fine> delete(Fine fine) {
        return fineApi.deleteFine(fine.getId());
    }
}
