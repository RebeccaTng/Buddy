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
import be.kuleuven.buddy.cards.HomeAdapter;
import be.kuleuven.buddy.cards.HomeInfo;

public class Home extends AppCompatActivity implements HomeAdapter.HomeListener {

    RecyclerView homeRecycler;
    RecyclerView.Adapter homeAdapter;
    TextView username;
    AccountInfo account;

    // TODO link with information from database
    // Array with information to be displayed
    ArrayList<HomeInfo> homePlants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeRecycler = findViewById(R.id.home_recycler);
        homeRecycler();

        TextView numOfPlants = findViewById(R.id.dyn_numOfPlants);
        numOfPlants.setText(String.valueOf(homeAdapter.getItemCount()));

        username = findViewById(R.id.dyn_username_home);

        if(getIntent().hasExtra("account")) {
            account = getIntent().getExtras().getParcelable("account");
            username.setText(account.getUsername());
        }
    }

    private void homeRecycler() {
        homeRecycler.setHasFixedSize(true);
        homeRecycler.setLayoutManager(new LinearLayoutManager(this)); // Vertical layout by default

        // Dummy info
        homePlants.add(new HomeInfo(1, R.drawable.plant_image, "Planty", "Aloe Vera", "42 min", "Bedroom", "Please refill the tank!"));
        homePlants.add(new HomeInfo(2, R.drawable.plant_image2, "This", "Cactus", "Being watered now", "Bedroom", "I'm so happy!"));
        homePlants.add(new HomeInfo(3, R.drawable.plant_image, "Is", "Peperomia", "1 hour, 3 min", "Kitchen", "It's too warm here!"));
        homePlants.add(new HomeInfo(4, R.drawable.plant_image2, "Very", "Aloe Vera", "2 weeks, 5 days", "Living Room", "I'm freezing!"));
        homePlants.add(new HomeInfo(5, R.drawable.plant_image, "Cool", "Snake Plant", "1 week, 1 day", "Bedroom 2", "Just growing, what about you?"));
        homePlants.add(new HomeInfo(6, R.drawable.dead, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH", "I don't know anything about plants dude", "Already dead", "Room under the stairs", "Yes everything is okay, this is pure for testing purposes."));

        homeAdapter = new HomeAdapter(homePlants, this);
        homeRecycler.setAdapter(homeAdapter);
    }

    public void goInfo(View caller) {
        Intent goToInfo = new Intent(this, Info.class);
        startActivity(goToInfo);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goAccount(View caller) {
        Intent goToAccount = new Intent(this, Account.class);
        goToAccount.putExtra("account", account);
        startActivity(goToAccount);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goAddPlant(View caller) {
        Intent goToAddPlant = new Intent(this, AddPlant.class);
        startActivity(goToAddPlant);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void onCardClick(int position) {
        Intent goToPlantStatistics  = new Intent(this, PlantStatistics.class);
        goToPlantStatistics.putExtra("plantName", homePlants.get(position));
        startActivity(goToPlantStatistics);
    }
}