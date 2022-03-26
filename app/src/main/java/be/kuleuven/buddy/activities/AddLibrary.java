package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.cards.LibraryInfo;

public class AddLibrary extends AppCompatActivity {

    LibraryInfo plant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_add_library);

        ImageView plantImage = findViewById(R.id.plantImage_addLib);
        TextView plantSpecies = findViewById(R.id.plantSpecies_addLib);

        if(getIntent().hasExtra("libPlant")) {
            plant = getIntent().getExtras().getParcelable("libPlant");
            plantImage.setImageResource(plant.getLibImage());
            plantSpecies.setText(plant.getLibSpecies());
        }
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}