package com.example.myapplication.paging.student;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.example.myapplication.database.student.Student;
import com.example.myapplication.databinding.StudentItemBinding;

public class StudentAdapter extends PagingDataAdapter<Student, StudentViewHolder> {
    public interface OnClickListener {
        void onClick(int position, Student data);
    }

    private final OnClickListener clickListener;

    public StudentAdapter(@NonNull DiffUtil.ItemCallback<Student> diffCallback,
                          OnClickListener clickListener) {
        super(diffCallback);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final StudentItemBinding binding = StudentItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StudentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        final Student student = getItem(position);
        holder.getBinding().setStudent(student);
        holder.itemView.setOnClickListener((view) -> clickListener.onClick(position, student));
    }
}
