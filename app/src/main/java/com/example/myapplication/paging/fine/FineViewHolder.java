package com.example.myapplication.paging.fine;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.FineItemBinding;

public class FineViewHolder extends RecyclerView.ViewHolder {
    private final FineItemBinding binding;

    public FineViewHolder(@NonNull FineItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public FineItemBinding getBinding() {
        return binding;
    }
}
