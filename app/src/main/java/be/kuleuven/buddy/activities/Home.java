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
import be.kuleuven.buddy.cards.HomeAdapter;
import be.kuleuven.buddy.cards.HomeInfo;

public class Home extends AppCompatActivity {

    RecyclerView homeRecycler;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeRecycler = findViewById(R.id.home_recycler);
        homeRecycler();
    }

    private void homeRecycler() {
        homeRecycler.setHasFixedSize(true);
        homeRecycler.setLayoutManager(new LinearLayoutManager(this)); // Vertical layout by default

        // TODO link with information from database
        // Array with information to be displayed
        ArrayList<HomeInfo> homePlants = new ArrayList<>();

        // Dummy info
        homePlants.add(new HomeInfo(R.drawable.plant_image, "Planty", "Aloe Vera", "42 min", "Bedroom", "Please refill the tank!"));
        homePlants.add(new HomeInfo(R.drawable.plant_image2, "This", "Cactus", "Being watered now", "Bedroom", "I'm so happy!"));
        homePlants.add(new HomeInfo(R.drawable.plant_image, "Is", "Peperomia", "1 hour, 3 min", "Kitchen", "It's too warm here!"));
        homePlants.add(new HomeInfo(R.drawable.plant_image2, "Very", "Aloe Vera", "2 weeks, 5 days", "Living Room", "I'm freezing!"));
        homePlants.add(new HomeInfo(R.drawable.plant_image, "Cool", "Snake Plant", "1 week, 1 day", "Bedroom 2", "Just growing, what about you?"));
        homePlants.add(new HomeInfo(R.drawable.plant_image, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH", "I don't know anything about plants dude", "Already dead", "Room under the stairs", "Yes everything is okay, this is pure for testing purposes."));

        adapter = new HomeAdapter(homePlants);
        homeRecycler.setAdapter(adapter);
    }

    public void goInfo(View caller) {
        Intent goToInfo = new Intent(this, Info.class);
        startActivity(goToInfo);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void goAccount(View caller) {
        Intent goToAccount = new Intent(this, Account.class);
        startActivity(goToAccount);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}