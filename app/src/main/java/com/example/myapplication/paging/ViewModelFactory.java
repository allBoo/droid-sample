package com.example.myapplication.paging;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.Database;
import com.example.myapplication.database.article.ArticleDao;
import com.example.myapplication.database.fine.FineDao;
import com.example.myapplication.network.FineApi;
import com.example.myapplication.network.FineRemoteMediator;
import com.example.myapplication.network.FineService;
import com.example.myapplication.paging.article.ArticleViewModel;
import com.example.myapplication.paging.fine.FineViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Context applicationContext;

    public ViewModelFactory(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        final AppDatabase database = Database.getDatabase(applicationContext);
        if (modelClass.isAssignableFrom(FineViewModel.class)) {
            final FineDao fineDao = database.fineDao();
            final FineApi fineApi = FineService.getFineApi();
            final FineRemoteMediator fineRemoteMediator =
                    new FineRemoteMediator(fineApi, database);
            return (T) new FineViewModel(fineDao, fineRemoteMediator);
        }
        if (modelClass.isAssignableFrom(ArticleViewModel.class)) {
            final ArticleDao articleDao = database.articleDao();
            return (T) new ArticleViewModel(articleDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
