package com.example.garnerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PsAdapter extends RecyclerView.Adapter<PsAdapter.PsViewHolder> {

    private Context context;
    private List<String> psList;

    public PsAdapter(Context context, List<String> psList) {
        this.context = context;
        this.psList = psList;
    }

    // Logika untuk selang-seling layout
    @Override
    public int getItemViewType(int position) {
        return position % 2; // 0 = item_ps, 1 = item_ps_alt
    }

    @NonNull
    @Override
    public PsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if (viewType == 0) {
            view = inflater.inflate(R.layout.item_ps, parent, false); // misalnya warna biru
        } else {
            view = inflater.inflate(R.layout.item_ps2, parent, false); // misalnya warna oranye
        }

        return new PsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PsViewHolder holder, int position) {
        String psName = psList.get(position);
        holder.tvName.setText(psName);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailPsActivity.class);
            intent.putExtra("ps_name", psName);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return psList.size();
    }

    public static class PsViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public PsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
