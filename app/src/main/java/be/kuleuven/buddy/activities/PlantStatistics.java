package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.cards.HomeInfo;

public class PlantStatistics extends AppCompatActivity {

    AccountInfo account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_plant_statistics);

        ImageView image = findViewById(R.id.dyn_plantImage_stat);
        TextView name = findViewById(R.id.dyn_plantName_stat);
        TextView species = findViewById(R.id.dyn_plantSpecies_stat);
        TextView water = findViewById(R.id.dyn_plantWater_stat);
        TextView place = findViewById(R.id.dyn_plantPlace_stat);
        TextView status = findViewById(R.id.dyn_plantStatus_stat);

        if(getIntent().hasExtra("account")) {
            account = getIntent().getExtras().getParcelable("account");
        }

        if(getIntent().hasExtra("plantName")) {
            HomeInfo plantName = getIntent().getExtras().getParcelable("plantName");
            image.setImageResource(plantName.getPlantImage());
            name.setText(plantName.getPlantName());
            species.setText(plantName.getPlantSpecies());
            water.setText(plantName.getPlantWater());
            place.setText(plantName.getPlantPlace());
            status.setText(plantName.getPlantStatus());
        }
    }

    public void goBack(View caller) {
        Intent goToHome = new Intent(this, Home.class);
        goToHome.putExtra("account", account);
        startActivity(goToHome);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goPlantSettings(View caller) {
        Intent goToPlantSettings = new Intent(this, PlantSettings.class);
        goToPlantSettings.putExtra("account", account);
        startActivity(goToPlantSettings);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}