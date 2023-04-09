package com.example.myapplication.paging.student;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.StudentItemBinding;

public class StudentViewHolder extends RecyclerView.ViewHolder {
    private final StudentItemBinding binding;

    public StudentViewHolder(@NonNull StudentItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public StudentItemBinding getBinding() {
        return binding;
    }
}
