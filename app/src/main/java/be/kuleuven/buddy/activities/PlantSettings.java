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
import be.kuleuven.buddy.account.AccountInfo;

public class PlantSettings extends AppCompatActivity {

    AccountInfo account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_plant_settings);

        ImageView selectIcon = findViewById(R.id.selectIcon);
        EditText customTextFill = findViewById(R.id.customTextFill);

        if(getIntent().hasExtra("account")) {
            account = getIntent().getExtras().getParcelable("account");
        }

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

    public void goBack(View caller) {
        Intent goToHome = new Intent(this, Home.class);
        goToHome.putExtra("account", account);
        startActivity(goToHome);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void goPlantStatistics(View caller) {
        Intent goToPlantStatistics = new Intent(this, PlantStatistics.class);
        goToPlantStatistics.putExtra("account", account);
        startActivity(goToPlantStatistics);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}