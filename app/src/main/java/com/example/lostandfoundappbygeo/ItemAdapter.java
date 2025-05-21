package com.example.lostandfoundappbygeo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private Context context;

    public ItemAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        String title = item.getType() + " " + item.getName();
        holder.tvItemTitle.setText(title);

        holder.tvItemLocation.setText(item.getLocation());
        holder.tvItemDate.setText(item.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra("item_id", item.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateData(List<Item> newItems) {
        this.itemList = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemTitle;
        TextView tvItemLocation;
        TextView tvItemDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemLocation = itemView.findViewById(R.id.tvItemLocation);
            tvItemDate = itemView.findViewById(R.id.tvItemDate);
        }
    }
}
