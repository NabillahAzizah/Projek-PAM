//PlayStationAdapter.java
package com.example.bookproject;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

    @NonNull
    @Override
    public PlayStationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playstation_card, parent, false);
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
        private RelativeLayout cardBackground;
        private CardView cardView;

        public PlayStationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaystationName = itemView.findViewById(R.id.tv_playstation_name);
            cardBackground = itemView.findViewById(R.id.card_background);
            cardView = (CardView) itemView;

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

            // Set background color based on card type
            GradientDrawable gradient = new GradientDrawable();
            gradient.setCornerRadius(32f); // 12dp converted to pixels approximately

            if (playstation.getColorType() == COLOR_ORANGE) {
                // Orange gradient like Figma
                gradient.setColors(new int[]{
                        Color.parseColor("#FCD34D"), // Light yellow/orange
                        Color.parseColor("#F59E0B")  // Darker orange
                });
            } else {
                // Mint/Turquoise gradient like Figma
                gradient.setColors(new int[]{
                        Color.parseColor("#A7F3D0"), // Light mint
                        Color.parseColor("#34D399")  // Darker mint/green
                });
            }

            gradient.setOrientation(GradientDrawable.Orientation.TL_BR);
            cardBackground.setBackground(gradient);

            // Change card appearance based on status
            if (playstation.getStatus().equals("Occupied")) {
                itemView.setAlpha(0.6f);
                cardView.setCardElevation(2f);
                itemView.setEnabled(false);
            } else {
                itemView.setAlpha(1.0f);
                cardView.setCardElevation(8f);
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