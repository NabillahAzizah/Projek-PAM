package com.example.bookproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookproject.BookingZoneActivity;
import com.example.bookproject.R;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<BookingZoneActivity.TimeSlot> timeSlotList;
    private OnTimeSlotClickListener listener;
    private int selectedPosition = -1;

    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(BookingZoneActivity.TimeSlot timeSlot);
    }

    public TimeSlotAdapter(List<BookingZoneActivity.TimeSlot> timeSlotList, OnTimeSlotClickListener listener) {
        this.timeSlotList = timeSlotList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        BookingZoneActivity.TimeSlot timeSlot = timeSlotList.get(position);
        holder.bind(timeSlot, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }

    class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSessionNumber;
        private TextView tvTimeRange;
        private TextView tvDuration;
        private CardView cardView;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSessionNumber = itemView.findViewById(R.id.tv_session_number);
            tvTimeRange = itemView.findViewById(R.id.tv_time_range);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            cardView = (CardView) itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        // Update selected position
                        int previousSelected = selectedPosition;
                        selectedPosition = position;

                        // Notify changes
                        if (previousSelected != -1) {
                            notifyItemChanged(previousSelected);
                        }
                        notifyItemChanged(selectedPosition);

                        // Call listener
                        listener.onTimeSlotClick(timeSlotList.get(position));
                    }
                }
            });
        }

        public void bind(BookingZoneActivity.TimeSlot timeSlot, boolean isSelected) {
            tvSessionNumber.setText(String.valueOf(timeSlot.getSessionNumber()));
            tvTimeRange.setText(timeSlot.getTimeRange());
            tvDuration.setText(timeSlot.getDuration());

            // Change appearance based on selection
            if (isSelected) {
                cardView.setCardElevation(8f);
                itemView.setAlpha(1.0f);
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.purple_light));
                tvTimeRange.setTextColor(itemView.getContext().getResources().getColor(android.R.color.white));
            } else {
                cardView.setCardElevation(2f);
                itemView.setAlpha(0.9f);
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.white));
                tvTimeRange.setTextColor(itemView.getContext().getResources().getColor(R.color.purple_primary));
            }
        }
    }

    // Method to clear selection
    public void clearSelection() {
        int previousSelected = selectedPosition;
        selectedPosition = -1;
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected);
        }
    }

    // Method to get selected time slot
    public BookingZoneActivity.TimeSlot getSelectedTimeSlot() {
        if (selectedPosition != -1 && selectedPosition < timeSlotList.size()) {
            return timeSlotList.get(selectedPosition);
        }
        return null;
    }
}