package com.example.myapplication.paging.student;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.myapplication.database.student.Student;

import java.util.Objects;

public class StudentComparator extends DiffUtil.ItemCallback<Student> {
    @Override
    public boolean areItemsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
        return Objects.equals(oldItem, newItem);
    }
}
