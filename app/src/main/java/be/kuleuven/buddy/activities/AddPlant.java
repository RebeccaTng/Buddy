package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.cards.LibraryAdapter;
import be.kuleuven.buddy.cards.LibraryInfo;

public class AddPlant extends AppCompatActivity implements LibraryAdapter.LibraryListener {

    RecyclerView libraryRecycler;
    RecyclerView.Adapter libraryAdapter;
    AccountInfo accountInfo;

    // TODO link with information from database
    // Array with information to be displayed
    ArrayList<LibraryInfo> libPlants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_add_plant);

        libraryRecycler = findViewById(R.id.addPlant_recycler);
        libraryRecycler();

        TextView libNumber = findViewById(R.id.dyn_libNumber);
        libNumber.setText(String.valueOf(libraryAdapter.getItemCount()));

        if(getIntent().hasExtra("accountInfo")) accountInfo = getIntent().getExtras().getParcelable("accountInfo");
    }

    private void libraryRecycler() {
        libraryRecycler.setHasFixedSize(true);
        libraryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Vertical layout by default

        // Dummy info
        libPlants.add(new LibraryInfo(1, R.drawable.plant_image, "Aloe Vera"));
        libPlants.add(new LibraryInfo(2, R.drawable.plant_image2, "Peperomia"));
        libPlants.add(new LibraryInfo(3, R.drawable.dead, "Just a dead plant..."));

        libraryAdapter = new LibraryAdapter(libPlants, this);
        libraryRecycler.setAdapter(libraryAdapter);
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goInfo(View caller) {
        Intent goToInfo = new Intent(this, Info.class);
        startActivity(goToInfo);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goAddManual(View caller) {
        Intent goToAddManual = new Intent(this, AddManual.class);
        goToAddManual.putExtra("accountInfo", accountInfo);
        startActivity(goToAddManual);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void onCardClick(int position) {
        Intent goToAddLibrary = new Intent(this, AddLibrary.class);
        goToAddLibrary.putExtra("libPlant", libPlants.get(position));
        startActivity(goToAddLibrary);
    }
}