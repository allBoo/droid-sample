package com.example.myapplication.network;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.LoadType;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxRemoteMediator;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.group.Group;
import com.example.myapplication.database.group.GroupDao;
import com.example.myapplication.database.student.Student;
import com.example.myapplication.database.student.StudentDao;

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
public class StudentRemoteMediator extends RxRemoteMediator<Integer, Student> {
    private static final int LIMIT = 10;

    private final AtomicBoolean isEnd = new AtomicBoolean(false);

    private final StudentApi studentApi;
    private final AppDatabase database;
    private final StudentDao studentDao;
    private final GroupDao groupDao;

    public StudentRemoteMediator(StudentApi studentApi, AppDatabase database) {
        this.studentApi = studentApi;
        this.database = database;
        this.studentDao = database.studentDao();
        this.groupDao = database.groupDao();
    }

    @NonNull
    @Override
    public Single<MediatorResult> loadSingle(@NonNull LoadType loadType,
                                             @NonNull PagingState<Integer, Student> pagingState) {
        switch (loadType) {
            case REFRESH:
                isEnd.set(false);
                break;
            case APPEND:
                break;
            case PREPEND:
                return Single.just(new MediatorResult.Success(true));
        }

        return studentApi.getGroups()
                .subscribeOn(Schedulers.io())
                .flatMap((Function<List<Group>, ObservableSource<List<Student>>>) groupResponse -> {
                    final AtomicInteger page = new AtomicInteger(1);
                    database.runInTransaction(() -> {
                        if (loadType == LoadType.REFRESH) {
                            groupDao.deleteAllSync();
                            groupDao.insertSync(groupResponse.toArray(new Group[0]));
                        }
                        if (loadType == LoadType.APPEND) {
                            page.set(studentDao.getStudentsCount().blockingGet() / LIMIT + 1);
                        }
                        if (isEnd.get()) {
                            page.getAndIncrement();
                        }
                    });
                    return studentApi.getStudents(page.get(), LIMIT);
                })
                .map((Function<List<Student>, MediatorResult>) response -> {
                    database.runInTransaction(() -> {
                        if (loadType == LoadType.REFRESH) {
                            studentDao.delete(response.toArray(new Student[0])).blockingAwait();
                        }
                        studentDao.insert(response.toArray(new Student[0])).blockingAwait();
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

    public Observable<Student> create(Student student) {
        return studentApi.createStudent(student);
    }

    public Observable<Student> update(Student student) {
        return studentApi.updateStudent(student.getId(), student);
    }

    public Observable<Student> delete(Student student) {
        return studentApi.deleteStudent(student.getId());
    }
}
