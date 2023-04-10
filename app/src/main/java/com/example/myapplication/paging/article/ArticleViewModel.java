package com.example.myapplication.paging.article;

import androidx.lifecycle.ViewModel;

import com.example.myapplication.database.article.Article;
import com.example.myapplication.database.article.ArticleDao;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ArticleViewModel extends ViewModel {
    private final ArticleDao articleDao;

    public ArticleViewModel(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    public Single<List<Article>> getArticles() {
        return articleDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
