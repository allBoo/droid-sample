package com.example.myapplication.paging;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.Database;
import com.example.myapplication.database.group.GroupDao;
import com.example.myapplication.database.student.StudentDao;
import com.example.myapplication.network.StudentApi;
import com.example.myapplication.network.StudentRemoteMediator;
import com.example.myapplication.network.StudentService;
import com.example.myapplication.paging.group.GroupViewModel;
import com.example.myapplication.paging.student.StudentViewModel;

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
        if (modelClass.isAssignableFrom(StudentViewModel.class)) {
            final StudentDao studentDao = database.studentDao();
            final StudentApi studentApi = StudentService.getStudentApi();
            final StudentRemoteMediator studentRemoteMediator =
                    new StudentRemoteMediator(studentApi, database);
            return (T) new StudentViewModel(studentDao, studentRemoteMediator);
        }
        if (modelClass.isAssignableFrom(GroupViewModel.class)) {
            final GroupDao groupDao = database.groupDao();
            return (T) new GroupViewModel(groupDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
