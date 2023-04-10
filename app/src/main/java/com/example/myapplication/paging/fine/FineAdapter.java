package com.example.myapplication.paging.fine;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.example.myapplication.database.fine.Fine;
import com.example.myapplication.databinding.FineItemBinding;

public class FineAdapter extends PagingDataAdapter<Fine, FineViewHolder> {
    public interface OnClickListener {
        void onClick(int position, Fine data);
    }

    private final OnClickListener clickListener;

    public FineAdapter(@NonNull DiffUtil.ItemCallback<Fine> diffCallback,
                       OnClickListener clickListener) {
        super(diffCallback);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public FineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final FineItemBinding binding = FineItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FineViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FineViewHolder holder, int position) {
        final Fine fine = getItem(position);
        holder.getBinding().setFine(fine);
        holder.itemView.setOnClickListener((view) -> clickListener.onClick(position, fine));
    }
}
