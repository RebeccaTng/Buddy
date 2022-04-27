package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.cards.LibraryInfo;
import be.kuleuven.buddy.other.InputFilterMinMax;

public class AddLibrary extends AppCompatActivity {

    LibraryInfo plant;
    EditText moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageMonths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_add_library);

        ImageView plantImage = findViewById(R.id.plantImage_addLib);
        TextView plantSpecies = findViewById(R.id.plantSpecies_addLib);
        moistMin = findViewById(R.id.moistMin_addLib);
        moistMax = findViewById(R.id.moistMax_addLib);
        lightMin = findViewById(R.id.lightMin_addLib);
        lightMax = findViewById(R.id.lightMax_addLib);
        tempMin = findViewById(R.id.tempMin_addLib);
        tempMax = findViewById(R.id.tempMax_addLib);
        waterlvl = findViewById(R.id.waterMin_addLib);
        ageMonths = findViewById(R.id.ageMonth_addLib);

        // Set minimum and maximum value
        moistMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        moistMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        lightMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        lightMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        tempMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        tempMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        waterlvl.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        ageMonths.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "12")});

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

    // TODO go to addPlant after delete btn
}