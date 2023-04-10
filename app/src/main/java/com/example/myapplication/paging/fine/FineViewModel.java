package com.example.myapplication.paging.fine;

import androidx.lifecycle.ViewModel;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.myapplication.database.fine.Fine;
import com.example.myapplication.database.fine.FineDao;
import com.example.myapplication.network.FineRemoteMediator;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FineViewModel extends ViewModel {
    private final FineDao fineDao;
    private final FineRemoteMediator fineRemoteMediator;

    public FineViewModel(FineDao fineDao,
                         FineRemoteMediator fineRemoteMediator) {
        this.fineDao = fineDao;
        this.fineRemoteMediator = fineRemoteMediator;
    }

    private PagingConfig createPagingConfig() {
        int PAGE_SIZE = 10;
        boolean ENABLE_PLACEHOLDERS = true;
        return new PagingConfig(
                PAGE_SIZE,
                PAGE_SIZE,
                ENABLE_PLACEHOLDERS,
                PAGE_SIZE * 2,
                PAGE_SIZE + PAGE_SIZE * 2,
                Integer.MIN_VALUE);
    }

    public Flowable<PagingData<Fine>> getFines() {
        return PagingRx.getFlowable(new Pager<>(
                createPagingConfig(),
                null,
                fineRemoteMediator,
                fineDao::getAll));
    }

    public Completable insert(Fine fine) {
        return Completable.fromObservable(fineRemoteMediator.create(fine))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable update(Fine fine) {
        return Completable.fromObservable(fineRemoteMediator.update(fine))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable delete(Fine fine) {
        return Completable.fromObservable(fineRemoteMediator.delete(fine))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
