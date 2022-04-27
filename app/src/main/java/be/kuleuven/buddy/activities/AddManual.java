package be.kuleuven.buddy.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.other.FieldChecker;

public class AddManual extends AppCompatActivity {

    AppCompatButton addPic;
    Button addPlant;
    ImageView picPreview;
    ActivityResultLauncher<String> getImage;
    EditText moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageYears, ageMonths, place, name, species;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_add_manual);

        addPic = findViewById(R.id.addPicBtn);
        picPreview = findViewById(R.id.picturePreview);
        addPlant = findViewById(R.id.addNewPlantBtn);
        moistMin = findViewById(R.id.moistMin_addManual);
        moistMax = findViewById(R.id.moistMax_addManual);
        lightMin = findViewById(R.id.lightMin_addManual);
        lightMax = findViewById(R.id.lightMax_addManual);
        tempMin = findViewById(R.id.tempMin_addManual);
        tempMax = findViewById(R.id.tempMax_addManual);
        waterlvl = findViewById(R.id.waterMin_addManual);
        ageYears = findViewById(R.id.ageYear_addManual);
        ageMonths = findViewById(R.id.ageMonth_addManual);
        place = findViewById(R.id.placeName_addManual);
        name = findViewById(R.id.nameName_addManual);
        species = findViewById(R.id.speciesName_addManual);
        error = findViewById(R.id.error_addManual);

        // Set minimum and maximum value
        FieldChecker fieldChecker = new FieldChecker(moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageYears, ageMonths, place , name, species, error);
        fieldChecker.setFilters();

        // Get image from library and set in preview
        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            picPreview.setImageURI(result);
            picPreview.setVisibility(View.VISIBLE);
        });

        addPic.setOnClickListener(view -> getImage.launch("image/*"));

        addPlant.setOnClickListener(view -> {
            if(fieldChecker.addPlant()) {
                Intent goToAddPlant = new Intent(this, AddPlant.class);
                startActivity(goToAddPlant);
                this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}