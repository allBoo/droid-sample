package com.example.myapplication.paging.group;

import androidx.lifecycle.ViewModel;

import com.example.myapplication.database.group.Group;
import com.example.myapplication.database.group.GroupDao;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GroupViewModel extends ViewModel {
    private final GroupDao groupDao;

    public GroupViewModel(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public Single<List<Group>> getGroups() {
        return groupDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
