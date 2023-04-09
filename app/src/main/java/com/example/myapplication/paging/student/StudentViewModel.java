package com.example.myapplication.paging.student;

import androidx.lifecycle.ViewModel;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.myapplication.database.student.Student;
import com.example.myapplication.database.student.StudentDao;
import com.example.myapplication.network.StudentRemoteMediator;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class StudentViewModel extends ViewModel {
    private final StudentDao studentDao;
    private final StudentRemoteMediator studentRemoteMediator;

    public StudentViewModel(StudentDao studentDao,
                            StudentRemoteMediator studentRemoteMediator) {
        this.studentDao = studentDao;
        this.studentRemoteMediator = studentRemoteMediator;
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

    public Flowable<PagingData<Student>> getStudents() {
        return PagingRx.getFlowable(new Pager<>(
                createPagingConfig(),
                null,
                studentRemoteMediator,
                studentDao::getAll));
    }

    public Completable insert(Student student) {
        return Completable.fromObservable(studentRemoteMediator.create(student))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable update(Student student) {
        return Completable.fromObservable(studentRemoteMediator.update(student))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable delete(Student student) {
        return Completable.fromObservable(studentRemoteMediator.delete(student))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
