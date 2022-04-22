package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.cards.HomeInfo;
import be.kuleuven.buddy.other.InputFilterMinMax;

public class PlantSettings extends AppCompatActivity {

    AccountInfo accountInfo;
    HomeInfo plant;
    EditText moistMin, moistMax, lightMin, lightMax, tempMin, tempMax, waterlvl, ageMonths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_plant_settings);

        Switch dispSwitch = findViewById(R.id.dispSwitch);
        ImageView selectIcon = findViewById(R.id.selectIcon);
        EditText customTextFill = findViewById(R.id.customTextFill);
        ImageView editIcon = findViewById(R.id.editIcon);
        EditText name = findViewById(R.id.dyn_plantName_settings);
        ImageView image = findViewById(R.id.dyn_plantImage_settings);
        TextView species = findViewById(R.id.dyn_plantSpecies_settings);
        moistMin = findViewById(R.id.moistMin_settings);
        moistMax = findViewById(R.id.moistMax_settings);
        lightMin = findViewById(R.id.lightMin_settings);
        lightMax = findViewById(R.id.lightMax_settings);
        tempMin = findViewById(R.id.tempMin_settings);
        tempMax = findViewById(R.id.tempMax_settings);
        waterlvl = findViewById(R.id.waterMin_settings);
        ageMonths = findViewById(R.id.ageMonth_settings);

        // Set minimum and maximum value
        moistMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        moistMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        lightMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        lightMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        tempMin.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        tempMax.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        waterlvl.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        ageMonths.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "12")});

        if(getIntent().hasExtra("accountInfo")) {
            accountInfo = getIntent().getExtras().getParcelable("accountInfo");
        }

        if(getIntent().hasExtra("plant")) {
            plant = getIntent().getExtras().getParcelable("plant");
            image.setImageResource(plant.getPlantImage());
            name.setText(plant.getPlantName());
            species.setText(plant.getPlantSpecies());
        }

        editIcon.setOnClickListener(view -> {
            name.requestFocus();
            name.setSelection(name.getText().length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
        });

        dispSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked) {
                selectIcon.setRotation(90);
                customTextFill.setVisibility(View.VISIBLE);
            } else {
                selectIcon.setRotation(0);
                customTextFill.setVisibility(View.INVISIBLE);
            }
        });

        selectIcon.setOnClickListener(view -> {
            if(dispSwitch.isChecked()) {
                selectIcon.setRotation(0);
                customTextFill.setVisibility(View.INVISIBLE);
                dispSwitch.setChecked(false);
            } else {
                selectIcon.setRotation(90);
                customTextFill.setVisibility(View.VISIBLE);
                dispSwitch.setChecked(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent goToHome = new Intent(this, Home.class);
        goToHome.putExtra("accountInfo", accountInfo);
        startActivity(goToHome);
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goPlantStatistics(View caller) {
        Intent goToPlantStatistics = new Intent(this, PlantStatistics.class);
        goToPlantStatistics.putExtra("plant", plant);
        goToPlantStatistics.putExtra("accountInfo", accountInfo);
        startActivity(goToPlantStatistics);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    // TODO goToPlantStatistics after clicking on save
    // TODO goToHome after clicking delete
}