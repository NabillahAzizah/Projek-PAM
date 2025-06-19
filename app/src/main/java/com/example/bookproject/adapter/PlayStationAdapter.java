//PlayStationAdapter.java
package com.example.bookproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookproject.MainActivity;
import com.example.bookproject.R;

import java.util.List;

public class PlayStationAdapter extends RecyclerView.Adapter<PlayStationAdapter.PlayStationViewHolder> {

    public static final int COLOR_ORANGE = 0;
    public static final int COLOR_MINT = 1;

    private List<MainActivity.PlayStation> playstationList;
    private OnPlayStationClickListener listener;

    public interface OnPlayStationClickListener {
        void onPlayStationClick(MainActivity.PlayStation playStation);
    }

    public PlayStationAdapter(List<MainActivity.PlayStation> playstationList, OnPlayStationClickListener listener) {
        this.playstationList = playstationList;
        this.listener = listener;
    }

    // Logika untuk selang-seling layout seperti partner
    @Override
    public int getItemViewType(int position) {
        return position % 2; // 0 = orange (bg_item), 1 = mint (bg_item2)
    }

    @NonNull
    @Override
    public PlayStationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == 0) {
            // Orange background - bg_item.png
            view = inflater.inflate(R.layout.item_playstation_card, parent, false);
        } else {
            // Mint background - bg_item2.png
            view = inflater.inflate(R.layout.item_playstation_card_mint, parent, false);
        }

        return new PlayStationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayStationViewHolder holder, int position) {
        MainActivity.PlayStation playstation = playstationList.get(position);
        holder.bind(playstation);
    }

    @Override
    public int getItemCount() {
        return playstationList.size();
    }

    class PlayStationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPlaystationName;
        private LinearLayout psLayout;

        public PlayStationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaystationName = itemView.findViewById(R.id.tv_name);
            psLayout = itemView.findViewById(R.id.ps_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onPlayStationClick(playstationList.get(position));
                    }
                }
            });
        }

        public void bind(MainActivity.PlayStation playstation) {
            tvPlaystationName.setText(playstation.getName());

            // Background sudah diatur di layout XML masing-masing
            // Orange card menggunakan bg_item, Mint card menggunakan bg_item2

            // Change appearance based on status
            if (playstation.getStatus().equals("Occupied")) {
                itemView.setAlpha(0.6f);
                itemView.setEnabled(false);
            } else {
                itemView.setAlpha(1.0f);
                itemView.setEnabled(true);
            }
        }
    }

    // Method to update PlayStation status
    public void updatePlayStationStatus(int playstationId, String newStatus) {
        for (int i = 0; i < playstationList.size(); i++) {
            if (playstationList.get(i).getId() == playstationId) {
                playstationList.get(i).setStatus(newStatus);
                notifyItemChanged(i);
                break;
            }
        }
    }
}