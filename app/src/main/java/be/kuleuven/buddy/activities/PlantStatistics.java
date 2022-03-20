package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.cards.HomeInfo;

public class PlantStatistics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_statistics);

        HomeInfo plantName = getIntent().getExtras().getParcelable("plantName");

        TextView textview = findViewById(R.id.textView);
        ImageView image = findViewById(R.id.imageView);
        textview.setText(plantName.getPlantName());
        image.setImageResource(plantName.getPlantImage());
    }
}