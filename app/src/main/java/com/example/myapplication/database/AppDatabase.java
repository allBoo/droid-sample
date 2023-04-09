package com.example.myapplication.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myapplication.database.group.Group;
import com.example.myapplication.database.group.GroupDao;
import com.example.myapplication.database.student.Student;
import com.example.myapplication.database.student.StudentDao;

@Database(entities = {Student.class, Group.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StudentDao studentDao();

    public abstract GroupDao groupDao();
}
