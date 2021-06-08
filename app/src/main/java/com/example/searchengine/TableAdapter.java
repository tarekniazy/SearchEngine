package com.example.searchengine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private ArrayList<CellItem> mCellList;
    private OnItemClickListener mListiner;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListiner = listener;
    }

    public static class TableViewHolder extends  RecyclerView.ViewHolder {
        public TextView title;
        public TextView url;
        public TextView description;

        public TableViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            description = itemView.findViewById(R.id.textDescription);
            url = itemView.findViewById(R.id.textURL);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public TableAdapter(ArrayList<CellItem> cellList) {
        mCellList = cellList;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell, parent, false);
        TableViewHolder tvh = new TableViewHolder(v, mListiner);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        CellItem cellItem = mCellList.get(position);

        holder.title.setText(cellItem.getTitle());
        holder.url.setText(cellItem.getUrl());
        holder.description.setText(cellItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return mCellList.size();
    }
}
