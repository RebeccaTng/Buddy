package be.kuleuven.buddy.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import be.kuleuven.buddy.R;

public class AddManual extends AppCompatActivity {

    AppCompatButton addPic;
    ImageView picPreview;
    ActivityResultLauncher<String> getImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_add_manual);

        addPic = findViewById(R.id.addPicBtn);
        picPreview = findViewById(R.id.picturePreview);

        // Get image from library and set in preview
        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            picPreview.setImageURI(result);
            picPreview.setVisibility(View.VISIBLE);
        });

        addPic.setOnClickListener(view -> getImage.launch("image/*"));
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}