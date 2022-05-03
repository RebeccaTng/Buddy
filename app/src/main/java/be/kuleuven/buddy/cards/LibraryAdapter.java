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

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    ArrayList<LibraryInfo> libPlants; // Contains the info/text
    LibraryAdapter.LibraryListener libraryListener;

    public LibraryAdapter(ArrayList<LibraryInfo> libPlants, LibraryAdapter.LibraryListener libraryListener) {
        this.libPlants = libPlants;
        this.libraryListener = libraryListener;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Pass design to ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_library, parent, false);
        return new LibraryViewHolder(view, libraryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        // Bind information
        LibraryInfo libraryInfo = libPlants.get(position);

        holder.libImage.setImageBitmap(libraryInfo.getLibImage());
        holder.libSpecies.setText(libraryInfo.getLibSpecies());
    }

    @Override
    public int getItemCount() { return libPlants.size(); }

    // Design
    public static class LibraryViewHolder extends RecyclerView.ViewHolder {

        ImageView libImage;
        TextView libSpecies;
        LibraryListener libraryListener;

        public LibraryViewHolder(@NonNull View itemView, LibraryListener libraryListener) {
            super(itemView);

            libImage = itemView.findViewById(R.id.dyn_libImage);
            libSpecies = itemView.findViewById(R.id.dyn_libSpecies);

            this.libraryListener = libraryListener;
            itemView.setOnClickListener(view -> libraryListener.onCardClick(getAdapterPosition()));
        }
    }

    public interface LibraryListener { void onCardClick(int position); }
}
