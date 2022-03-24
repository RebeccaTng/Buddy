package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import be.kuleuven.buddy.R;

public class PlantSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_settings);

        ImageView selectIcon = findViewById(R.id.selectIcon);
        EditText customTextFill = findViewById(R.id.customTextFill);

        Switch dispSwitch = findViewById(R.id.dispSwitch);
        dispSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked) {
                selectIcon.setRotation(90);
                customTextFill.setVisibility(View.VISIBLE);
            } else {
                selectIcon.setRotation(0);
                customTextFill.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void goBack(View caller) {
        Intent goToHome = new Intent(this, Home.class);
        startActivity(goToHome);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goPlantStatistics(View caller) {
        Intent goToPlantStatistics = new Intent(this, PlantStatistics.class);
        startActivity(goToPlantStatistics);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}