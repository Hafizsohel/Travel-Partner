package com.example.travelpartner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelpartner.R;
import com.example.travelpartner.model.PlaceModel;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private Context context;
    private List<PlaceModel> placeList;

    public PlaceAdapter(Context context, List<PlaceModel> placeList) {
        this.context = context;
        this.placeList = new ArrayList<>();
    }

    public void setPlaceList(List<PlaceModel> placeList) {
        this.placeList = placeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.see_all_places, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        PlaceModel place = placeList.get(position);
        holder.name.setText(place.getName());
        holder.location.setText(place.getLocation());

        Glide.with(context)
                .load(place.getImg_url())
                .placeholder(R.drawable.place)
                .error(R.drawable.loading_error)
                .into(holder.placeImage);
    }
    @Override
    public int getItemCount() {
        return placeList.size();
    }
    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView name, location;
        ImageView placeImage;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.all_place_name);
            location = itemView.findViewById(R.id.place_location);
            placeImage = itemView.findViewById(R.id.all_img);
        }
    }
}
