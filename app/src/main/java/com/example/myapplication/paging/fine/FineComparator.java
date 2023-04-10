package com.example.myapplication.paging.fine;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.myapplication.database.fine.Fine;

import java.util.Objects;

public class FineComparator extends DiffUtil.ItemCallback<Fine> {
    @Override
    public boolean areItemsTheSame(@NonNull Fine oldItem, @NonNull Fine newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Fine oldItem, @NonNull Fine newItem) {
        return Objects.equals(oldItem, newItem);
    }
}
