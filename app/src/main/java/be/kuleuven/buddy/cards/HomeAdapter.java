package be.kuleuven.buddy.cards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import be.kuleuven.buddy.R;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    ArrayList<HomeInfo> homePlants; // Contains the info/text

    public HomeAdapter(ArrayList<HomeInfo> homePlants) {
        this.homePlants = homePlants;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Pass design to ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        // Bind information
        HomeInfo homeInfo = homePlants.get(position);

        holder.plantImage.setImageResource(homeInfo.getPlantImage());
        holder.plantName.setText(homeInfo.getPlantName());
        holder.plantSpecies.setText(homeInfo.getPlantSpecies());
        holder.plantWater.setText(homeInfo.getPlantWater());
        holder.plantPlace.setText(homeInfo.getPlantPlace());
        holder.plantStatus.setText(homeInfo.getPlantStatus());
    }

    @Override
    public int getItemCount() {
        return homePlants.size();
    }

    // Design
    public static class HomeViewHolder extends RecyclerView.ViewHolder {

        ImageView plantImage;
        TextView plantName, plantSpecies, plantWater, plantPlace, plantStatus;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);

            plantImage = itemView.findViewById(R.id.dyn_plantImage);
            plantName = itemView.findViewById(R.id.dyn_plantName);
            plantSpecies = itemView.findViewById(R.id.dyn_plantSpecies);
            plantWater = itemView.findViewById(R.id.dyn_plantWater);
            plantPlace = itemView.findViewById(R.id.dyn_plantPlace);
            plantStatus = itemView.findViewById(R.id.dyn_plantStatus);
        }
    }
}
